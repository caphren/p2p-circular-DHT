package cdht;

import java.io.IOException;
import java.util.Scanner;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class cdht {
	private static final int peerRange = 255;
	private static final int portSeed = 50000;
	
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
		cdht peer = new cdht(thisPeer, firstSuc, secondSuc);
		DatagramSocket socket = new DatagramSocket(portSeed + thisPeer);
		
		//get ready to run the peer
		boolean running = true;
		DatagramPacket reply = new DatagramPacket(new byte[1024], 1024);
		Scanner scan = new Scanner(System.in);
		String s, q = "quit\n";
		
		//run the peer
		while(running) {
			// receive messages
			try {
				socket.receive(reply);
			
				if (reply.equals(new DatagramPacket(new byte[1024], 1024)))
					continue;

				System.out.printf("ping to %s, seq = %d, rtt = %d ms\n", host, i, (toc-tic));
			}
			catch (IOException e) {
				System.out.printf("Request timed out. %s\n", e.getMessage());
			}
			catch (Exception e) {
				System.out.printf("Error: %s\n", e.getMessage());
			}
			// check if the program needs to quit
			while (scan.hasNextLine()) {
				s = scan.nextLine();
				if (s.compareTo(q) == 0)
					running = false;
			}
		}
		
		return;
	}
	

}
