package chatapplication;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class sideserver extends Thread  {
	 private final int serverport;
	
	 private ArrayList<coserver> coserverlist= new ArrayList<>();
	 
	public sideserver (int serverport) {
		this.serverport = serverport;
	}

	public java.util.List<coserver> getcoserverlist(){
		return coserverlist;
	}
	public void run ( ) {
		try {
			ServerSocket ss = new ServerSocket(serverport);
			while (true) {
				Socket clisoc= ss.accept();
				 coserver cs = new coserver( this , clisoc);
				 coserverlist.add(cs);
				 cs.start();
				 
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void removecoserver(coserver coserver) {
		coserverlist.remove(coserver);
	}
}
