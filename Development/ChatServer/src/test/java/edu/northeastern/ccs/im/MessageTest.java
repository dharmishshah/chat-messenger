package edu.northeastern.ccs.im;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import edu.northeastern.ccs.im.messages.AbstractMessageType;
import edu.northeastern.ccs.im.messages.clientsidemessages.MultiCastMessage;
import edu.northeastern.ccs.im.messages.clientsidemessages.UniCastMessage;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import edu.northeastern.ccs.im.messages.IMessageType;

/**
 * Tests that verify that the messages class works as expected.
 *
 * @author Riya Nadkarni
 * @version 12 -31-2018
 */
class MessageTest {

  /** Error message if the sender name is incorrect. */
  private static final String SENDER_ERROR = "Incorrect sender name.";
  /** Error message if the message text is incorrect. */
  private static final String TEXT_ERROR = "Incorrect message text.";
  /** Error message if the message is not of type QUIT. */
  private static final String TERMINATE_ERROR = "Not a terminate handle.";
  /** Error message if the message is not of type BROADCAST. */
  private static final String BCT_ERROR = "Not a broadcast handle.";
  /** Error message if the message is not of type HELLO. */
  private static final String INIT_ERROR = "Not an initialization handle.";
  /** Error message if the toString method does not give the right output. */
  private static final String TO_STRING_ERROR = "toString method not working as expected.";

  /**
   * Test the makeQuitMessageMethod method.
   */
  @Test
  void testMakeQuitMessage() {
    Message testMessage = Message.makeQuitMessage("Riya");
    assertEquals("Riya", testMessage.getName(), SENDER_ERROR);
    assertEquals(null, testMessage.getText(), TEXT_ERROR);
    assertEquals(true, testMessage.terminate(), TERMINATE_ERROR);
    assertEquals(false, testMessage.isBroadcastMessage(), BCT_ERROR);
    assertEquals(false, testMessage.isInitialization(), INIT_ERROR);
  }

  /**
   * Test the makeBroadcastMessageMethod method.
   */
  @Test
  void testMakeBroadcastMessage() {
    Message testMessage = Message.makeBroadcastMessage("Peter", "How's it going?");
    assertEquals("Peter", testMessage.getName(), SENDER_ERROR);
    assertEquals("How's it going?", testMessage.getText(), TEXT_ERROR);
    assertEquals(false, testMessage.terminate(), TERMINATE_ERROR);
    assertEquals(true, testMessage.isBroadcastMessage(), BCT_ERROR);
    assertEquals(false, testMessage.isInitialization(), INIT_ERROR);
  }

  /**
   * Test the makeHelloMessageMethod method.
   */
  @Test
  void testMakeHelloMessage() {
    Message testMessage = Message.makeHelloMessage("John","qwerty");
    assertEquals(null, testMessage.getName(), SENDER_ERROR);
    assertEquals("John", testMessage.getUserName(), TEXT_ERROR);
    assertEquals(false, testMessage.terminate(), TERMINATE_ERROR);
    assertEquals(false, testMessage.isBroadcastMessage(), BCT_ERROR);
    assertEquals(false, testMessage.isInitialization(), INIT_ERROR);
  }

  /**
   * Test the makeMessageMethod method with Quit handle.
   */
  @Test
  void testMakeMessageWithQuit() {
    Message testMessage = Message.makeMessage("BYE", "Josh",null, "Logging out.",null,null);
    assertEquals("Josh", testMessage.getName(), SENDER_ERROR);
    assertEquals(null, testMessage.getText(), TEXT_ERROR);
    assertEquals(true, testMessage.terminate(), TERMINATE_ERROR);
    assertEquals(false, testMessage.isBroadcastMessage(), BCT_ERROR);
    assertEquals(false, testMessage.isInitialization(), INIT_ERROR);
  }

  /**
   * Test the makeMessageMethod method with Broadcast handle.
   */
  @Test
  void testMakeMessageWithBroadcast() {
    Message testMessage = Message.makeMessage("BCT", "Peter",null, "Happy Birthday.",null,null);
    assertEquals("Peter", testMessage.getName(), SENDER_ERROR);
    assertEquals("Happy Birthday.", testMessage.getText(), TEXT_ERROR);
    assertEquals(false, testMessage.terminate(), TERMINATE_ERROR);
    assertEquals(true, testMessage.isBroadcastMessage(), BCT_ERROR);
    assertEquals(false, testMessage.isInitialization(), INIT_ERROR);
  }

  /**
   * Test the makeMessageMethod method with Hello handle.
   */
  @Test
  void testMakeMessageWithHello() {
    Message testMessage = Message.makeMessage("LOG", "David","Peter", null,"David",null);
    assertEquals("David", testMessage.getUserName(), SENDER_ERROR);
    assertEquals(null, testMessage.getText(), TEXT_ERROR);
    assertEquals(false, testMessage.terminate(), TERMINATE_ERROR);
    assertEquals(false, testMessage.isBroadcastMessage(), BCT_ERROR);
    assertEquals(false, testMessage.isInitialization(), INIT_ERROR);
  }

  /**
   * Test the makeBroadcastMessageMethod method with No handle.
   */
  @Test
  void testMakeMessageWithNoHandle() {
    Message testMessage = Message.makeMessage("DUMMY", "Peter",null, "Just checking.",null,null);
    assertEquals(null, testMessage, "Incorrect message created.");
  }

  /**
   * Test the toString method.
   */
  @Test
  void testToString() {
    Message testMessage = Message.makeMessage("BCT", "Josh",null, "This is a good way to communicate.",null,null);
    String result = "BCT 4 Josh 34 This is a good way to communicate.";
    assertEquals(result, testMessage.toString(), TO_STRING_ERROR);
  }

  /**
   * Test the toString method with null value for sender for Broadcast.
   */
  @Test
  void testToStringWithNullSenderforBroadcast() {
    Message testMessage = Message.makeMessage("BCT", null,null, "How are you doing?",null,null);
    String result = "BCT 2 -- 18 How are you doing?";
    assertEquals(result, testMessage.toString(), TO_STRING_ERROR);
  }

  /**
   * Test the toString method with null value for text for Broadcast.
   */
  @Test
  void testToStringWithNullTextforBroadcast() {
    Message testMessage = Message.makeMessage("BCT", "Riya",null, null,null,null);
    String result = "BCT 4 Riya 2 --";
    assertEquals(result, testMessage.toString(), TO_STRING_ERROR);
  }


  /**
   * Make quit message.
   */
  @Test
  void makeQuitMessage(){
    Message quitMessage = Message.makeMessage("BYE","John",null,null,null,null);
    assertEquals("John",quitMessage.getName());
  }

  /**
   * Make multi cast message.
   */
  @Test
  void makeMultiCastMessage(){
    MultiCastMessage mcm = new MultiCastMessage(new String[2]);
    Message mcmMessage = Message.makeMessage("MCM",null,"Group 1","Hi this is a Multicast Message",null,null);
    assertEquals("Hi this is a Multicast Message",mcmMessage.getText());
    assertEquals("Group 1",mcmMessage.getMsgReceiver());
  }

  /**
   * Make uni cast message.
   */
  @Test
  void makeUniCastMessage(){
    UniCastMessage ucm = new UniCastMessage(new String[2]);
    Message uniMessage = Message.makeMessage("UCM",null,"Peter","Hello",null,null);
    assertEquals("Hello",uniMessage.getText());
    assertEquals("Peter",uniMessage.getMsgReceiver());
  }

  /**
   * Make register message.
   */
  @Test
  void makeRegisterMessage(){
    Message registerMessage = Message.makeMessage("RGT",null,null,null,"Paul","qwerty");
    assertEquals("Paul",registerMessage.getUserName());
    assertEquals("qwerty",registerMessage.getPassword());
  }


//  /**
//   * Make command message.
//   */
//  @Test
//  void makeCommandMessage(){
//    Message commandMessage = Message.makeMessage("CMD","SERVER",null,"Command",null,null);
//    assertEquals("SERVER",commandMessage.getName());
//    assertEquals("Command",commandMessage.getText());
//  }

  /**
   * Testing making a Failure Message
   */
  @Test
  void makeFailureMessage(){
    Message failure = Message.makeMessage("FAI",null,null,null,null,null);
    IMessageType type = failure.getMsgType();
    assertEquals("FAI",type.getAbbreviation());
    assertEquals("[200]",Arrays.toString(type.extraParams()));
  }

  /**
   * Testing making a Success Message
   */
  @Test
  void makeSuccessMessage(){
    Message success = Message.makeMessage("SUC",null,null,null,null,null);
    IMessageType type = success.getMsgType();
    assertEquals("SUC",type.getAbbreviation());
    assertEquals("[100]",Arrays.toString(type.extraParams()));
  }




}
