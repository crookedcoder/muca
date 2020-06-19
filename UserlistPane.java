package chatapplication;
    
import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.swing.*;
    


public class UserlistPane extends JPanel implements UserStatusListener {
    
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Client client ;
	private JList<String> userListUI;
	private DefaultListModel<String> userListModel;
	
    //CONSTRUCTOR
	public UserlistPane(Client client) {
		
		
		this.client = client;
		this.client.addUserStatusListener(this);
		
		userListModel = new DefaultListModel<>();
		userListUI =new JList<>(userListModel);
		setLayout (new BorderLayout());
		add(new JScrollPane(userListUI),BorderLayout.CENTER);
		
		//mouse
		userListUI.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount()>1) {
					String login = userListUI.getSelectedValue();
					MessagePane messagePane = new MessagePane(client , login);
					
					JFrame f = new JFrame("message" + login);
					f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					f.setSize(500,500);
					f.getContentPane().add(messagePane,BorderLayout.CENTER);
					f.setVisible(true);
					
					
				}
			}
		});
	}
   //MAIN()
	public static void main (String [] args) throws IOException {
	
	Client client = new Client ("localhost",6654);
	
	
	/*UserlistPane ulp =  new UserlistPane(client);
	JFrame frame= new JFrame ("User__List" );
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.setSize(400,600);
	
	frame.getContentPane().add(new JScrollPane(ulp),BorderLayout.CENTER);
	frame.setVisible(true);
	*/
	if(client.connect())
	{
		client.login("guest", "guest");
	}
}

	@Override
	public void online(String login) {
		userListModel.addElement(login);
	}

	@Override
	public void offline(String login) {
		// TODO Auto-generated method stub
		userListModel.removeElement(login);
	}

}
