/*******************************************************************************
 * Copyright (c) 2011 Subgraph.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Subgraph - initial API and implementation
 ******************************************************************************/
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
 */
public class RepeatableStreamingEntity extends AbstractHttpEntity {
	private final static int BUFFER_SIZE = 8192;
	private boolean isStreaming = true;
	private long length;
	private long maximumInputKilobytes = 0; // 0 = no maximum length
	private HttpEntity bufferEntity;
	private volatile InputStream input;

	/**
	 * @param consumeInput Boolean indicating whether to immediately consume all input into buffer. 
	 * @throws IOException 
	 */
	RepeatableStreamingEntity(InputStream input, long length, boolean consumeInput, boolean isChunked, String contentType, String contentEncoding) throws IOException {
		setChunked(isChunked);
		setContentType(contentType);
		setContentEncoding(contentEncoding);
		setActiveInputStream(input, length);
		if (consumeInput != false) {
			consumeAllInput();
		}
	}

	RepeatableStreamingEntity(HttpEntity originalEntity) throws IOException {
		copyEntityProperties(originalEntity);
		if(originalEntity.isStreaming()) 
			setActiveInputStream(originalEntity.getContent(), originalEntity.getContentLength());
		else 
			setActiveByteArrayEntity(EntityUtils.toByteArray(originalEntity));
	}

	void setMaximumInputKilobytes(int kb) {
		maximumInputKilobytes = kb;
	}

	private void copyEntityProperties(HttpEntity e) {
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

	private void consumeAllInput() throws IOException {
		int rv;
		if (length < 0) {
			while ((rv = input.read()) != -1);
		} else {
			int remaining = (int)this.length;
			byte[] buffer = new byte[BUFFER_SIZE];
			while (remaining > 0) {
				int sz = (remaining < BUFFER_SIZE) ? (remaining) : (BUFFER_SIZE);
				rv = input.read(buffer, 0, sz);
				if(rv == -1)
					break;
				remaining -= rv;
			}
		}
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
		
		try {
			if(length < 0) {
				// consume until EOF
				while((l = input.read(buffer)) != -1) {
					outstream.write(buffer, 0, l);
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
		} finally {
			if(input != null) {
				input.close();
				input = null;
			}
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
		if(bufferEntity != null) {
			EntityUtils.consume(bufferEntity);
		}
	}

	private class CachingInputStream extends InputStream {
		private final InputStream wrappedInput;
		private ByteArrayBuffer buffer;
		private boolean eof;

		CachingInputStream(InputStream input, int bufferSize) {
			wrappedInput = input;
			buffer = new ByteArrayBuffer(bufferSize);
		}

		@Override
		public int read() throws IOException {
			if(eof) {
				return -1;
			}

			final int b = wrappedInput.read();
			if(b == -1) { 
				processEOF();
			} else {
				buffer.append(b);
			}
			checkMaximumLength();
			return b;
		}
		
		public int read(byte[] b, int off, int len) throws IOException {
			if(eof) {
				return -1;
			}
			
			final int n = wrappedInput.read(b, off, len);
			if(n == -1) {
				processEOF();
			} else {
				buffer.append(b, off, n);
			}
			checkMaximumLength();
			return n;
		}

		private void checkMaximumLength() throws IOException {
			if(maximumInputKilobytes > 0 && buffer.length() > (maximumInputKilobytes * 1024)) {
				wrappedInput.close();
				eof = true;
				buffer = null;
				setActiveByteArrayEntity(new byte[0]);
				throw new IOException("Maximum length of "+ maximumInputKilobytes +" kb exceeded while streaming http entity.");
			}
		}

		public void close() throws IOException {
			wrappedInput.close();
			if(!eof) {
				setActiveByteArrayEntity(buffer.toByteArray());
			}
		}

		private void processEOF() throws IOException {
			wrappedInput.close();
			eof = true;
			setActiveByteArrayEntity(buffer.toByteArray());
		}
	}
}
