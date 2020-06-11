package edu.northeastern.ccs.im;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import edu.northeastern.ccs.im.messages.IMessageType;
import edu.northeastern.ccs.im.messages.MessageTypes;
import edu.northeastern.ccs.im.messages.clientsidemessages.BroadcastMessage;
import edu.northeastern.ccs.im.messages.clientsidemessages.LoginMessage;
import edu.northeastern.ccs.im.messages.clientsidemessages.MultiCastMessage;
import edu.northeastern.ccs.im.messages.clientsidemessages.QuitMessage;
import edu.northeastern.ccs.im.messages.clientsidemessages.RegisterMessage;
import edu.northeastern.ccs.im.messages.clientsidemessages.SimpleLoginMessage;
import edu.northeastern.ccs.im.messages.clientsidemessages.UniCastMessage;
import edu.northeastern.ccs.im.messages.commandmessage.CommandMessage;
import edu.northeastern.ccs.im.messages.responsemessages.FailureMessage;
import edu.northeastern.ccs.im.messages.responsemessages.SuccessMessage;

/**
 * Each instance of this class represents a single transmission by our IM
 * clients.
 * <p>
 * This work is licensed under the Creative Commons Attribution-ShareAlike 4.0
 * International License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by-sa/4.0/. It is based on work
 * originally written by Matthew Hertz and has been adapted for use in a class
 * assignment at Northeastern University.
 *
 * @version 1.3
 */
public class Message {

  /**
   * The string sent when a field is null.
   */
  private static final String NULL_OUTPUT = "--";

  /**
   * The handle of the message.
   */
  private IMessageType msgType;

  /**
   * The first argument used in the message. This will be the sender's identifier.
   */
  private String msgSender;

  /**
   * The second argument used in the message.
   */
  private String msgText;

  /**
   * The destination identification of the message.
   */
  private String msgReceiver;

  /**
   * The username of the potential user.
   */
  private String username;

  /**
   * The password of the potential user.
   */
  private String passwordHold;


  private static Map<String, Message> typeToMessageMap = new HashMap<>();

  /**
   * Create a new message that contains response of type success.
   *
   * @param handle Handle for the type of message being created.
   */
  private Message(IMessageType handle) {
    msgType = handle;
  }

  /**
   * Create a new message that contains a command sent the server that requires a
   * single argument. This message contains the given handle and the single
   * argument.
   *
   * @param handle  Handle for the type of message being created.
   * @param srcName Argument for the message; at present this is the name used to
   *                log-in to the IM server.
   */
  private Message(IMessageType handle, String srcName) {
    this.msgType = handle;
    this.msgSender = srcName;
  }

  /**
   * Create a new message that contains actual IM text. The type of distribution
   * is defined by the handle and we must also set the name of the message sender,
   * message recipient, and the text to send.
   *
   * @param handle  Handle for the type of message being created.
   * @param srcName Name of the individual sending this message
   * @param text    Text of the instant message
   */
  private Message(IMessageType handle, String srcName, String text) {

    msgType = handle;
    // Save the properly formatted identifier for the user sending the
    // message.
    if (msgType.getAbbreviation().equals(MessageTypes.UNICAST)
        || msgType.getAbbreviation().equals(MessageTypes.MULTICAST)) {
      msgReceiver = srcName;
    } else {
      msgSender = srcName;
    }
    // Save the text of the message.
    msgText = text;
  }

  /**
   * A constructor to create a Command type of Message specifically.
   *
   * @param handle       the type of command message we are creating.
   * @param groupName    the name of the group being affected.
   * @param personalName includes an Admin's name and possibly an affected user.
   */
  private Message(IMessageType handle, String groupName, String[] personalName) {
    this.msgType = handle;
    this.msgReceiver = groupName;
    if (personalName != null && personalName.length != 0) {
      if (personalName.length == 1) {
        this.msgSender = personalName[0];
      } else {
        this.msgSender = personalName[0];
        this.username = personalName[1];
      }
    }
  }


  /**
   * Create a new message that contains actual IM text. The type of distribution
   * is defined by the handle and we must also set the name of the message sender,
   * message recipient, and the text to send.
   *
   * @param handle   Handle for the type of message being created.
   * @param srcName  Name of the individual sending this message
   * @param destName Destination location of the message
   * @param text     Text of the instant message
   * @param userName The user name of the person attempting to login/register.
   * @param password The password of the person attempting to login/register.
   */
  private Message(IMessageType handle, String srcName, String destName, String text,
                  String userName, String password) {
    msgType = handle;
    msgSender = srcName;
    msgReceiver = destName;
    msgText = text;
    username = userName;
    passwordHold = password;
  }


  /**
   * Creates a new Command Message, for operations on Users and Groups
   *
   * @param messageType  the type of Message we are creating.
   * @param groupName    the name of the group being affected.
   * @param personalName includes an Admin's name and possible a user's name also.
   * @return a new Command Message, featuring all of the above.
   */
  public static Message makeCommandMessage(String messageType, String groupName, String[] personalName) {
    String[] params = new String[1];
    params[0] = messageType;
    IMessageType typeOfCommand = new CommandMessage(params);
    return new Message(typeOfCommand, groupName, personalName);
  }

  /**
   * Creates a new Command Message, for making auxiliary Command Messages
   *
   * @param messageType the type of Message we are creating.
   * @return a new Command Message, featuring all of the above.
   */
  public static Message makeCommandMessage(String messageType) {
    String[] params = new String[1];
    params[0] = messageType;
    IMessageType typeOfCommand = new CommandMessage(params);
    return new Message(typeOfCommand);
  }


  /**
   * Create a new message to continue the logout process.
   *
   * @param myName The name of the client that sent the quit message.
   * @return Instance of messages that specifies the process is logging out.
   */
  public static Message makeQuitMessage(String myName) {
    IMessageType quitMsg = new QuitMessage();
    return new Message(quitMsg, myName);
  }

  /**
   * Create a new message broadcasting an announcement to the world.
   *
   * @param myName Name of the sender of this very important missive.
   * @param text   Text of the message that will be sent to all users
   * @return Instance of messages that transmits text to all logged in users.
   */
  public static Message makeBroadcastMessage(String myName, String text) {
    IMessageType broadcastMsg = new BroadcastMessage();
    return new Message(broadcastMsg, myName, text);
  }

  /**
   * Create a new message stating the name with which the user would like to
   * login.
   *
   * @param username the username
   * @param password the password
   * @return Instance of messages that can be sent to the server to try and login.
   */
  public static Message makeHelloMessage(String username, String password) {
    IMessageType loginMsg = new LoginMessage();
    return new Message(loginMsg, null, null, null, username, password);
  }

  /**
   * Creates a new Multi-Cast Message. A Multi cast message is sent to more than one user,
   * but not the entire Prattle population.
   *
   * @param text The text of the message.
   * @param dest The destination to where we will send the message.
   * @return a newly created Multi-Cast Message.
   */
  public static Message makeMultiCastMessage(String text, String dest) {
    IMessageType multiCast = new MultiCastMessage();
    return new Message(multiCast, dest, text);
  }

  /**
   * Creates a new Multi-Cast Message. A Multi cast message is sent to more than one user,
   * but not the entire Prattle population. Also includes the extra-params field filled.
   *
   * @param text        The text of the message.
   * @param dest        The destination to where we will send the message.
   * @param extraParams any extra parameters we want to pass
   * @return a newly created Multi-Cast Message.
   */
  public static Message makeMultiCastMessage(String text, String dest, String extraParams) {
    String[] extra = new String[1];
    extra[0] = extraParams;
    IMessageType multicast = new MultiCastMessage(extra);
    return new Message(multicast, dest, text);
  }

  /**
   * Creates a new Uni-Cast Message. A Uni-Cast Message is sent between two users only, 1-to-1.
   *
   * @param text The text of the message.
   * @param dest The unique identifier of the recipient.
   * @return a newly created Uni-Cast Message.
   */
  public static Message makeUniCastMessage(String text, String dest) {
    IMessageType uniCast = new UniCastMessage();
    return new Message(uniCast, dest, text);
  }


  /**
   * Creates a new Uni-Cast Message. A Uni-Cast Message is sent between two users only, 1-to-1,
   * includes a special extra-params field for specialized
   *
   * @param text       The text of the message.
   * @param dest       The unique identifier of the recipient.
   * @param extraParam any extra parameters we want to pass
   * @return a newly created Uni-Cast Message.
   */
  public static Message makeUniCastMessage(String text, String dest, String extraParam) {
    String[] hs = new String[1];
    hs[0] = extraParam;
    IMessageType unicast = new UniCastMessage(hs);
    return new Message(unicast, dest, text);
  }

  /**
   * Gets msg sender.
   *
   * @return the msg sender
   */
  public String getMsgSender() {
    return msgSender;
  }

  /**
   * Gets msg text.
   *
   * @return the msg text
   */
  public String getMsgText() {
    return msgText;
  }

  /**
   * Gets user name hold.
   *
   * @return the user name hold
   */
  public String getUsername() {
    return username;
  }

  /**
   * Sets msg type.
   *
   * @param msgType the msg type
   */
  public void setMsgType(IMessageType msgType) {
    this.msgType = msgType;
  }

  /**
   * Sets msg sender.
   *
   * @param msgSender the msg sender
   */
  public void setMsgSender(String msgSender) {
    this.msgSender = msgSender;
  }

  /**
   * Sets msg text.
   *
   * @param msgText the msg text
   */
  public void setMsgText(String msgText) {
    this.msgText = msgText;
  }

  /**
   * Sets msg receiver.
   *
   * @param msgReceiver the msg receiver
   */
  public void setMsgReceiver(String msgReceiver) {
    this.msgReceiver = msgReceiver;
  }

  /**
   * Sets user name hold.
   *
   * @param username the user name hold
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * Sets password hold.
   *
   * @param passwordHold the password hold
   */
  public void setPasswordHold(String passwordHold) {
    this.passwordHold = passwordHold;
  }

  /**
   * Gets password hold.
   *
   * @return the password hold
   */
  public String getPasswordHold() {
    return passwordHold;
  }

  /**
   * Make register message message.
   *
   * @param userName the user name
   * @param password the password
   * @return the message
   */
  public static Message makeRegisterMessage(String userName,String password) {
    IMessageType register = new RegisterMessage();
    return new Message(register, userName, null, null, userName, password);
  }


  /**
   * Gets msg type.
   *
   * @return the msg type
   */
  public IMessageType getMsgType() {
    return msgType;
  }

  /**
   * Gets msg receiver.
   *
   * @return the msg receiver
   */
  public String getMsgReceiver() {
    return msgReceiver;
  }

  /**
   * Gets user name.
   *
   * @return the user name
   */
  public String getUserName() {
    return username;
  }

  /**
   * Gets password.
   *
   * @return the password
   */
  public String getPassword() {
    return passwordHold;
  }


  /**
   * Given a handle, name and text, return the appropriate message instance or an
   * instance from a subclass of message.
   *
   * @param handle   Handle of the message to be generated.
   * @param srcName  Name of the originator of the message (may be null)
   * @param destName the dest name
   * @param text     Text sent in this message (may be null)
   * @param userName the user name
   * @param password the password
   * @return Instance of messages (or its subclasses) representing the handle, name, & text.
   */
  public static Message makeMessage(String handle,
                                    String srcName,
                                    String destName,
                                    String text,
                                    String userName,
                                    String password) {
    typeToMessageMap.clear();
    typeToMessageMap.put(MessageTypes.QUIT, makeQuitMessage(srcName));
    typeToMessageMap.put(MessageTypes.LOGIN, makeLoginMessage(userName, password));
    typeToMessageMap.put(MessageTypes.BROADCAST, makeBroadcastMessage(srcName, text));
    typeToMessageMap.put(MessageTypes.MULTICAST, makeMultiCastMessage(text, destName));
    typeToMessageMap.put(MessageTypes.UNICAST, makeUniCastMessage(text, destName));
    typeToMessageMap.put(MessageTypes.REGISTER, makeRegisterMessage(userName, password));
    typeToMessageMap.put(MessageTypes.SUCCESS, makeSuccessMessage());
    typeToMessageMap.put(MessageTypes.FAILURE, makeFailureMessage());
    return typeToMessageMap.get(handle);
  }


  /**
   * Create a new message for the early stages when the user logs in without all
   * the special stuff.
   *
   * @param myName Name of the user who has just logged in.
   * @return Instance of messages specifying a new friend has just logged in.
   */
  public static Message makeSimpleLoginMessage(String myName) {
    IMessageType loginMsg = new SimpleLoginMessage();
    return new Message(loginMsg, myName);
  }

  /**
   * Make success message message.
   *
   * @return the message
   */
  public static Message makeSuccessMessage() {
    String[] params = new String[1];
    params[0] = "100";
    IMessageType successMsg = new SuccessMessage(params);
    return new Message(successMsg);
  }

  /**
   * Make failure message message.
   *
   * @return the message
   */
  public static Message makeFailureMessage() {
    String[] params = new String[1];
    params[0] = "200";
    IMessageType failureMsg = new FailureMessage(params);
    return new Message(failureMsg);
  }

  /**
   * Create a new message for the early stages when the user logs in with password
   *
   * @param myName   Name of the user who has just logged in.
   * @param password password of the user
   * @return Instance of messages specifying a new friend has just logged in.
   */
  public static Message makeLoginMessage(String myName, String password) {
    IMessageType loginMsg = new LoginMessage();
    return new Message(loginMsg, myName, null, null, myName, password);
  }

  /**
   * Return the name of the sender of this message.
   *
   * @return String specifying the name of the message originator.
   */
  public String getName() {
    return msgSender;
  }

  /**
   * Return the text of this message.
   *
   * @return String equal to the text sent by this message.
   */
  public String getText() {
    return msgText;
  }

  /**
   * Determine if this message is broadcasting text to everyone.
   *
   * @return True if the message is a broadcast message; false otherwise.
   */
  public boolean isBroadcastMessage() {
    return msgType.getAbbreviation().equals(MessageTypes.BROADCAST);
  }

  /**
   * Determine if this message is sent by a new client to log-in to the server.
   *
   * @return True if the message is an initialization message; false otherwise
   */
  public boolean isInitialization() {
    return msgType.getAbbreviation().equals(MessageTypes.SIMPLE_LOGIN);
  }

  /**
   * Determine if this message is a message signing off from the IM server.
   *
   * @return True if the message is sent when signing off; false otherwise
   */
  public boolean terminate() {
    return msgType.getAbbreviation().equals(MessageTypes.QUIT);
  }

  /**
   * Getter for the MessageType of this message.
   *
   * @return The MessageType abbreviation of the message.
   */
  public String messageType() {
    return this.msgType.getAbbreviation();
  }

  /**
   * Representation of this message as a String. This begins with the message
   * handle and then contains the length (as an integer) and the value of the next
   * two arguments.
   *
   * @return Representation of this message as a String.
   */
  @Override
  public String toString() {
    String result = msgType.getAbbreviation();
    if (msgSender != null) {
      result += " " + msgSender.length() + " " + msgSender;
    } else {
      result += " " + NULL_OUTPUT.length() + " " + NULL_OUTPUT;
    }
    if (msgText != null) {
      result += " " + msgText.length() + " " + msgText;
    } else {
      result += " " + NULL_OUTPUT.length() + " " + NULL_OUTPUT;
    }
    return result;
  }
}
