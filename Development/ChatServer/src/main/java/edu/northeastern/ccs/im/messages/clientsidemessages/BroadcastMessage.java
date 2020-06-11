package edu.northeastern.ccs.im.messages.clientsidemessages;

import edu.northeastern.ccs.im.messages.AbstractMessageType;
import edu.northeastern.ccs.im.messages.MessageTypes;

/**
 * Specifies a Broadcast Message is to be selected.
 * A Broadcast message is sent to everyone currently online in Prattle.
 */
public class BroadcastMessage extends AbstractMessageType {

  /**
   * The abbreviated version of the Broadcast message for brevity.
   * @return the abbreviated version of the Broadcast Message.
   */
  @Override
  public String getAbbreviation() {
    return MessageTypes.BROADCAST;
  }
}
