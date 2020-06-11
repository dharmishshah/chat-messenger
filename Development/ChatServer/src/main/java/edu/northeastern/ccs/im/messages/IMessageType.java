package edu.northeastern.ccs.im.messages;

/**
 * The interface against which all Message types are bound. This specifies that each,
 * message type must report its full name, it's abbreviated call sign,
 * as well as any extra valid parameters.
 */
public interface IMessageType {

  /**
   * The abbreviated version of each Message, a call sign in other words.
   * @return the call sign of each message.
   */
  String getAbbreviation();

  /**
   * If the message contains any extra special parameters.
   * @return An Array of special parameters that is optional.
   */
  String[] extraParams();
}
