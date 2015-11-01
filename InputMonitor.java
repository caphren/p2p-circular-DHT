//package comp9331;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputMonitor extends Thread {

	public void run() {
		// prep
		Scanner scan = new Scanner(System.in); // to read in a command
		String s;
		Pattern quit = Pattern.compile("quit");
		Pattern request = Pattern.compile("request [0-9]{4}");

		while (Peer.running) {
			if (scan.hasNextLine()) {
				s = scan.nextLine();
				Matcher quitMatch = quit.matcher(s);
				Matcher requestMatch = request.matcher(s);

				if (!quitMatch.matches() && !requestMatch.matches()) {
					// If invalid input, error message
					System.out
							.print("Please enter a valid command: \n"
									+ "\tquit \tTo quit the peer, or\n"
									+ "\trequest nnnn \t To request the file \"nnnn\" (4 digit filename)");

				} else if (quitMatch.matches()) {
					// This peer will depart the network
					peerQuit();
				} else {
					// Get the filename
					String[] req = s.split(" ");
					if (req.length == 2)
						requestFile(req[1]);
				}
			}
		}
	}

	private void peerQuit() {
		try {
			// need to inform both predecessors that this peer will quit
			int p1 = Peer.getFirstPredecessor();
			int p2 = Peer.getSecondPredecessor();
			String quitString = "quit " + Integer.toString(Peer.getPeerId()) + " " + p1 + " " + p2;
			String quitReply1 = "";
			String quitReply2 = "";
			// First Predecessor
			if (!Peer.outOfRange(p1)) { // check it's been initialised
				Socket tcpSocket1 = new Socket("localhost", Peer.portSeed + p1);

				// write to server
				DataOutputStream outToServer1 = new DataOutputStream(
						tcpSocket1.getOutputStream());
				outToServer1.writeBytes(quitString + '\n');

				// create read stream and receive from server
				BufferedReader inFromServer1 = new BufferedReader(
						new InputStreamReader(tcpSocket1.getInputStream()));
				quitReply1 = inFromServer1.readLine();
				tcpSocket1.close();
			}

			// Second Predecessor
			if (!Peer.outOfRange(p2)) { // check it's been initialised
				Socket tcpSocket2 = new Socket("localhost", Peer.portSeed + p2);

				// write to server
				DataOutputStream outToServer2 = new DataOutputStream(
						tcpSocket2.getOutputStream());
				outToServer2.writeBytes(quitString + '\n');

				// create read stream and receive from server
				BufferedReader inFromServer2 = new BufferedReader(
						new InputStreamReader(tcpSocket2.getInputStream()));
				quitReply2 = inFromServer2.readLine();
				tcpSocket2.close();
			}

			// Check responses were ok before closing peer
			if (quitReply1.contains("ok") && quitReply2.contains("ok")) {
				Peer.running = false;
				System.out.println("This peer is no longer running.");
			}

		} catch (IOException e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	private void requestFile(String fileName) {
		try {
			// Send the file request to first successor
			if (!Peer.outOfRange(Peer.getFirst())) { // make sure peer is valid
				Socket tcpSocket = new Socket("localhost", Peer.portSeed
						+ Peer.getFirst());
				String fileReq = "request " + fileName + " "
						+ Integer.toString(Peer.getPeerId()); // eg
																// "request 2012 8"
																// for file 2012
																// from peer 8
				DataOutputStream outToServer = new DataOutputStream(
						tcpSocket.getOutputStream());
				outToServer.writeBytes(fileReq);

				tcpSocket.close();

				// Print message to stdin
				System.out.println("File request for " + fileName
						+ " has been send to my successor.");
			}
		} catch (IOException e) {
			System.out.println("Error: " + e.getMessage());
		}
	}
}
