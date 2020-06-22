// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import java.io.*;
import ocsf.server.*;
import common.*;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 * @version July 2000
 */
public class EchoServer extends AbstractServer 
{
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;

  ChatIF serverUI;
    
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */


  public EchoServer(int port) 
  {
    super(port);
  }


  public EchoServer(int port, ChatIF serverUI){
    super(port);
    this.serverUI = serverUI;
  }


  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient(Object msg, ConnectionToClient client)
  {
     if(msg.toString().startsWith("#login")){
      if(client.getInfo("loginID") != null){ //if it does not equal an object input
        try{
          System.out.println("You are already logged in!");
        }
        catch(Exception e){
          System.out.println("null");
        }
      }
      client.setInfo("loginID",msg.toString().substring(8));
      try{
        client.sendToClient(client.getInfo("loginID") + " has logged on!");
      }
      catch(IOException e){}
    }
    else{
      try{
        if(client.getInfo("loginID") == null){ //if it does equal object input
          System.out.println("Could not login, please use #login!");
          client.close();
        }
      }
      catch(IOException e){
        e.printStackTrace();
      }
    }
    System.out.println("Message received: " + msg + " from " + client.getInfo("loginID") + ", Users IP: " + client);
    if(msg.toString().startsWith("#login")){
      if(client.getInfo("loginID") != null){ //if it does equal object input
        try{
          System.out.println(client.getInfo("loginID") + " has logged on!");
        }
        catch(Exception gg){}
      }
    }
  }


  public void handleMessageFromServerUI(String message){
    try{
      if(message.charAt(0) == '#'){
        if(message.equals("#quit")){
          stopListening();  //server has now stop listening for new clients 
          this.sendToAllClients("Server has stopped listening for new clients!"); 
          close(); //closes server socket with clients 
          serverUI.display("Server is now quitting.");
        }
        else if(message.equals("#stop")){
          stopListening(); //server has now stop listening for new clients 
          this.sendToAllClients("Server has stopped listening for new clients!");
        }
        else if(message.equals("#close")){
          stopListening(); //causes the server to stop listening to new clients
          close(); //disconnect all exisitng clients 
        }
        else if(message.startsWith("#setport")){
           if(!isListening()){ //client are all logged off 
            try{
              String setPortID = message.substring(9); //port number begins after the space and starts at 9th index
              int setPort = Integer.parseInt(setPortID);
              setPort(setPort);
              serverUI.display("The new port is set to: " + setPort);
            }
            catch(Exception e){
              serverUI.display("Error... couldn't set the port");
            }
          }
          else{
             serverUI.display("Server must be closed to set a port!");
          }
        }
        else if(message.equals("#start")){
          if(!isListening()){ //while the server is stopped 
            try{
              listen();
            }
            catch(Exception el){
              serverUI.display("Cannot listen for new clients");
            }
          }
          else{ //server must be stopped
            serverUI.display("Server must be stopped");
          }
      }
        else if(message.equals("getport")){
          serverUI.display(Integer.toString(getPort())); //obtains the port number for the server
        }
      }
      else{
        serverUI.display(message);
        this.sendToAllClients("SERVER MSG> " + message);
      }
    }
      catch(IOException e){
        serverUI.display("Could not send message from server. Terminating server... ");
        System.exit(1);
      }
  }
    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    System.out.println("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    System.out.println("Server has stopped listening for connections.");
  }
  
  //Notifying when client has disconnected from server A.M.
  protected void clientConnected(ConnectionToClient client) {
    System.out.println("A client has connected to the server!");
  }
  
  //Notifying when client has connected from server A.M.
  synchronized protected void clientDisconnected(ConnectionToClient client) {
    System.out.println(client.getInfo("loginID") + " has disconnected from the server.");
    this.sendToAllClients(client.getInfo("loginID") + " has disconnected from the server.");
  }
  

  synchronized protected void clientException(ConnectionToClient client, Throwable exception) {
    clientDisconnected(client);
  }


  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of 
   * the server instance (there is no UI in this phase).
   *
   * @param args[0] The port number to listen on.  Defaults to 5555 
   *          if no argument is entered.
   */
  public static void main(String[] args) 
  {
    int port = 0; //Port to listen on

    try
    {
      port = Integer.parseInt(args[0]); //Get port from command line
    }
    catch(Throwable t)
    {
      port = DEFAULT_PORT; //Set port to 5555
    }
	
    EchoServer sv = new EchoServer(port);
    
    try 
    {
      sv.listen(); //Start listening for connections
    } 
    catch (Exception ex) 
    {
      System.out.println("ERROR - Could not listen for clients!");
    }
  }
}
//End of EchoServer class
