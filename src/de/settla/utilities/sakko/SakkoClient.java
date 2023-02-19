package de.settla.utilities.sakko;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Objects;

public class SakkoClient implements Sakko {

	public static SakkoClient createSakkoClient(SakkoAddress address, SakkoListener listener) {
		SakkoClient connection = new SakkoClient(address, listener);
		new Thread(connection).start();
		return connection;
	}

	private boolean close = false;
	private boolean connected = false;
	private Socket socket;
	private final SakkoAddress address;
	private SakkoListener listener;

	private DataInputStream dataInput;
	private DataOutputStream dataOutput;

	private SakkoClient(SakkoAddress address, SakkoListener listener) {
		this.address = Objects.requireNonNull(address);
		this.listener = Objects.requireNonNull(listener);
	}

	public SakkoAddress getAddress() {
		return address;
	}

	public SakkoListener getListener() {
		return listener;
	}

	public void setListener(SakkoListener listener) {
		this.listener = listener;
	}

	public void close() {
		
		close = true;
		
		if (connected) {
			System.out.println("[SakkoConnection] Connection will be closed.");
		} else {
			System.out.println("[SakkoConnection] No Connection found.");
		}
		
		try {
			if (socket != null && !socket.isClosed())
				socket.close();
		} catch (IOException e) {
			System.err.println("[SakkoConnection] Could not proberly close the socket.");
		}
		
	}

	public boolean isClosed() {
		return connected;
	}

	public boolean publish(String msg) {
		Objects.requireNonNull(msg);
		if (connected && dataOutput != null) {
			try {
				dataOutput.writeUTF(msg);
				dataOutput.flush();
				return true;
			} catch (IOException e) {
				return false;
			}
		} else {
			return false;
		}
	}

	@Override
	public void run() {
		while (!close && !connected) {
			try {
				socket = new Socket(address.getHost(), address.getPort());
				try {
					dataInput = new DataInputStream(socket.getInputStream());
					try {
						dataOutput = new DataOutputStream(socket.getOutputStream());
						System.out.println("[SakkoConnection] Connection builded.");
						connected = true;
						while (!close && connected) {
							try {
								String received = dataInput.readUTF();
								listener.listen(received);
							} catch (EOFException e) {
								System.err.println(
										"[SakkoConnection] Connection lost while reading data, try to connect later...");
								connected = false;
							} catch (IOException e) {
								System.err.println("[SakkoConnection] Error while reading data...");
								connected = false;
							}
						}
					} catch (IOException e) {
						System.err.println("[SakkoConnection] Could not open output stream!");
						connected = false;
					}
				} catch (IOException e) {
					System.err.println("[SakkoConnection] Could not open input stream!");
					connected = false;
				}
			} catch (UnknownHostException e) {
				System.err.println(
						"[SakkoConnection] Unknown Host: (" + address.getHost() + ":" + address.getPort() + ")");
				connected = false;
			} catch (ConnectException e) {
				// System.err.println("[SakkoConnection] Connection lost, try to connect later...");
				connected = false;
			} catch (IOException e) {
				e.printStackTrace();
				connected = false;
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}
		try {
			if (socket != null && !socket.isClosed())
				socket.close();
		} catch (IOException e) {
			System.err.println("[SakkoConnection] Could not proberly close the socket.");
		} finally {
			connected = false;
			System.out.println("[SakkoConnection] Connection closed.");
		}
	}

}
