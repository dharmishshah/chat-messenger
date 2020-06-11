package edu.northeastern.ccs.im.server.utils;


import org.junit.jupiter.api.Test;

import java.util.Arrays;

import edu.northeastern.ccs.im.Message;
import edu.northeastern.ccs.im.messages.IMessageType;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonParserImplTest {


  /**
   * Tests correctly making a Login Message from a JSON String Object.
   */
  @Test
  void makeLoginFromJSON() {
    String sampleJson = "{\"type\":\"LOG\",\"username\":\"John\",\"password\":\"qwerty\"}";
    Message makeLogin = JsonParserImpl.makeMessageFromJSON(sampleJson);
    assert makeLogin != null;
    assertEquals("qwerty", makeLogin.getPassword());
    assertEquals("John", makeLogin.getUserName());
    IMessageType msgType = makeLogin.getMsgType();
    assertEquals("LOG", msgType.getAbbreviation());
  }

  @Test
  void makeBadJson(){
    String sampleJson = "{\"type\":\"LOG\",\"password\":\"qwerty\"}";
    Message makeLogin = JsonParserImpl.makeMessageFromJSON(sampleJson);
    assert makeLogin != null;
    assertEquals("qwerty", makeLogin.getPassword());
   // assertEquals("John", makeLogin.getUserName());
    IMessageType msgType = makeLogin.getMsgType();
    assertEquals("LOG", msgType.getAbbreviation());
  }

  /**
   * Tests correctly making a Register Message from a JSON String Object.
   */
  @Test
  void makeRegisterFromJSON() {
    String sampleJson = "{\"type\":\"RGT\",\"username\":\"John\",\"password\":\"qwerty\"}";
    Message makeLogin = JsonParserImpl.makeMessageFromJSON(sampleJson);
    assert makeLogin != null;
    assertEquals("qwerty", makeLogin.getPassword());
    assertEquals("John", makeLogin.getUserName());
    IMessageType msgType = makeLogin.getMsgType();
    assertEquals("RGT", msgType.getAbbreviation());
  }


  /**
   * Tests correctly making a Unicast Message from a JSON String Object.
   */
  @Test
  void makeUCMFromJSON() {
    String sampleJson = "{\"type\":\"UCM\",\"text\":\"Hey Terry this is Jonathan, how have you been? Hope to talk to you again soon, Jonathan\",\"destination\":\"Terry\"}";
    Message makeUCM = JsonParserImpl.makeMessageFromJSON(sampleJson);
    assert makeUCM != null;
    assertEquals("Hey Terry this is Jonathan, how have you been? Hope to talk to you again soon, Jonathan", makeUCM.getText());
    assertEquals("Terry", makeUCM.getMsgReceiver());
    IMessageType msgType = makeUCM.getMsgType();
    assertEquals("UCM", msgType.getAbbreviation());

  }

  /**
   * Tests correctly making a Multicast Message from a JSON String Object.
   */
  @Test
  void makeMCMFromJSON() {
    String sampleJson = "{\"type\":\"MCM\",\"text\":\"This is a sample group message\",\"destination\":\"Group1\"}";
    Message makeMCM = JsonParserImpl.makeMessageFromJSON(sampleJson);
    assert makeMCM != null;
    assertEquals("This is a sample group message", makeMCM.getText());
    assertEquals("Group1", makeMCM.getMsgReceiver());
    IMessageType msgType = makeMCM.getMsgType();
    assertEquals("MCM", msgType.getAbbreviation());
  }

//  @Test
//  void makeAddMemberFromJSON() {
//    String sampleJson = "{\"type\":\"ADM\",\"destination\":\"Group1\",\"admin\":\"Thomas\",\"member\":\"Jerry\"}";
//    Message addMessage = JsonParserImpl.makeMessageFromJSON(sampleJson);
//    assert addMessage != null;
//    assertEquals("Thomas", addMessage.getName());
//    assertEquals("Jerry", addMessage.getUserName());
//    IMessageType typeOf = addMessage.getMsgType();
//    assertEquals("CMD", typeOf.getAbbreviation());
//    assertEquals("[ADM]", Arrays.toString(typeOf.extraParams()));
//    assertEquals("Group1", addMessage.getMsgReceiver());
//  }

  @Test
  void makeRemoveMemberFromJSON() {
    String sampleJson = "{\"type\":\"RMM\",\"destination\":\"Group2\",\"admin\":\"Donald\",\"member\":\"Jerry\"}";
    Message addMessage = JsonParserImpl.makeMessageFromJSON(sampleJson);
    assert addMessage != null;
    assertEquals("Donald", addMessage.getName());
    assertEquals("Jerry", addMessage.getUserName());
    IMessageType typeOf = addMessage.getMsgType();
    assertEquals("CMD", typeOf.getAbbreviation());
    assertEquals("[RMM]", Arrays.toString(typeOf.extraParams()));
    assertEquals("Group2", addMessage.getMsgReceiver());
  }

  @Test
  void createGroupFromJSON() {
    String sampleJson = "{\"type\":\"CRT\",\"destination\":\"group1\",\"admin\":\"tom\"}";
    Message crtMessage = JsonParserImpl.makeMessageFromJSON(sampleJson);
    assert crtMessage != null;
    assertEquals("tom", crtMessage.getName());
    IMessageType typeOf = crtMessage.getMsgType();
    assertEquals("CMD", typeOf.getAbbreviation());
    assertEquals("[CRT]", Arrays.toString(typeOf.extraParams()));
    assertEquals("group1", crtMessage.getMsgReceiver());
  }

  @Test
  void deleteGroupFromJson() {
    String sampleJson = "{\"type\":\"DLG\",\"destination\":\"group1\",\"admin\":\"tom\"}";
    Message crtMessage = JsonParserImpl.makeMessageFromJSON(sampleJson);
    assert crtMessage != null;
    assertEquals("tom", crtMessage.getName());
    IMessageType typeOf = crtMessage.getMsgType();
    assertEquals("CMD", typeOf.getAbbreviation());
    assertEquals("[DLG]", Arrays.toString(typeOf.extraParams()));
    assertEquals("group1", crtMessage.getMsgReceiver());
  }
}