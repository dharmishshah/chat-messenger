package edu.northeastern.ccs.im.server.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.northeastern.ccs.im.Message;
import edu.northeastern.ccs.im.NetworkConnection;
import edu.northeastern.ccs.im.server.ClientRunnable;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

/**
 * The type Json maker imp test.
 */
class JsonMakerImpTest {

  /**
   * The Cr.
   */
  ClientRunnable cr;
  /**
   * The Mock connection.
   */
  NetworkConnection mockConnection;

  /**
   * Sets up.
   */
  @BeforeEach
  void setUp() {
    mockConnection = mock(NetworkConnection.class);
    cr = new ClientRunnable(mockConnection);
    cr.setName("Tom");

  }

  /**
   * Test uni json.
   */
  @Test
  void testUniJson(){
    Message uni = Message.makeUniCastMessage("Hello this is unicast","Pete");
    uni.setMsgSender("Tom");
    assertEquals("{\"type\":\"UCM\",\"text\":\"Hello this is unicast\",\"sender\":\"Tom\",\"destination\":\"Pete\"}",JsonMakerImp.makeJson(uni));
  }

  /**
   * Test mcm json.
   */
  @Test
  void testMcmJson(){
    Message mcm = Message.makeMultiCastMessage("Hello this is multicast","Group1");

    mcm.setMsgSender("George");
    assertEquals("{\"type\":\"MCM\",\"text\":\"Hello this is multicast\",\"sender\":\"George\",\"destination\":\"Group1\"}",JsonMakerImp.makeJson(mcm));
  }
}