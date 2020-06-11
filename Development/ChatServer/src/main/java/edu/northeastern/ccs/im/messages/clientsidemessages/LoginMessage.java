package edu.northeastern.ccs.im.messages.clientsidemessages;

import edu.northeastern.ccs.im.messages.AbstractMessageType;
import edu.northeastern.ccs.im.messages.MessageTypes;

/**
 * Specifies a Login Message is to be selected.
 * A Login message is sent once a user wants to login into the server.
 */
public class LoginMessage extends AbstractMessageType {

  /**
   * The abbreviated version of the Login message for brevity.
   * @return the abbreviated version of the Login Message.
   */
  @Override
  public String getAbbreviation() {
    return MessageTypes.LOGIN;
  }
}
