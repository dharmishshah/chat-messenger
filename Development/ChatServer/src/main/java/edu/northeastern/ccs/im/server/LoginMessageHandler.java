package edu.northeastern.ccs.im.server;

import edu.northeastern.ccs.im.Message;

/**
 * The type Login message handler.
 */
public class LoginMessageHandler extends ChatMessageHandler{

  /**
   * Instantiates a new Login message handler.
   *
   * @param msg the msg
   */
  LoginMessageHandler(Message msg){super(msg);}
  @Override
  void handleMessage(ClientRunnable clientRunnable) {
    Prattle.getInstance().loginClient(msg,clientRunnable);

  }
}
