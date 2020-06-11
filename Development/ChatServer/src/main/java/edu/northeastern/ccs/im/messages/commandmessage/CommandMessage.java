
package edu.northeastern.ccs.im.messages.commandmessage;

import edu.northeastern.ccs.im.messages.AbstractMessageType;
import edu.northeastern.ccs.im.messages.MessageTypes;


/**
 * The type Command message.
 */
public class CommandMessage extends AbstractMessageType {

  public CommandMessage(String[] extra){
    super.extraParams = extra;
  }

  /**
   * The abbreviated version of the Command message for brevity.
   * @return the abbreviated version of the Command Message.
   */
  @Override
  public String getAbbreviation() {
    return MessageTypes.COMMAND;
  }
}
