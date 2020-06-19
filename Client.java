package chatapplication;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Logger;

public class Client {
		
	// DATA MEMBERS 
	private final String serverName;
	private final int serverPort;
	private static  Socket socket;
	
	private OutputStream serverOut;
	private InputStream serverIn;
	private static BufferedReader Bufferedin;
	
	//ARRAY LISTS ARE MADE
	public static   ArrayList<UserStatusListener> listOfUserStatusListener = new ArrayList<>();
	
	public static   ArrayList<messageListener> messageListenerlist = new ArrayList<>();

	//CONSTRUCTOR
	public Client (String serverName, int serverPort)
	  {
		this.serverName =serverName;
		this.serverPort =serverPort;
		
	  }
	//void main
	public static void main (String args []) throws IOException {
		 Client client = new Client ("localhost",6654);
		             //addUserlistener will add the login user to the array list 
		 			//new UserStatuslistener creates an instance of the interface of UserStatusLisetener    
		 						
					 client.addUserStatusListener(  new UserStatusListener() {
						@Override//below two functions are to display who is online and who is offline  
						public void online(String login) {
							// here we have defined a function online 
							 System.out.println("ONLINE"+login);
						}
						
						@Override
						public void offline(String login) {
							// here we have defined a function offline
							 System.out.println("OFFLINE"+login);				 		 
					 }});
					    
					 client.addMessageListener(new messageListener() {
						public void onMessage(String fromLogin, String msgBody) {
							System.out.println("you got a message from "+ fromLogin +":"+ msgBody);
						 
						}
				     });
			 
		 if (!client.connect()) {
			 System.out.println("connection failed");
			    
		 }else {
			    System.out.println("connection successfull");
				if ( client.login("parth","parth") ) {
					 System.out.println("login successfull");
					 
					} else {
					 System.err.println("login failed");
					}
				//client.logoff();
	     }
   }

   
	public void msg(String sendto, String msgbody) throws IOException {
			String cmd ="msg" + sendto +" "+ msgbody +  "\n";
			serverOut.write(cmd.getBytes());
	}


	public void logoff() throws IOException {
		
		String cmd = "logoff \n";
		serverOut.write(cmd.getBytes());
	}

	public boolean login(String name , String password) throws IOException {
			String cmd = "login "+ name +" "+password +"\n";
			serverOut.write(cmd.getBytes());
			
			String response = Bufferedin.readLine();
			System.out.println("Response line : " + response);
			if (" ok login ".equalsIgnoreCase(response)) {
				
				startMessageReader();
				
				return (true) ;
				
			
			}else {
				return false;
			}
			
			
	}
	
	public  void startMessageReader() {
		
		Thread t = new Thread () {
		public	void run () {
				readMessageloop();
			}
		};
		t.start();
	}

	public static void readMessageloop() {
		try {
			String line;
			while((line=Bufferedin.readLine())!= null) {
			  
			  String [] tokens =line.split("\\s",0);
				if(tokens!= null&&tokens.length>0) { 
		            String cmd = tokens[0];
		           
		            if("online".equalsIgnoreCase(cmd)) {
		            	handleOnline(tokens);
		            }else if ("offline".equalsIgnoreCase(cmd)){
		            	handleOffline(tokens);
		            	
		            }else if ("msg".equalsIgnoreCase(cmd)) {
		            	String [] tokensn =line.split("\\s", 3);
		            	handleMsg(tokensn);
		            }   
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				socket.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	

	public static void handleMsg(String[] tokensn) {
		String sendTo = tokensn[1];
		String msgcontent =tokensn[2];
		for (messageListener Listener : messageListenerlist) {
			Listener.onMessage(sendTo, msgcontent);
		}
		
	}

	public static void handleOffline(String[] tokens) {
			String login = tokens[1];
			for (UserStatusListener listener :listOfUserStatusListener) {
				listener.offline(login);
			}
	}

	public static void handleOnline(String[] tokens) {
			String login = tokens[1];
			for (UserStatusListener listener :listOfUserStatusListener)
			{
				listener.online(login);
			}
	}

public boolean connect() {
		
		try {
			socket = new Socket (serverName,serverPort);
			System.out.println("client port is "+ socket.getLocalPort());
			this.serverOut = socket.getOutputStream();
			this.serverIn  = socket.getInputStream();
			Bufferedin = new BufferedReader(new InputStreamReader(serverIn));
			return true;

		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	
	public void addUserStatusListener(UserStatusListener listener) {
		listOfUserStatusListener.add(listener);
    }
	public void removeUserStatusListener(UserStatusListener listener) {
		listOfUserStatusListener.remove(listener);
    }
	public void addMessageListener(messageListener listener) {
		messageListenerlist.add(listener);
	}
	public void removeMessageListener(messageListener listener) {
		messageListenerlist.remove( listener);
	}
	
}