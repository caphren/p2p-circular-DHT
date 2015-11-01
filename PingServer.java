//package comp9331;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class PingServer extends Thread {

	public void run() {
		try {

			// Socket
			DatagramSocket socket = new DatagramSocket(Peer.portSeed + Peer.getPeerId());
			InetAddress host = InetAddress.getByName("localhost");
			

			while (Peer.running) {
				DatagramPacket msg = new DatagramPacket(new byte[1024], 1024);

				socket.receive(msg);

				String s = new String(msg.getData());
				String[] replyMsg = s.split(" "); // eg "2 8" is Seq# 2 from Peer 8
				// Print 
				System.out.println("A ping request message was received from Peer " + replyMsg[1] + ".\n");
				// respond to ping
				byte[] b = replyMsg[0].getBytes(); // eg "2" if Seq# 2 above
				String preS = replyMsg[1];
				int pre = Integer.parseInt(preS.trim());
				DatagramPacket reply = new DatagramPacket(b, b.length, host,
						msg.getPort());
				socket.send(reply);
				// set predecessor
				Peer.addPredecessor(pre);
			}
		} catch (SocketException e) {
			System.out.println("Error: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("Error: " + e.getMessage());
		}

	}
}
