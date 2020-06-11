package edu.northeastern.ccs.im.messages.clientsidemessages;

import edu.northeastern.ccs.im.messages.AbstractMessageType;
import edu.northeastern.ccs.im.messages.MessageTypes;

/**
 * Specifies a Register Message is to be selected.
 * A Register message is sent once a new user wants to Register for the service.
 */
public class RegisterMessage extends AbstractMessageType {

  /**
   * The abbreviated version of the Register message for brevity.
   * @return the abbreviated version of the Register Message.
   */
  @Override
  public String getAbbreviation() {
    return MessageTypes.REGISTER;
  }
}
