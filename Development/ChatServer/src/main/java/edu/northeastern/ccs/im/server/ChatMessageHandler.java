package edu.northeastern.ccs.im.server;


import edu.northeastern.ccs.im.Message;

/**
 * A base class for handlers of chat messages. Intended to contain the global checks
 * applicable to all chat message types. All message handlers for chat messages
 * (ie group messages or private user to user messages) should extend this class and
 * implement the handleMessage method to complete the implementation.
 * See BroadcastMessageHandler for example implementation.
 */
abstract class ChatMessageHandler extends IncomingMessageHandler {

  ChatMessageHandler(Message message) {
    super(message);
  }

  @Override
  public final void run(ClientRunnable clientRunnable) {

    // Check if the message is legally formatted
    if (clientRunnable.messageChecks(msg) || clientRunnable.getName() != null) {
      this.handleMessage(clientRunnable);
    } else {
      Message sendMsg = Message.makeBroadcastMessage(ServerConstants.BOUNCER_ID,
              "Last message was rejected because it specified an incorrect user name.");
      clientRunnable.enqueueMessage(sendMsg);
    }
  }

  /**
   * Implemented in child classes to handle the type specific sending of messages.
   *
   * @param clientRunnable the ClientRunnable that sent the message being handled.
   */
  abstract void handleMessage(ClientRunnable clientRunnable);
}
