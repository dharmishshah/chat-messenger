package edu.northeastern.ccs.im.messages.responsemessages;

import edu.northeastern.ccs.im.messages.AbstractMessageType;
import edu.northeastern.ccs.im.messages.MessageTypes;

/**
 * The type Success message.
 */
public class SuccessMessage extends AbstractMessageType {


  /**
   * Instantiates a new Success message.
   *
   * @param extraParams the extra params
   */
  public SuccessMessage(String[] extraParams) {
    super.extraParams = extraParams;
  }

  @Override
  public String getAbbreviation() {
    return MessageTypes.SUCCESS;
  }
}
