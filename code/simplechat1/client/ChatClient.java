// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package client;

import ocsf.client.*;
import common.*;
import java.io.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @version July 2000
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 

  private String loginID;

  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String host, int port, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    this.loginID = "Guest";
    openConnection();
    sendToServer("#loginID " + loginID);
  }

  public ChatClient(String loginID, String host, int port, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    this.loginID = loginID; 
    openConnection();
    sendToServer("#loginID " + loginID);
  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
      clientUI.display(msg.toString());
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {
    try
    {
      if(message.charAt(0) == '#'){
        if(message.equals("#quit")){
          quit();
          clientUI.display("The connection to the server has been terminated");
        }
        else if(message.equals("#logoff")){
          clientUI.display("Logging off...");
          closeConnection();
        }
        else if(message.startsWith("#sethost")){
          if(!isConnected()){ //client are all logged off 
            try{
              String hostName = message.substring(9); //host name begins after the space and starts at 9th index
              setHost(hostName);
              clientUI.display("The new host is set to: " + hostName);
            }
            catch(Exception e){
              clientUI.display("Error... couldn't set host");
            }
          }
          else{
              System.out.println("Client is still connected to server! Cannot set an host...");
            }
        }
        else if(message.startsWith("#setport")){
           if(!isConnected()){ //client are all logged off 
            try{
              String setPort = message.substring(9); //port number begins after the space and starts at 9th index
              int setPortInt = Integer.parseInt(setPort);
              setPort(setPortInt);
              clientUI.display("The new port is set to: " + setPortInt);
            }
            catch(Exception e){
              clientUI.display("Error... couldn't set the port");
            }
          }
          else{
             clientUI.display("Client is still connected to server! Cannot set a port...");
            }
        }
        else if(message.equals("#login")){
          if(!isConnected()){ //client are all logged off 
            openConnection();
          }else{
            clientUI.display("Error ... Must disconnect from the server!");
          }
        }
        else if(message.equals("#gethost")){
          clientUI.display(getHost());
        }
        else if(message.equals("#getport")){
          clientUI.display(Integer.toString(getPort()));
        }
        else if(message.startsWith("#")){
          clientUI.display("Invalid command!");
        }
      }else{
        sendToServer(message);
      }
    }
    catch(IOException e)
    {
      clientUI.display("Could not send message to server.  Terminating client.");
      quit();
    }
  }

  //Hook method overridin from AbstractClient (A.M.)
  public void connectionClosed(){
    System.out.println("Server has shut down and quitting!");
  }

  //Hook method overridin from AbstractClient (A.M.)
  public void connectionException(){
    quit();
  }

  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }
}
//End of ChatClient class
  