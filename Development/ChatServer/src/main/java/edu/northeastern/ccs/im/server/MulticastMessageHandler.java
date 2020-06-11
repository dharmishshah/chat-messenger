package edu.northeastern.ccs.im.server;

import edu.northeastern.ccs.im.Message;

/**
 * The type Multicast message handler.
 */
public class MulticastMessageHandler extends ChatMessageHandler{

  /**
   * Instantiates a new Multicast message handler.
   *
   * @param msg the msg
   */
  public MulticastMessageHandler(Message msg){
    super(msg);
  }
  @Override
  void handleMessage(ClientRunnable clientRunnable) {
    Prattle.getInstance().multicastMessage(msg,clientRunnable);
  }
}
