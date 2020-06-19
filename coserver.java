package chatapplication;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashSet;
import java.util.List;

public class coserver extends Thread{
	
	private Socket clientsoc ;
	private final sideserver sideserver;
	private OutputStream os;
	private String loginname =null; 
    private HashSet<String> topicSet =new HashSet<>();
	//constructor
	public coserver (sideserver sideserver,Socket clisoc) {
		this.sideserver =sideserver ;
		this.clientsoc =clisoc;
	}
	
	public void run () {
		try {
			// as the coserver thread  run is called cli handler is called
			clihandler();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}  
	//clihandler method
	public void clihandler() throws IOException {
		this.os = clientsoc.getOutputStream();
		InputStream  is = clientsoc.getInputStream ();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line;
		//here cli handler is continuously  reading ms from client end via input stream
		while ((line=br.readLine())!= null) {
            String [] tokens =line.split("\\s", 0);
		
            if(tokens!= null && tokens.length>0) { 
           
	             String cmd = tokens[0];
				
				 if ("logoff".equalsIgnoreCase(line)||"quit".equalsIgnoreCase(line)) {
					 hanldelogoff();
					 break;
				 
				 } else if ("login".equalsIgnoreCase(cmd)){
					handlelogin (os,tokens);
				
				 }else if ("msg".equalsIgnoreCase(cmd)){
			            String [] tokensn =line.split("\\s", 3);
				        handlemsg(tokensn);
				
				 }else if("join".equalsIgnoreCase(cmd)) {
					 handlejoin( tokens);
				
				 }else if ("leave".equalsIgnoreCase(cmd)) {
					 handleleave(tokens);   
			
				 }else {
					 String msg ="unknown"+ cmd +"\n";
					 os.write(msg.getBytes());
					 
				 }
		} 
		}
	    clientsoc.close();
	    
	}
	private void handleleave(String[] tokens) {
		 if (tokens.length>1) {
			 String topic = tokens[1];
			 topicSet.remove(topic);}
	}

	public boolean isMemberOfTopic(String topic) {
		return topicSet.contains(topic);
		}
	
	private void handlejoin(String[] tokens) {
		 if (tokens.length>1) {
			 String topic = tokens[1];
			 topicSet.add(topic);
			 
		 }
	}
	
	//format : "msg " " name " body
	// format : "msg" "#topic" body
	
	private void handlemsg(String[] tokens) throws IOException {
		
		String sendTo = tokens[1];
		String msgcontent =tokens[2];
	    
		boolean isTopic = sendTo.charAt(0)=='#';
		
		List<coserver> coserverList = sideserver.getcoserverlist();
		for(coserver coserver : coserverList) {
			
			if (isTopic) {
				if (coserver.isMemberOfTopic(sendTo)) {
					String sendthismsg= "msg"+ sendTo +" "+msgcontent;
					
				}
			} else {
			
					if (sendTo.equalsIgnoreCase(coserver.getlogin())) {
					String outmsg="msg"+loginname+" "+ msgcontent +"\n";
					coserver.send(outmsg);
			}
		  }
		}
	}
	
	private void hanldelogoff() throws IOException {
		sideserver.removecoserver( this);
		List<coserver> coserverlist = sideserver.getcoserverlist();
		
		//sending other online users notification of current users logout
		String offlinemsg ="offine"+loginname+"\n";
	    	
		for (coserver coserver :coserverlist) {
			if (!loginname.equals(coserver.getlogin())) {
				coserver.send(offlinemsg);
			}
		}
	}

	public String getlogin() {
		return loginname;
		
	}
		
	public  void  handlelogin  (OutputStream os ,String[] tokens) throws IOException {
		
		String name	=tokens[1];
		String password =tokens[2];
	    
		if((name.equalsIgnoreCase("guest") && password.equalsIgnoreCase("guest"))||(name.equalsIgnoreCase("parth") && password.equalsIgnoreCase("parth")) ) {
			
			String response =" ok login \n";
			os.write(response.getBytes());
			
			this.loginname=name;
			System.out.println("the user has logged in successfully "+ name );
			
			List<coserver> coserverlist =sideserver.getcoserverlist();
				    
					//Send current user all other online logins 
							for (coserver coserver : coserverlist ) {
								if(!loginname.equals(coserver.getlogin())) {
									
								String msg2= "online " +coserver.getlogin() +"\n"; 
								send (msg2);
								}
							}
					
					//Send other online users current users status                                                                     
						String onlinestatus ="online "+name+"\n";
								for (coserver coserver : coserverlist ) {
									coserver.send(onlinestatus);
								}
}else  {
		 String msg ="error login \n";
		 os.write(msg.getBytes());
		      		   }
	}

	private void send(String anything) throws IOException {
        if (loginname != null)  
		os.write(anything.getBytes());  	 	
	}
	}


