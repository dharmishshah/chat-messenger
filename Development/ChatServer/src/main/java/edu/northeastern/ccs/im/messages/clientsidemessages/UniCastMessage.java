package edu.northeastern.ccs.im.messages.clientsidemessages;

import edu.northeastern.ccs.im.messages.AbstractMessageType;
import edu.northeastern.ccs.im.messages.MessageTypes;

/**
 * Specifies a UniCast Message is to be selected.
 * A UniCast message is sent as per 1 to 1 person to person communication only.
 */
public class UniCastMessage extends AbstractMessageType {

  /**
   * Adding support for a Unicast message that also contains extra parameters.
   *
   * @param extraParam any extra parameters to be included in the unicast message.
   */
  public UniCastMessage(String[] extraParam) {
    super.extraParams = extraParam;
  }
  public UniCastMessage(){}

  /**
   * The abbreviated version of the UniCast message for brevity.
   *
   * @return the abbreviated version of the UniCast Message.
   */
  @Override
  public String getAbbreviation() {
    return MessageTypes.UNICAST;
  }
}
