package cdht;

import java.io.IOException;
import java.util.Scanner;
import java.net.*;

public class cdht {
	private static final int peerRange = 255;
	private static final int portSeed = 50000;
	private static final int oneSecond = 1000;
	private static final int fiveSeconds = 5000;
	private static final String noMessage = "";
	private static final String ping = "ping";
	private static final String pingback = "pong";
	private static final String quit = "quit\n";
	
	private int thisPeer;
	private int firstSuc;
	private int secondSuc;
	
	
	cdht(int peer, int first, int second) {
		thisPeer = peer; firstSuc = first; secondSuc = second;
	}

	public static void main(String[] args) throws Exception{
		//setup
		String wrongArgs = "Please enter 3 integers to define this peer and its two successors.";
		int thisPeer = 0, firstSuc = 0, secondSuc = 0;
		
		if (args.length != 3) {
			System.out.println(wrongArgs);
			return;
		}
		boolean peerInit = true;
		// First peer
		if (args[0].matches("^\\d+$")) {
			thisPeer = Integer.parseInt(args[0]);
			if (thisPeer > peerRange)
				peerInit = false;
		}
		// First successor
		if (args[1].matches("^\\d+$")) {
			firstSuc = Integer.parseInt(args[1]);
			if (firstSuc > peerRange)
				peerInit = false;
		}
		// Second successor
		if (args[2].matches("^\\d+$")) {
			secondSuc = Integer.parseInt(args[2]);
			if (secondSuc > peerRange)
				peerInit = false;
		}
		// were the args valid integers?
		// this doesn't test that they correctly describe a circular DHT
		if (!peerInit){
			System.out.println(wrongArgs);
			return;
		}
		
		//initialise the peer
		// TODO remove: cdht peer = new cdht(thisPeer, firstSuc, secondSuc);
		DatagramSocket socket = new DatagramSocket(portSeed + thisPeer); // this peer's port
		socket.setSoTimeout(oneSecond + (thisPeer * 10)); // spread out the initial wait times
		//TODO remove next line
		int x = socket.getReceiveBufferSize();
		System.out.printf("The receive buffer size is %d bytes.\n", x);
		
		InetAddress host = InetAddress.getByName("localhost"); // for sending msgs
		
		//get ready to run the peer
		boolean running = true;
		DatagramPacket reply = new DatagramPacket(new byte[1024], 1024); // for reading messages
		Scanner scan = new Scanner(System.in); // to read in a "quit" command from stdin
		String s; // use with scanner
		int replyPort;
		String message = noMessage;
		int state = 2; // start off ready to send pings
		boolean first = false; // the next successor to be pinged - first or second successor alternately
		int port = portSeed;
		int rx = 0; // count received packets
		int loop = 0; // count timeouts when waiting for ping responses
		
		//run the peer
		while(running) {
			
			// State 1: Listening
			try {
				socket.receive(reply);
				// check what packet was rx'd
				message = new String(reply.getData());
				if (message.compareTo(pingback) == 0) {
					state = 3;
					rx++;
				} else if (message.compareTo(ping) == 0) {
					state = 4;
				}
				
			}
			catch (IOException e) {
				// Socket timeout, no problem, keep going
				if (state == 3)
					loop++;
			}
			catch (Exception e) {
				System.out.printf("Error: %s\n", e.getMessage());
			}
			
			// State 2: Sending pings
			if (state == 2) {
				// Create datagram packets to send & receive
				first = !first;
				if (first)
					port = portSeed + firstSuc;
				else
					port = portSeed + secondSuc;
				byte[] b = ping.getBytes();
				DatagramPacket p = new DatagramPacket(b, b.length, host, port);
				// send 3x pings
				for (int i = 0; i < 3; i++) {
					socket.send(p);
				}
				socket.setSoTimeout(oneSecond); // don't wait too long for replies
			}
			
			// Stage 3: Ping responses
			if (state == 3) {
				if ((rx + loop) >= 2) {
					if (rx == 0) {
						// no ping replies, successor may have quit
						// use "first" to determine which
						// TODO
					}
					loop = 0;
					rx = 0;
					state = 2;
				}
			}
			
			// Stage 4: Received a ping request
			if (state == 4) {
				// TODO
			}

			
			// check if the program needs to quit
			while (scan.hasNextLine()) {
				s = scan.nextLine();
				if (s.compareTo(quit) == 0)
					running = false;
			}
		}
		socket.close();
		
		return;
	}
	

}
