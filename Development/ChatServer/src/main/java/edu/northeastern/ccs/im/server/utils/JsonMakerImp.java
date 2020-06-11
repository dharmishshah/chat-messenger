package edu.northeastern.ccs.im.server.utils;

import edu.northeastern.ccs.im.Message;
import edu.northeastern.ccs.im.messages.IMessageType;


/**
 * The type Json maker imp.
 */
public class JsonMakerImp {
  private JsonMakerImp() {
  }

  private static final String START = "{";
  private static final String END = "}";

  private static final String TYPE_KEY = "\"type\":";
  private static final String TEXT_KEY = "\"text\":";
  private static final String SENDER_KEY = "\"sender\":";
  private static final String DESTINATION_KEY = "\"destination\":";

  private static final String HANDSHAKE_KEY = "\"handshake\":";

  private static final String SEPARATOR_KEY = ",";

  /**
   * Make json string.
   *
   * @param message the message
   * @return the string
   */
  public static String makeJson(Message message) {
    String typeOf = message.getMsgType().getAbbreviation();
    StringBuilder buildUp = new StringBuilder();
    buildUp.append(START).append(TYPE_KEY).append(addQuotes(typeOf)).append(SEPARATOR_KEY);
    buildUp.append(TEXT_KEY).append(addQuotes(message.getText())).append(SEPARATOR_KEY);
    buildUp.append(SENDER_KEY).append(addQuotes(message.getMsgSender())).append(SEPARATOR_KEY);
    buildUp.append(DESTINATION_KEY).append(addQuotes(message.getMsgReceiver()));
    IMessageType extra = message.getMsgType();
    String[] extraParamsArr = extra.extraParams();

    if (extraParamsArr.length >= 1 &&
            (extraParamsArr[0].equalsIgnoreCase("true")
                    || extraParamsArr[0].equalsIgnoreCase("false"))) {
      buildUp.append(SEPARATOR_KEY).append(HANDSHAKE_KEY).append(extraParamsArr[0]);

    }
    buildUp.append(END);

    return new String(buildUp);
  }

  /**
   * Adds quotes around a passed in String.
   * @param input the passed in String to add quotes to.
   * @return the same passed in String, enclosed in quotes.
   */
  private static String addQuotes(String input) {
    StringBuilder quoteUp = new StringBuilder();
    quoteUp.append("\"");
    quoteUp.append(input);
    quoteUp.append("\"");
    return new String(quoteUp);
  }
}