package edu.northeastern.ccs.im.messages.clientsidemessages;

import edu.northeastern.ccs.im.messages.AbstractMessageType;
import edu.northeastern.ccs.im.messages.MessageTypes;

/**
 * Specifies a Quit Message is to be selected.
 * A Quit message is sent once a currently online user wants to terminate his session
 */
public class QuitMessage extends AbstractMessageType {

  /**
   * The abbreviated version of the Quit message for brevity.
   * @return the abbreviated version of the Quit Message.
   */
  @Override
  public String getAbbreviation() {
    return MessageTypes.QUIT;
  }
}
