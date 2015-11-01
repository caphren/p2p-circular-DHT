//package comp9331;

public class Peer {
	// NB in this circular model, p2 < p1 < peerId < s1 < s2
	private static int peerId;
	private static int firstSuccessor;
	private static int secondSuccessor;
	private static int firstPredecessor;
	private static int secondPredecessor;
	
	public static boolean running;
	
	public static final int peerRange = 255;
	public static final int portSeed = 50000;
	
	public static int getPeerId() {
		return peerId;
	}
	public static int getFirst() {
		return firstSuccessor;
	}
	public static int getSecond() {
		return secondSuccessor;
	}
	public static int getFirstPredecessor() {
		return firstPredecessor;
	}
	public static int getSecondPredecessor() {
		return secondPredecessor;
	}
	public static void addSuccessor(int s) {
		if (firstSuccessor > peerRange) {
			firstSuccessor = s;
		}
		else if (secondSuccessor > peerRange) {
			if (s == firstSuccessor) {
				return;
			}
			else if (s > firstSuccessor)
				secondSuccessor = s;
			else {
				secondSuccessor = firstSuccessor;
				firstSuccessor = s;
			}
		}
	}
	public static void removeSuccessor(int s) {
		if (secondSuccessor == s)
			secondSuccessor = peerRange + 1;
		else if (firstSuccessor == s) {
			firstSuccessor = secondSuccessor;
			secondSuccessor = peerRange + 1;
		}
	}
	public static void addPredecessor(int pre) {
		// This predecessor may already be recorded
		if (pre == firstPredecessor) {
			return;
		}
		if (pre == secondPredecessor) {
			return;
		}
		// make sure correct predecessor is used
		if (firstPredecessor > peerRange) {
			firstPredecessor = pre;
		} else if (secondPredecessor > peerRange) {
			if (pre > firstPredecessor)
				secondPredecessor = pre;
			else {
				secondPredecessor = firstPredecessor;
				firstPredecessor = pre;
			}
		}
	}
	public static void removePredecessor(int pre) {
		if (pre == firstPredecessor) {
			firstPredecessor = secondPredecessor;
			secondPredecessor = peerRange + 1;
		}
		else if (pre == secondPredecessor)
			secondPredecessor = peerRange + 1;
	}
	public static boolean outOfRange(int i) {
		return (i > peerRange);
	}
	
	/* public static void setup(String[] args)
	 * 
	 * Takes the CLI arguments and initialises the (static) Peer
	 * */
	public static void setup(String[] args) {
		String wrongArgs = "Please enter 3 integers to define this peer and its two successors.";
		int thisPeer = 0, firstSuc = 0, secondSuc = 0;
		firstPredecessor = peerRange + 1;
		secondPredecessor = peerRange + 1;
		running = true;

		if (args.length != 3) {
			System.out.println(wrongArgs);
			peerId = 0;
			firstSuccessor = peerRange + 1;
			secondSuccessor = peerRange + 1;
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
		if (!peerInit) {
			System.out.println(wrongArgs);
			peerId = 0;
			firstSuccessor = peerRange + 1;
			secondSuccessor = peerRange + 1;
			return;
		}
		// initialise the Peer
		peerId = thisPeer;
		firstSuccessor = firstSuc;
		secondSuccessor = secondSuc;
	}
}
