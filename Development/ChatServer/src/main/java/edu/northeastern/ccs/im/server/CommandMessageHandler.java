package edu.northeastern.ccs.im.server;

import java.util.HashMap;
import java.util.Map;

import edu.northeastern.ccs.im.ChatLogger;
import edu.northeastern.ccs.im.Message;


/**
 * The type Command message handler.
 */
public class CommandMessageHandler extends ChatMessageHandler {

  private Map<String, Runnable> typeToRun = new HashMap<>();

  /**
   * Instantiates a new Command message handler.
   *
   * @param msg the msg
   */
  CommandMessageHandler(Message msg) {
    super(msg);
  }


  /**
   * Handles Command type Messages, these can be anything relating to adding and removing of Groups or Users.
   *
   * @param clientRunnable the ClientRunnable that sent the message being handled.
   */
  @Override
  void handleMessage(ClientRunnable clientRunnable) {
    String[] args = msg.getMsgType().extraParams();
    loadMap(clientRunnable);
    if(args[0].equals("JAG"))
    {
     Prattle.getInstance().joinAgroup(msg.getMsgReceiver(),clientRunnable);
    }
    else if(args[0].equals("CRT"))
    {
      Prattle.getInstance().createGroup(msg.getMsgReceiver(), clientRunnable);
    }
    else if(args[0].equals("RAG"))
    {
      Prattle.getInstance().retrieveAllPublicGroup(clientRunnable);
    }
    else if(args[0].equals("AMD"))
    {
      Prattle.getInstance().addModerator(msg.getMsgReceiver(),msg.getUserName(),clientRunnable);
    }
    else if (!typeToRun.containsKey(args[0])) {
      ChatLogger.info("invalid message");
    } else {
      typeToRun.get(args[0]);
    }
  }

  /**
   * Loads up our Type of Message, to appropriate Runnable HashMap with our currently available options.
   *
   * @param clientRunnable the client runnable we are going to use with our messaging.
   */
  private void loadMap(ClientRunnable clientRunnable) {
    typeToRun.put("CRT", () -> Prattle.getInstance().createGroup(msg.getMsgReceiver(), clientRunnable));
    typeToRun.put("ADM", () -> Prattle.getInstance().addMember(msg.getMsgReceiver(), msg.getUserName(), clientRunnable));
    typeToRun.put("RMM", () -> Prattle.getInstance().removeMember(msg.getMsgReceiver(), msg.getUserName(), clientRunnable));
    typeToRun.put("DLG", () -> Prattle.getInstance().deleteGroup(msg.getMsgReceiver(), clientRunnable));
    typeToRun.put("RAG",() -> Prattle.getInstance().retrieveAllPublicGroup(clientRunnable));
    typeToRun.put("JAG",() -> Prattle.getInstance().joinAgroup(msg.getMsgReceiver(),clientRunnable));
  }

}
