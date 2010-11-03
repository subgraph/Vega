package com.subgraph.vega.internal.http.requests;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.HttpEntity;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EntityUtils;

/**
 * An entity wrapper which converts a non-repeatable streaming entity
 * into a repeatable entity by storing the data as it streams in from
 * a connection (or other streaming source).  When all streaming data has
 * been received, the entity behaves like a ByteArrayEntity. 
 * 
 *
 */
public class RepeatableStreamingEntity extends AbstractHttpEntity {
	private final static int BUFFER_SIZE = 2048;
	private boolean isStreaming = true;
	private long length;

	private HttpEntity bufferEntity;
	private volatile InputStream input;

	RepeatableStreamingEntity(InputStream input, long length, boolean isChunked, String contentType, String contentEncoding) {
		setChunked(isChunked);
		setContentType(contentType);
		setContentEncoding(contentEncoding);
		setActiveInputStream(input, length);
	}

	RepeatableStreamingEntity(HttpEntity originalEntity) throws IOException {
		copyEntityData(originalEntity);
		
		if(originalEntity.isStreaming()) 
			setActiveInputStream(originalEntity.getContent(), originalEntity.getContentLength());
		else 
			setActiveByteArrayEntity(EntityUtils.toByteArray(originalEntity));
	}

	private void copyEntityData(HttpEntity e) {
		setChunked(e.isChunked());
		if(e.getContentType() != null)
			setContentType(e.getContentType().getValue());
		if(e.getContentEncoding() != null)
			setContentEncoding(e.getContentType().getValue());
		length = e.getContentLength();
	}

	private void setActiveInputStream(InputStream input, long length) {
		if(length > Integer.MAX_VALUE)
			throw new IllegalArgumentException("HTTP entity is too large to be buffered in memory: "+ length);
		this.length = length;
		final int sz = (length <  0) ? (BUFFER_SIZE) : ((int) length);
		this.input = new CachingInputStream(input, sz);
		isStreaming = true;
	}

	private void setActiveByteArrayEntity(byte[] content) {
		ByteArrayEntity entity = new ByteArrayEntity(content);
		isStreaming = false;
		length = content.length;
		bufferEntity = entity;
		input = null;
	}

	@Override
	public boolean isRepeatable() {
		return true;
	}

	@Override
	public long getContentLength() {
		return length;
	}

	@Override
	public InputStream getContent() throws IOException {
		if(input != null)
			return input;
		else
			return bufferEntity.getContent();
	}

	@Override
	public void writeTo(OutputStream outstream) throws IOException {
		if(input == null) {
			bufferEntity.writeTo(outstream);
			return;
		}

		byte[] buffer = new byte[BUFFER_SIZE];
		int l;
		if(length < 0) {
			// consume until EOF
			while((l = input.read(buffer)) != -1) {
				try {
					outstream.write(buffer, 0, l);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			// consume no more than length
			long remaining = this.length;

			while(remaining > 0) {
				int sz = (remaining < BUFFER_SIZE) ? ((int) remaining) : (BUFFER_SIZE);
				l = input.read(buffer, 0, sz);
				if(l == -1)
					break;
				outstream.write(buffer, 0, l);
				remaining -= l;
			}
		}
		if(input != null) {
			input.close();
			input = null;
		}
	}

	@Override
	public boolean isStreaming() {
		return isStreaming;
	}

	@Override
	public void consumeContent() throws IOException {	
		if(input != null) {
			input.close();
			input = null;
		}
		if(bufferEntity != null)
			bufferEntity.consumeContent();
	}

	private class CachingInputStream extends InputStream {
		private final InputStream wrappedInput;
		private final ByteArrayBuffer buffer;
		private boolean eof;

		CachingInputStream(InputStream input, int bufferSize) {
			wrappedInput = input;
			buffer = new ByteArrayBuffer(bufferSize);
		}

		@Override
		public int read() throws IOException {
			if(eof)
				return -1;

			final int b = wrappedInput.read();
			if(b == -1) 
				processEOF();
			else
				buffer.append(b);
			return b;
		}
		
		public int read(byte[] b, int off, int len) throws IOException {
			if(eof)
				return -1;
			final int n = wrappedInput.read(b, off, len);
			if(n == -1)
				processEOF();
			else
				buffer.append(b, off, n);
			return n;
		}

		public void close() throws IOException {
			wrappedInput.close();
			if(!eof)
				setActiveByteArrayEntity(buffer.toByteArray());
		}

		private void processEOF() throws IOException {
			wrappedInput.close();
			eof = true;
			setActiveByteArrayEntity(buffer.toByteArray());
		}
		
	}
}
