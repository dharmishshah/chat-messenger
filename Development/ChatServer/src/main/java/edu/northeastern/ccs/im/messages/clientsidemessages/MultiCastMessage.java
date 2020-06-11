package edu.northeastern.ccs.im.messages.clientsidemessages;

import edu.northeastern.ccs.im.messages.AbstractMessageType;
import edu.northeastern.ccs.im.messages.MessageTypes;

/**
 * The type Multi cast message.
 */
public class MultiCastMessage extends AbstractMessageType {

  public MultiCastMessage(){}
  public MultiCastMessage(String[] extraParams){
    super.extraParams = extraParams;
  }
  /**
   * The abbreviated version of the MultiCast message for brevity.
   * @return the abbreviated version of the MultiCast Message.
   */
  @Override
  public String getAbbreviation() {
    return MessageTypes.MULTICAST;
  }
}
