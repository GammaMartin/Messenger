import javax.swing.*;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

/*GUI that displays and handles new
 * chat interactions between users. In addition,
 * for returning users, will display the user's 
 * friendlist as well as their chat histories. 
 */
public class MessengerPage {
	
	/*instance variables and constants*/ 
	
	private JFrame frame;
	private String mainuser;
	private JTextField friendfield;
	private JLabel addfriend;
	private JTabbedPane friendtabs;
	private JPanel panel1;
	private DatabaseManager manager;
	private static final int port = 65501;
	private ObjectInputStream inputstream;
	private ObjectOutputStream outputstream;
	private Socket connection;
	private Set<Tab> tabs;
	
	public MessengerPage(String user) {
		mainuser = user;
		frame = LoginPage.frame;
		frame.getContentPane().removeAll();
		manager = LoginPage.manager;
		tabs = new HashSet<>();
		friendfield = new JTextField(LoginPage.FIELD_SIZE);
		friendfield.setMaximumSize(new Dimension(Integer.MAX_VALUE, friendfield.getPreferredSize().height));
		friendfield.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String newfriend = friendfield.getText();
				if (newfriend.isEmpty() != true && manager.containsUser(newfriend) && !isAFriend(newfriend)) {
					updateFriendList(newfriend);
					friendfield.setText("");
				}
			}
		});
		addfriend = new JLabel("Add a friend.");
		addfriend.setAlignmentX(Component.CENTER_ALIGNMENT);
		friendtabs = new JTabbedPane(JTabbedPane.LEFT, JTabbedPane.SCROLL_TAB_LAYOUT);
		panel1 = new JPanel();
		panel1.setLayout(new BoxLayout(panel1, BoxLayout.Y_AXIS));
		panel1.add(addfriend);
		panel1.add(friendfield);
		setUpStreams();
		panel1.add(friendtabs);
		frame.add(panel1);
		frame.pack();
		frame.setVisible(true);
		begin();
	}
	
	/*The Message object is what is communicated
	 * to the Server and relayed to the user's friend.
	 * Contains information on the sender, addressee
	 * and message.
	 */
	public static class Message implements Serializable {
		private static final long serialVersionUID = 1L;
		public String sender;
		public String receiver;
		public String msg;
		public Message(String sender, String receiver, String msg) {
			this.sender = sender;
			this.receiver = receiver;
			this.msg = msg;
		}
	}
	
	/*The Tab component represents the
	 * tab corresponding to each user-friend
	 * interaction. When a friend tab is clicked upon, 
	 * will display the user's conversation with
	 * said friend
	 */
	private class Tab extends JPanel {
		public JTextField input;
		public JTextArea chatlog;
		public JScrollPane scroller;
		public String friendname;
		public Tab(String friend) {
			this.friendname = friend;
			chatlog = new JTextArea("");
			chatlog.setEditable(false);
			input = new JTextField(LoginPage.FIELD_SIZE);
			input.setMaximumSize(new Dimension(Integer.MAX_VALUE, input.getPreferredSize().height));
			scroller = new JScrollPane(chatlog);
			this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			this.add(scroller);
			this.add(input);
			input.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String msg = input.getText();
					if (msg.isEmpty() != true) {
						String messageout = mainuser + ": " + msg + "\n";
						Message out = new Message(mainuser, friend, messageout);
						try {
							outputstream.writeObject(out);
							outputstream.flush();
						}
						catch (IOException ex) {
							ex.printStackTrace();
						}
						manager.updateLog(messageout + "\n", mainuser, friend);
						updateTab("\n" + out.msg, out.receiver);
						input.setText("");
					}
				}
			});
		}
	}
	
	/*Sets up ObjectStreams for the Messages as well as
	 * the connection to the server.
	 */
	private void setUpStreams() {
		try {
			connection = new Socket(LoginPage.IP, port);
			outputstream = new ObjectOutputStream(connection.getOutputStream());
			inputstream = new ObjectInputStream(connection.getInputStream());
			loadTabs();
		}
		catch (IOException | SQLException e) {
			e.printStackTrace();
		}
	}
	
	/*Loads chat history*/
	public void loadTabs() throws SQLException {
		Set<String> list = manager.retrieveFriendList(mainuser);
		if (!list.isEmpty()) {
			Map<String, ArrayList<String>> history = manager.retrieveLog(mainuser);
			for (String friend: list) {
				if (history.containsKey(friend)) {
					Tab upload = new Tab(friend);
					ArrayList<String> chat = history.get(friend);
					for (String post: chat) {
						upload.chatlog.append(post);
					}
					friendtabs.addTab(friend, upload);
					tabs.add(upload);
				}
			}
		}
	}
	
	/*A thread that will accept user messages
	 * until the program is closed.
	 */
	private void begin() {
		Thread receive = new Thread(new Runnable() {
			@Override
			public void run() { {
					try {
						outputstream.writeObject(mainuser);
						while (true) {
							Message objectin = (Message) inputstream.readObject();	
							updateTab("\n" + objectin.msg, objectin.sender);
						}
					} 
					catch (IOException | ClassNotFoundException exception) {
						exception.printStackTrace();
					}
					finally {
						try {
							System.out.println("Closing...");
							connection.close();
							inputstream.close();
							outputstream.close();
						}
						catch (IOException ex) {
							ex.printStackTrace();
						}
					}
				}
			}
		});
		receive.start();
	}
	
	/*Displays new message on tab*/
	private void updateTab(String newmessage, String id) {
		for (Tab t: tabs) {
			if (t.friendname.equals(id)) {
				t.chatlog.append(newmessage);
				frame.setVisible(true);
			}
		}
	}
	
	/*checks whether the current user is already
	 * friends with another user
	 */
	private boolean isAFriend(String newfriend) {
		for (Tab tab: tabs) {
			if (tab.friendname.equals(newfriend)) {
				return true;
			}
		}
		return false;
	}
	
	/*Adds new friend to friendlist*/
	private void updateFriendList(String newfriend) {
		manager.createLog(mainuser, newfriend);
		manager.updateFriendList(mainuser, newfriend + "\n");
		Tab addition = new  Tab(newfriend);
		friendtabs.addTab(newfriend, addition);
		tabs.add(addition);
		frame.pack();
		frame.setVisible(true);
	}
}