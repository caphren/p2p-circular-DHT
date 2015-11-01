//package comp9331;

public class cdht_ex {

	public static void main(String[] args) {
		// setup Peer
		Peer.setup(args);
		
		// run threads which do the Peer jobs
		try {
			PingServer pingerz = new PingServer();
			pingerz.start();
			
			PingClient pong = new PingClient();
			pong.start();
			
			InputMonitor readz = new InputMonitor();
			readz.start();
			
			TCPServer messagez = new TCPServer();
			messagez.start();
			
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	
}
