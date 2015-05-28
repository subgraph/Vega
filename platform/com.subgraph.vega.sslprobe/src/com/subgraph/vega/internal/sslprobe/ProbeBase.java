package com.subgraph.vega.internal.sslprobe;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.SocketFactory;

abstract public class ProbeBase<T> implements Callable<T>, Runnable {
	private final static Logger logger = Logger.getLogger(ProbeBase.class.getName());
	private final SocketFactory socketFactory;
	private Socket socket;
	private InputStream input;
	private OutputStream output;
	
	
	
	protected final SSLServerScanResult scanResult;
	
	ProbeBase(SSLServerScanResult scanResult) {
		this(scanResult, SocketFactory.getDefault());
	}

	ProbeBase(SSLServerScanResult scanResult, SocketFactory socketFactory) {
		this.scanResult = scanResult;
		this.scanResult.incrementOutstandingProbeCount();
		this.socketFactory = socketFactory;
	}
	

	
	protected void skip(ByteBuffer bb, int n) {
		bb.position(bb.position() + n);
	}

	protected void hexDump(ByteBuffer bb) {
		int n = 0;
		while(bb.hasRemaining()) {
			int b = bb.get() & 0xFF;
			n += 1;
			if(n % 16 == 0) {
				System.out.print("\n");
			}
			System.out.printf("%02X ", b);
		}
		System.out.println();
		bb.rewind();
	}

	@Override
	public void run() {
		try {
			call();
		} catch (Exception e) {
			logger.log(Level.WARNING, "Unexpected exception during probe: "+ e, e);
		}
	}

	@Override
	public T call() throws Exception {
		try {
			return runProbe();
		} catch (UnknownHostException e) {
			logger.warning("Unknown host exception executing probe.");
			scanResult.setTLSProbeFailure("Unknown host");
			return null;
		} catch (IOException e) {
			logger.warning("I/O error while executing probe: "+ e);
			//scanResult.setTLSProbeFailure("I/O error: "+ e);
			return null;
		} finally {
			scanResult.decrementOutstandingProbeCount();
			if(socket != null) {
				closeQuietly(socket);
			}
		}
	}
	
	protected abstract T runProbe() throws Exception;

	protected void closeConnection() {
		if(socket != null) {
			
			closeQuietly(socket);
			socket = null;
			input = null;
			output = null;
		}
	}

	protected Socket getSocket() throws UnknownHostException, IOException {
		if(socket == null) {
			if (System.getProperty("socksEnabled") != null) {
				if (System.getProperty("socksEnabled").equals("true")) {
					InetSocketAddress unresolved = InetSocketAddress.createUnresolved(scanResult.getTargetHost(), scanResult.getTargetPort());
					socket = socketFactory.createSocket();
					socket.connect(unresolved);
				} else {
					socket = socketFactory.createSocket(scanResult.getTargetHost(), scanResult.getTargetPort());
				}
			} else
			{
				socket = socketFactory.createSocket(scanResult.getTargetHost(), scanResult.getTargetPort());
			}
		}
		return socket;
	}
	
	protected InputStream getInputStream() throws IOException {
		if(input == null) {
			input = getSocket().getInputStream();
		}
		return input;
	}
	
	protected OutputStream getOutputStream() throws IOException {
		if(output == null) {
			output = getSocket().getOutputStream();
		}
		return output;
	}
	
	protected boolean readAll(byte[] buffer) throws IOException {
		int remaining = buffer.length;
		int offset = 0;
		while(remaining > 0) {
			int n = getInputStream().read(buffer, offset, remaining);
			if(n == -1) {
				return false;
			}
			remaining -= n;
			offset += n;
		}
		return true;
	}

	private void closeQuietly(Socket socket) {
		try {
			socket.close();
		} catch (IOException e) {}
	}
	
}
