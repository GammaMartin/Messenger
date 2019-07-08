# Messenger

IMPORTANT - MUST READ: In order for this program to work, two server connections must be established: a MySQL server and a Server for handling
messages in the chat (provided as Server.java). The following is designed for Mac OS users,
but it can be similarly adapted for Windows.

  1) The user must have downloaded the MySQL JDBC driver and inserted it into the classpath.
  Accordingly, the user must start a MySQL server and a MySQL client.
  Then, to run the program, the user must enter 
  
  'java -cp .:$HOME/myWebProject/mysql-connector-java-8.0.{xx}/mysql-connector-java-8.0.{xx}.jar LoginPage Server-IP'
  
  into the terminal, where "xx" is the driver version and Server-IP is the IP address of the server the user wants 
  to connect to in order to relay messages.
  
  2) The user must run the Server file independently, which will handle all messages in the chat.
  
  Messenger is a instant messaging program designed in Java Swing that allows users to communicate with each other 
  through a Server. Through a MySQL database, user history is established, allowing communications from former 
  sessions to be readily accessed. To enhance user security, the BCrypt hashing function is used along with a 
  user-specific salt to store the password as a hashcode. 
