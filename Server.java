package chatapplication;


public class Server {
	public static void main (String args []) {
		int port =6654;
		sideserver sideserver = new sideserver(port);
		sideserver.start();
	
}
}
