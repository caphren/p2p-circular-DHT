//package comp9331;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class PingClient extends Thread {

	private static final int oneSecond = 1000;
	private static final long fiveSeconds = 5000;

	public void run() {
		try {
			// Socket
			DatagramSocket socket1 = new DatagramSocket();
			DatagramSocket socket2 = new DatagramSocket();
			socket1.setSoTimeout(oneSecond);
			socket2.setSoTimeout(oneSecond);
			InetAddress host = InetAddress.getByName("localhost");

			// Loop Variables
			int s1 = Peer.getFirst();
			int s2 = Peer.getSecond(); // record these in case they change
										// midway
			DatagramPacket msg1 = new DatagramPacket(new byte[1024], 1024);
			DatagramPacket msg2 = new DatagramPacket(new byte[1024], 1024);
			String peerNum = Integer.toString(Peer.getPeerId());
			int seq = 0; // sequence number for pings
			int rx1 = 0;
			int rx2 = 0; // keep track of acks

			while (Peer.running) {
				// if successors have changed since the last loop, reset ping
				// attempts count
				if (s1 != Peer.getFirst()) {
					rx1 = seq - 1;
					s1 = Peer.getFirst();
				}
				if (s2 != Peer.getSecond()) {
					rx2 = seq - 1;
					s2 = Peer.getSecond();
				}
				// Prepare ping messages
				String pings = Integer.toString(seq) + " " + peerNum;
				byte[] b = pings.getBytes();
				// Send ping requests
				DatagramPacket request1 = new DatagramPacket(b, b.length, host,
						Peer.portSeed + s1);
				DatagramPacket request2 = new DatagramPacket(b, b.length, host,
						Peer.portSeed + s2);
				socket1.send(request1);
				socket2.send(request2);
				seq++;

				// Wait for responses (One second timeout was set above)
				try {
					// First successor
					socket1.receive(msg1);

					if ((new String(msg1.getData())).contains(Integer
							.toString(seq))) {
						// Print when response received
						System.out
								.println("A ping response message was received from Peer "
										+ Integer.toString(s1) + ".");
						rx1 = seq; // record last acked seq number
					}

				} catch (SocketTimeoutException e) {
					// if 3 pings are missed, assume peer is gone
					if ((seq - rx1) >= 3) {
						Peer.removeSuccessor(s1);

						// ask Peer.getSecond() for its first successor...
						// Use TCP connection
						if (!Peer.outOfRange(Peer.getFirst())) { // make sure successor is valid peer number
							Socket sucSocket = new Socket("localhost",
									Peer.portSeed + Peer.getFirst());
							// eg "successor 8" if the new successor is peer 8
							DataOutputStream outToServer = new DataOutputStream(
									sucSocket.getOutputStream());
							outToServer.writeBytes("successor\n");

							BufferedReader inFromServer = new BufferedReader(
									new InputStreamReader(
											sucSocket.getInputStream()));
							String reply = inFromServer.readLine();

							// close client socket
							sucSocket.close();

							// extract peer number from reply
							String[] rep = reply.split(" ");
							if (rep.length == 2) {
								int suc = Integer.parseInt(rep[1]);
								Peer.addSuccessor(suc);
							}
							
							System.out.println("Peer " + Integer.toString(s1)
									+ " is no longer alive.");
							System.out.println("My first successor is now peer "
									+ Peer.getFirst() + ".");
							System.out.println("My second successor is now peer "
									+ Peer.getSecond() + ".");
						}
					}
				}
				try {
					// Second successor
					socket2.receive(msg2);

					if ((new String(msg2.getData())).contains(Integer
							.toString(seq))) {
						// Print when response received
						System.out
								.println("A ping response message was received from Peer "
										+ Integer.toString(s2) + ".");
						rx2 = seq; // record last acked seq number
					}

				} catch (SocketTimeoutException e) {
					// if 3 pings are missed, assume peer is gone
					if ((seq - rx2) >= 3) {
						Peer.removeSuccessor(s2);
						int suc = s2;
						
						// ask Peer.getFirst() for its first successor...
						// Use TCP connection
						while ((suc == s2) && !Peer.outOfRange(Peer.getFirst())) { // make sure successor is valid peer number
							Socket sucSocket = new Socket("localhost",
									Peer.portSeed + Peer.getFirst());
							// eg "successor 8" if the new successor is peer 8
							DataOutputStream outToServer = new DataOutputStream(
									sucSocket.getOutputStream());
							outToServer.writeBytes("successor\n");

							BufferedReader inFromServer = new BufferedReader(
									new InputStreamReader(
											sucSocket.getInputStream()));
							String reply = inFromServer.readLine();

							// close client socket
							sucSocket.close();

							// extract peer number from reply
							String[] rep = reply.split(" ");
							if (rep.length == 2) {
								suc = Integer.parseInt(rep[1]);
								if (suc != s2)
									Peer.addSuccessor(suc);
							}
						}
						System.out.println("Peer " + Integer.toString(s2)
								+ " is no longer alive.");
						System.out.println("My first successor is now peer "
								+ Peer.getFirst() + ".");
						System.out.println("My second successor is now peer "
								+ Peer.getSecond() + ".");
					}

				}
				// Space pings by 5 seconds
				Thread.sleep(fiveSeconds);
			}
		} catch (SocketException e) {
			System.out.println("Error: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("Error: " + e.getMessage());
		} catch (InterruptedException e) {
			System.out.println("Error: " + e.getMessage());
		}
	}
}
