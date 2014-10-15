package com.subgraph.vega.internal.sslprobe;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Random;

public class TLSProtocol {
	private final static int TLS_RECORD_HEADER_LENGTH = 5;
	private final static int TLS_MAXIMUM_RECORD_LENGTH = 16 * 1024;
	private final static int MAXIMUM_HANDSHAKE_MESSAGE_LENGTH = 64 * 1024;
	private final static int TLS_RECORD_ALERT = 0x15;
	public final static int TLS_RECORD_HANDSHAKE = 0x16;
	
	private final static byte TLS_HANDSHAKE_CLIENTHELLO = 0x01;
	private final static byte[] DEFAULT_VERSION = { 0x03, 0x01 };
	private final static Random random = new Random();
	
	
	private final InputStream input;
	private final OutputStream output;

	private final ByteBuffer recordBuffer;
	private final ByteBuffer handshakeBuffer;
	
	
	TLSProtocol(InputStream input, OutputStream output) {
		this.input = input;
		this.output = output;
		this.recordBuffer = ByteBuffer.allocate(TLS_RECORD_HEADER_LENGTH + TLS_MAXIMUM_RECORD_LENGTH);
		this.handshakeBuffer = ByteBuffer.allocate(MAXIMUM_HANDSHAKE_MESSAGE_LENGTH);
		handshakeBuffer.flip();
	}
	
	
	ByteBuffer getNextHandshakeMessage() throws IOException, TLSAlertException {
		while(!hasFullHandshakeMessage()) {
			if(!readNextRecord()) {
				return null;
			}
		}
		return extractNextHandshakeMessage();
	}

	private boolean hasFullHandshakeMessage() {
		final int r = handshakeBuffer.remaining();
		return !(r < 4 || r < (peekHandshakeLength() + 4));
	}

	private int peekHandshakeLength() {
		// read 32 bits from current location and mask off first byte (type)
		return handshakeBuffer.getInt(handshakeBuffer.position()) & 0x00FFFFFF;
	}
	
	private ByteBuffer extractNextHandshakeMessage() {
		final int length = peekHandshakeLength();
		final byte[] msg = new byte[length + 4];
		handshakeBuffer.get(msg);
		handshakeBuffer.compact().flip();
		return ByteBuffer.wrap(msg);
	}

	private void appendMessageBytes(byte[] messageBytes) {
		handshakeBuffer.mark();
		handshakeBuffer.position(handshakeBuffer.limit());
		handshakeBuffer.limit(handshakeBuffer.capacity());
		
		handshakeBuffer.put(messageBytes);
		
		handshakeBuffer.limit(handshakeBuffer.position());
		handshakeBuffer.reset();
	}
	
	private boolean readNextRecord() throws IOException, TLSAlertException {
		while(!hasFullRecord()) {
			if(!readToRecordBuffer()) {
				return false;
			}
		}
		recordBuffer.flip();
		int type = recordBuffer.get() & 0xFF;
		recordBuffer.getShort();     /* version */
		int length = recordBuffer.getShort() & 0xFFFF;
		byte[] messageBytes = new byte[length];
		recordBuffer.get(messageBytes);
		recordBuffer.compact();
		
		if(type == TLS_RECORD_HANDSHAKE) {
			appendMessageBytes(messageBytes);
		} else if (type == TLS_RECORD_ALERT) {
			if(messageBytes.length != 2) {
				
			}
			throw new TLSAlertException(messageBytes[0] & 0xFF, messageBytes[1] & 0xFF);
		} else {
			
		}
		return true;
	}
	
	private boolean readToRecordBuffer() throws IOException {
		final byte[] array = recordBuffer.array();
		final int offset = recordBuffer.position();
		final int len = recordBuffer.remaining();

		final int n = input.read(array, offset, len);
		if(n == -1) {
			return false;
		}
		recordBuffer.position(recordBuffer.position() + n);
		return true;
	}
	
	boolean hasFullRecord() {
		if(recordBuffer.position() < 5) {
			return false;
		}
		int length = recordBuffer.getShort(3) & 0xFFFF;
		return recordBuffer.position() >= (length + 5);
	}

	void createTLSClientHello(ByteBuffer buffer, List<TLSCipherSpec> ciphers, byte[] versionBytes) {
		buffer.clear();
		skip(buffer, 5);
		final int recordStart = buffer.position();

		buffer.put(TLS_HANDSHAKE_CLIENTHELLO);
		buffer.mark();
		skip(buffer, 3);
		int len = packClientHello(buffer, ciphers, versionBytes);
		int end = buffer.position();
		buffer.reset();
		buffer.put(packInt24(len));
		
		buffer.rewind();
		packRecordHeader(buffer, versionBytes, end - recordStart);
		buffer.flip();
	}
	
	int packClientHello(ByteBuffer buffer, List<TLSCipherSpec> ciphers, byte[] versionBytes) {
		final int start = buffer.position();
		buffer.put(versionBytes);
		buffer.putInt(getTimeSeconds());
		buffer.put(getRandomBytes());
		buffer.put((byte) 0);
		buffer.putShort((short) (2 * ciphers.size()));
		for(TLSCipherSpec c: ciphers) {
			buffer.putShort((short) c.getNumber());
		}
		buffer.put((byte) 0x02);
		buffer.put((byte) 0);
		buffer.put((byte) 0x01);
		return buffer.position() - start;
	}
	
	int extractCipherFromServerHello(ByteBuffer helloMessage) {
		skipStartOfServerHello(helloMessage);
		return helloMessage.getShort() & 0xFFFF;
	}
	
	int extractCompressionFromServerHello(ByteBuffer helloMessage) {
		skipStartOfServerHello(helloMessage);
		helloMessage.getShort(); // Cipher
		return helloMessage.get() & 0xFF;
	}
	
	private void skipStartOfServerHello(ByteBuffer helloMessage) {
		helloMessage.rewind();
		final int type = helloMessage.get() & 0xFF;
		if(type != 0x02) {
			
		}
		final int length = getInt24(helloMessage);
		if(helloMessage.remaining() < length) {
			
		}
		skip(helloMessage, 2); // version
		skip(helloMessage, 32); // random;
		final int sessionIdLength = helloMessage.get() & 0xFF;
		skip(helloMessage, sessionIdLength);
	}
	
	public int getInt24(ByteBuffer bb) {
		int val = 0;
		for(int i = 0; i < 3; i++) {
			val <<= 8;
			val |= (bb.get() & 0xFF);
		}
		return val;
	}
	
	void packRecordHeader(ByteBuffer buffer, byte[] versionBytes, int length) {
		buffer.put((byte) TLS_RECORD_HANDSHAKE);
		buffer.put(versionBytes);
		buffer.putShort((short) length);
	}
	

	private int skip(ByteBuffer buffer, int n) {
		final int pos = buffer.position();
		buffer.position(pos + n);
		return pos;
	}
	
	public void sendTLSClientHello(List<TLSCipherSpec> ciphers) throws IOException {
		sendTLSClientHello(ciphers, null);
	}
	
	public void sendTLSClientHello(List<TLSCipherSpec> ciphers, byte[] versionBytes) throws IOException {
		writeAll(createTLSClientHello(ciphers, versionBytes));
	}
	
	protected void writeAll(ByteBuffer buffer) throws IOException {
		output.write(buffer.array(), buffer.position(), buffer.remaining());
		buffer.position(buffer.position() + buffer.remaining());
		output.flush();
	}
	
	private static ByteBuffer createTLSClientHello(List<TLSCipherSpec> ciphers, byte[] versionBytes) {
		final byte[] vb = (versionBytes == null) ? (DEFAULT_VERSION) : (versionBytes);
		final ByteBuffer buffer = ByteBuffer.allocate(getTLSHelloLength(ciphers.size()));

		final int tlsRecordLength = 44 + (2 * ciphers.size());
		final int tlsHandshakeLength = tlsRecordLength - 4;
		
		buffer.put((byte) TLS_RECORD_HANDSHAKE);
		buffer.put(vb);
		buffer.putShort((short) tlsRecordLength);
		buffer.put(TLS_HANDSHAKE_CLIENTHELLO);
		buffer.put(packInt24(tlsHandshakeLength));
		buffer.put(vb);
		buffer.putInt(getTimeSeconds());
		buffer.put(getRandomBytes());
		buffer.put((byte) 0);
		buffer.putShort((short) (2 * ciphers.size()));
		for(TLSCipherSpec c: ciphers) {
			buffer.putShort((short) c.getNumber());
		}
		buffer.put((byte) 0x02);
		buffer.put((byte) 0x00);
		buffer.put((byte) 0x01);
		buffer.flip();
		return buffer;
	}
	
	static int getTLSHelloLength(int cipherCount) {
		return 49 + (2 * cipherCount);
	}
	
	static byte[] packInt24(int n) {
		final byte[] bs = new byte[3];
		bs[0] = (byte) (n >> 16);
		bs[1] = (byte) (n >> 8);
		bs[2] = (byte) n;
		return bs;
	}
	
	static private int getTimeSeconds() {
		return (int) (System.currentTimeMillis() / 1000);
	}
	
	static private byte[] getRandomBytes() {
		final byte[] bs = new byte[28];
		random.nextBytes(bs);
		return bs;
	}
}
