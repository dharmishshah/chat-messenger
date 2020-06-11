package edu.northeastern.ccs.im.server.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import edu.northeastern.ccs.im.Message;
import edu.northeastern.ccs.im.messages.MessageTypes;


/**
 * Parses a JSON String Object into a Message Class Object.
 */
public class JsonParserImpl {

  private static Map<String, Message> messageTypeToMessageMap = new HashMap<>();
  private static Gson jObj = new Gson();
  private static Type mapType = new TypeToken<Map<String, String>>() {
  }.getType();
  private static Map<String, String> jMap;

  private static final String DEST_KEY = "destination";
  private static final String ADMIN_KEY = "admin";
  private static final String MEMBER_KEY = "member";
  private static final String USERNAME_KEY = "username";
  private static final String PS_KEY = "password";
  private static final String TEXT_KEY = "text";
  private static final String HANDSHAKE_KEY = "handshake";

  /**
   * Hiding the default constructor, as we don't want to instantiate this class.
   */
  private JsonParserImpl() {
  }

  /**
   * Makes a Message from a supplied JSON String Object.
   *
   * @param jsonString the message to be formatted given in JSON String format.
   * @return the JSON Object formatted as a Message.
   */
  public static Message makeMessageFromJSON(String jsonString) {
    jMap = jObj.fromJson(jsonString, mapType);
    String messageType = jMap.get("type");
    loadMap(messageType);
    return messageTypeToMessageMap.get(messageType);
  }

  /**
   * The main method that loads all the server operations per message type into the Map.
   *
   * @param messageType the type of Message that the JSON contains.
   */
  private static void loadMap(String messageType) {
    loadMessageOps();
    loadGroupOps(messageType);
  }

  /**
   * Loading all the default Message Operations into the map, such as Login, Register, Unicast and Multicast.
   */
  private static void loadMessageOps() {
    messageTypeToMessageMap.put(MessageTypes.LOGIN, Message.makeLoginMessage(jMap.get(USERNAME_KEY), jMap.get(PS_KEY)));
    messageTypeToMessageMap.put(MessageTypes.REGISTER, Message.makeRegisterMessage(jMap.get(USERNAME_KEY), jMap.get(PS_KEY)));
    if (jMap.containsKey(HANDSHAKE_KEY)) {
      messageTypeToMessageMap.put(MessageTypes.UNICAST, Message.makeUniCastMessage(jMap.get(TEXT_KEY), jMap.get(DEST_KEY), jMap.get(HANDSHAKE_KEY)));
    } else {
      messageTypeToMessageMap.put(MessageTypes.UNICAST, Message.makeUniCastMessage(jMap.get(TEXT_KEY), jMap.get(DEST_KEY)));
    }
    messageTypeToMessageMap.put(MessageTypes.MULTICAST, Message.makeMultiCastMessage(jMap.get(TEXT_KEY), jMap.get(DEST_KEY)));
  }

  /**
   * Loading all group-based operations into the map, such as Create Group, Delete Group, Add Member and Delete Member.
   *
   * @param messageType the types of messages, as mentioned in the comment above.
   */
  private static void loadGroupOps(String messageType) {
    String[] adminOnly = new String[1];
    adminOnly[0] = jMap.get(ADMIN_KEY);
    messageTypeToMessageMap.put("CRT", Message.makeCommandMessage(messageType, jMap.get(DEST_KEY), adminOnly));
    messageTypeToMessageMap.put("DLG", Message.makeCommandMessage(messageType, jMap.get(DEST_KEY), adminOnly));
    String[] names = new String[2];
    names[0] = jMap.get(ADMIN_KEY);
    names[1] = jMap.get(MEMBER_KEY);

    String[] addMod = new String[2];
    addMod[0] =  jMap.get(ADMIN_KEY);
    addMod[1] = jMap.get(USERNAME_KEY);
    messageTypeToMessageMap.put("AMD", Message.makeCommandMessage(messageType,jMap.get(DEST_KEY), addMod));
    messageTypeToMessageMap.put("RMM", Message.makeCommandMessage(messageType, jMap.get(DEST_KEY), names));
    messageTypeToMessageMap.put("RAG", Message.makeCommandMessage(messageType));

    String[] onlyMember = new String[1];
    onlyMember[0] = jMap.get(MEMBER_KEY);

    messageTypeToMessageMap.put("AMM", Message.makeCommandMessage(messageType, jMap.get(DEST_KEY), names));
    messageTypeToMessageMap.put("JAG", Message.makeCommandMessage(messageType, jMap.get(DEST_KEY), onlyMember));

  }
}

