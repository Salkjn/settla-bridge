package de.settla.utilities.sakko;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class SakkoServer implements Sakko {

	public static SakkoServer createSakkoServer(SakkoAddress address, SakkoListener listener) {
		SakkoServer connection = new SakkoServer(address, listener);
		new Thread(connection).start();
		return connection;
	}

	private final Object clientGuard = new Object();

	private boolean close = false;
	private ServerSocket server;
	private final SakkoAddress address;
	private SakkoListener listener;

	private final List<SakkoClient> clients = new ArrayList<>();

	private SakkoServer(SakkoAddress address, SakkoListener listener) {
		this.address = Objects.requireNonNull(address);
		this.listener = Objects.requireNonNull(listener);
	}
	
	public SakkoListener getListener() {
		return listener;
	}

	public void setListener(SakkoListener listener) {
		this.listener = listener;
	}

	public SakkoAddress getAddress() {
		return address;
	}

	public boolean publish(String str) {
		if(!close && server != null && !server.isClosed()) {
			consumeSakkoClients(c -> c.publish(str));
			return true;
		}
		return false;
	}
	
	private void addSakkoClient(SakkoClient client) {
		Objects.requireNonNull(client);
		synchronized (clientGuard) {
			System.out.println("[SakkoConnection] Client added...");
			clients.add(client);
		}
	}

	private void removeSakkoClient(SakkoClient client) {
		Objects.requireNonNull(client);
		synchronized (clientGuard) {
			System.out.println("[SakkoConnection] Client removed...");
			clients.remove(client);
		}
	}

	public void consumeSakkoClients(Consumer<SakkoClient> consumer) {
		Objects.requireNonNull(consumer);
		synchronized (clientGuard) {
			clients.forEach(consumer);
		}
	}

	public void close() {
		close = true;
		try {
			if (server != null && !server.isClosed())
				server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (!close) {
			try {
				server = new ServerSocket(address.getPort());
				while (!close) {
					try {
						System.out.println("[SakkoConnection] Waiting for clients...");
						Socket accepted = server.accept();
						try {
							DataInputStream dataInput = new DataInputStream(accepted.getInputStream());
							try {
								DataOutputStream dataOutput = new DataOutputStream(accepted.getOutputStream());
								SakkoClient client = new SakkoClient(accepted, dataInput, dataOutput);
								new Thread(client).start();
							} catch (IOException e) {
								System.err.println(
										"[SakkoConnection] Could not open output stream of incoming SakkoClient!");
							}
						} catch (IOException e) {
							System.err
									.println("[SakkoConnection] Could not open input stream of incoming SakkoClient!");
						}
					} catch (IOException e) {
						System.err.println("[SakkoConnection] Could not accept incoming Socket!");
					}
				}
			} catch (IOException e) {
				System.err.println("[SakkoConnection] Could not open server on defined port! Try later...");
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}
		try {
			if (server != null && !server.isClosed())
				server.close();
		} catch (IOException e) {
			System.err.println("[SakkoConnection] Could not proberly close the socket.");
		} finally {
			System.out.println("[SakkoConnection] Connection closed.");
		}
	}

	public class SakkoClient implements Runnable {

		private boolean close = false;
		
		private final Socket socket;
		private final DataInputStream dataInput;
		private final DataOutputStream dataOutput;
		
		private SakkoClient(Socket socket, DataInputStream dataInput, DataOutputStream dataOutput) {
			this.socket = Objects.requireNonNull(socket);
			this.dataInput = Objects.requireNonNull(dataInput);
			this.dataOutput = Objects.requireNonNull(dataOutput);
		}

		@Override
		public void run() {
			addSakkoClient(this);
			while (!close && socket.isConnected() && !socket.isClosed() && socket.isBound()) {
				try {
					String input = dataInput.readUTF();
					listener.listen(input);
				} catch (EOFException e) {
					// This means that the socket was closed because we
					// could not receive any more data!
					close = true;
				} catch (IOException e) {
				}
			}
			removeSakkoClient(this);
		}

		public boolean publish(String msg) {
			Objects.requireNonNull(msg);
			if (socket.isConnected() && !socket.isClosed() && socket.isBound() && dataOutput != null) {
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
	}
}
