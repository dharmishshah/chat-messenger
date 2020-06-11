package edu.northeastern.ccs.im.messages.responsemessages;

import edu.northeastern.ccs.im.messages.AbstractMessageType;
import edu.northeastern.ccs.im.messages.MessageTypes;

/**
 * The type Failure message.
 */
public class FailureMessage extends AbstractMessageType {

  /**
   * Instantiates a new Failure message.
   *
   * @param params the params
   */
  public FailureMessage(String[] params) {
    super.extraParams = params;
  }

  @Override
  public String getAbbreviation() {
    return MessageTypes.FAILURE;
  }
}
