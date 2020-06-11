package edu.northeastern.ccs.im.messages;

/**
 * Specifies an Abstract Message Type for which all other types of Messages can be,
 * descendent from.
 */
public abstract class AbstractMessageType implements IMessageType {

  protected String[] extraParams;

  /**
   * The Constructor, since we only care about the methods, this is blank by default.
   */
  public AbstractMessageType() {
    this.extraParams = new String[0];
  }


  /**
   * By default there are no extra params related to a Message Type so we return an empty array.
   *
   * @return an empty array containing no extra parameters.
   */
  public String[] extraParams() {
    return this.extraParams;
  }

}
