package edu.northeastern.ccs.im.server;

import edu.northeastern.ccs.im.Message;
import edu.northeastern.ccs.im.messages.MessageTypes;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * A basis for all handlers for incoming messages using a Strategy pattern.
 * Also provides a static method to choose the appropriate message handler
 * by MessageTypes.
 */
public abstract class IncomingMessageHandler {
  /**
   * The message being handled. Common between all handlers.
   */
  protected Message msg;

  /**
   * Map of handlers for various message types.
   */
  private static final Map<String, Function<Message,IncomingMessageHandler>> handlerMap = new HashMap<>();

  static {
    handlerMap.put(MessageTypes.QUIT,       msg -> new QuitMessageHandler(msg));
    handlerMap.put(MessageTypes.BROADCAST,  msg -> new BroadcastMessageHandler(msg));
    handlerMap.put(MessageTypes.REGISTER,   msg -> new RegistrationMessageHandler(msg));
    handlerMap.put(MessageTypes.LOGIN,      msg -> new LoginMessageHandler(msg));
    handlerMap.put(MessageTypes.UNICAST,    msg -> new UnicastMessageHandler(msg));
    handlerMap.put(MessageTypes.MULTICAST,  msg -> new MulticastMessageHandler(msg));
    handlerMap.put(MessageTypes.COMMAND,    msg -> new CommandMessageHandler(msg));
  }


  protected IncomingMessageHandler(Message message) {
    this.msg = message;
  }

  /**
   * Choose the appropriate message handler based on the incoming message.
   * Pretty much just checks the message type.
   *
   * @param message the incoming message that needs handling.
   * @return the appropriate message handler
   */
  public static IncomingMessageHandler choose(Message message) {
    if (handlerMap.containsKey(message.messageType())) {
      return handlerMap.get(message.messageType()).apply(message);
    } else {
      throw new IllegalArgumentException("Invalid message type for incoming message handler.");
    }
  }

  /**
   * Run the message handler's handling procedure.
   *
   * @param clientRunnable the client runnable that received the message,
   *                       for use in the handling if needed.
   */
  public abstract void run(ClientRunnable clientRunnable);
}
