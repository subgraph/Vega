package com.subgraph.vega.internal.sslprobe;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

public class SSL2Protocol {
	
	public final static int SSLV2_CLIENT_HELLO = 0x01;
	public final static int SSLV2_SERVER_HELLO = 0x04;
	
	private final static int SSLV2_HEADER_LENGTH = 2;
	private final static int SSLV2_VERSION = 0x0002;
	private final static int SSLV2_CHALLENGE_LENGTH = 0x10;
	private final static int SSLV2_CLIENT_HELLO_BASE_LENGTH = 9 + SSLV2_CHALLENGE_LENGTH;
	
	private final InputStream input;
	private final OutputStream output;
	
	public SSL2Protocol(InputStream input, OutputStream output) {
		this.input = input;
		this.output = output;
	}
	
	public ByteBuffer getNextHandshakeMessage() throws IOException {
		final int len = readRecordLength();
		if(len == -1) {
			return null;
		}
		final ByteBuffer buffer = ByteBuffer.allocate(len);
		if(!readAll(buffer)) {
			return null;
		}
		return buffer;
	}

	private int readRecordLength() throws IOException {
		final byte[] bs = new byte[2];
		if(input.read(bs, 0, bs.length) != bs.length) {
			return -1;
		}
		return ((bs[0] & 0x7F) << 8) | (bs[1] & 0xFF);
	}
	
	private boolean readAll(ByteBuffer buffer) throws IOException {
		while(buffer.hasRemaining()) {
			int n = input.read(buffer.array(), buffer.position(), buffer.remaining());
			if(n == -1) {
				return false;
			}
			buffer.position(buffer.position() + n);
		}
		buffer.rewind();
		return true;
	}
	
	public void sendClientHello(List<SSLv2CipherSpec> ciphers) throws IOException {
		final ByteBuffer hello = createClientHello(ciphers);
		writeAll(hello);
	}

	private ByteBuffer createClientHello(List<SSLv2CipherSpec> ciphers) {
		final int length = SSLV2_HEADER_LENGTH + SSLV2_CLIENT_HELLO_BASE_LENGTH + (3 * ciphers.size());
		final ByteBuffer buffer = ByteBuffer.allocate(length);
		buffer.putShort((short) (0x8000 | (length - SSLV2_HEADER_LENGTH)));
		
		buffer.put((byte) SSLV2_CLIENT_HELLO);
		buffer.putShort((short) SSLV2_VERSION);
		buffer.putShort((short) (ciphers.size() * 3));
		buffer.putShort((short) 0); // SID length
		buffer.putShort((short) SSLV2_CHALLENGE_LENGTH);
		for(SSLv2CipherSpec c: ciphers) {
			buffer.put(c.getNumber());
		}
		final byte[] challenge = new byte[SSLV2_CHALLENGE_LENGTH];
		Arrays.fill(challenge, (byte)0x01);
		buffer.put(challenge);
		buffer.flip();
		return buffer;
	}
	
	private void writeAll(ByteBuffer buffer) throws IOException {
		output.write(buffer.array(), buffer.position(), buffer.remaining());
		buffer.position(buffer.position() + buffer.remaining());
		output.flush();
	}
}
