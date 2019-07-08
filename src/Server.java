import java.io.*;
import java.net.*;
import java.util.Vector;
import java.util.concurrent.*;

/*Server class for establishing a connection with a
 * Client and then accepting Client messages,
 * relaying them to the Client for whom
 * the message is addressed
 */
public class Server {
	private static final int port = 65501;
	private static final int maxusers = 10;
	private static Vector<ChatHandler> users = new Vector<ChatHandler>();
	public static void main(String[] args) throws IOException {
		try (ServerSocket server = new ServerSocket(port)) {
			ExecutorService threadpool = Executors.newFixedThreadPool(maxusers);
			while (true) {
				Socket connection = server.accept();
				ChatHandler newuser = new ChatHandler(connection);
				users.add(newuser);
				threadpool.execute(newuser);
			}
		}
	}
	
	/*Handler class for processing the activity in the
	 * Object In/OutputStreams of each Client
	 */
	private static class ChatHandler implements Runnable{
		private Socket connection;
		private ObjectInputStream inputstream;
		private ObjectOutputStream outputstream;
		private String username;
		ChatHandler(Socket connection) {
			this.connection = connection;
			try {
				outputstream = new ObjectOutputStream(connection.getOutputStream());
				inputstream = new ObjectInputStream(connection.getInputStream());
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		@Override 
		public void run() {
			try {
				String receivedname = (String) inputstream.readObject();
				username = receivedname;
				while (true) {
					System.out.println("Waiting...");
					MessengerPage.Message in = (MessengerPage.Message) inputstream.readObject();
					for (ChatHandler user : users) {
						if (user.username.equals(in.receiver)) {
							user.outputstream.writeObject(in);
						}
					}
				}
			}
			catch (EOFException ex) {
				System.out.println(username + " has left the chat");
			}
			catch (IOException | ClassNotFoundException ex) {
				ex.printStackTrace();
			}
			finally {
				try {
					inputstream.close();
					outputstream.close();
					connection.close();
				} 
				catch (IOException e) {
					e.printStackTrace();
				}
			}
	   }
	}
}