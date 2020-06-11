package edu.northeastern.ccs.im.messages;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the MessageTypes constants class
 */
public class MessageTypesTest {

  /**
   * Tests that the Broadcast message type abbreviation is correct.
   */
  @Test
   void testBroadcastType() {
    assertEquals("BCT", MessageTypes.BROADCAST);
  }

  /**
   * Tests that the Login message type abbreviation is correct.
   */
  @Test
   void testLoginType() {
    assertEquals("LOG", MessageTypes.LOGIN);
  }

  /**
   * Tests that the Multicast message type abbreviation is correct.
   */
  @Test
   void testMulticastType() {
    assertEquals("MCM", MessageTypes.MULTICAST);
  }

  /**
   * Tests that the Quit message type abbreviation is correct.
   */
  @Test
   void testQuitType() {
    assertEquals("BYE", MessageTypes.QUIT);
  }

  /**
   * Tests that the Register message type abbreviation is correct.
   */
  @Test
   void testRegisterType() {
    assertEquals("RGT", MessageTypes.REGISTER);
  }

  /**
   * Tests that the Simple Login message type abbreviation is correct.
   */
  @Test
   void testSimpleLoginType() {
    assertEquals("HLO", MessageTypes.SIMPLE_LOGIN);
  }

  /**
   * Tests that the Unicast message type abbreviation is correct.
   */
  @Test
   void testUnicastType() {
    assertEquals("UCM", MessageTypes.UNICAST);
  }

  /**
   * Tests that the Command message type abbreviation is correct.
   */
  @Test
   void testCommandType() {
    assertEquals("CMD", MessageTypes.COMMAND);
  }

  /**
   * Tests that the Failure message type abbreviation is correct.
   */
  @Test
   void testFailureType() {
    assertEquals("FAI", MessageTypes.FAILURE);
  }

  /**
   * Tests that the Success message type abbreviation is correct.
   */
  @Test
   void testSuccessType() {
    assertEquals("SUC", MessageTypes.SUCCESS);
  }
}
