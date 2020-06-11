package edu.northeastern.ccs.im.messages;

import edu.northeastern.ccs.im.messages.clientsidemessages.SimpleLoginMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import edu.northeastern.ccs.im.messages.clientsidemessages.MultiCastMessage;
import edu.northeastern.ccs.im.messages.clientsidemessages.RegisterMessage;
import edu.northeastern.ccs.im.messages.clientsidemessages.UniCastMessage;
import edu.northeastern.ccs.im.messages.commandmessage.CommandMessage;
import edu.northeastern.ccs.im.messages.responsemessages.FailureMessage;
import edu.northeastern.ccs.im.messages.responsemessages.SuccessMessage;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * The type Message type test.
 */
class IMessageTypeTest {

  private IMessageType mcm;
  private IMessageType rgm;
  private IMessageType uni;
  private IMessageType cmd;
  private IMessageType hlo;
  private IMessageType suc;
  private IMessageType fai;

  private String[] paramsSuc;
  private String[] paramsFail;


  /**
   * Sets up.
   */
  @BeforeEach
  void setUp() {
    paramsSuc = new String[1];
    paramsSuc[0] = "100";
    paramsFail = new String[1];
    paramsFail[0] = "200";
    mcm = new MultiCastMessage();
    rgm = new RegisterMessage();
    uni = new UniCastMessage();
    cmd = new CommandMessage(new String[0]);
    suc = new SuccessMessage(paramsSuc);
    fai = new FailureMessage(paramsFail);
    hlo = new SimpleLoginMessage();
  }

  /**
   * Test mcm abbreviation.
   */
  @Test
  public void testMCMAbbreviation() {
    assertEquals("MCM", mcm.getAbbreviation());
  }


  @Test
  public void testHLOAbbreviation()
  {
    assertEquals("HLO",hlo.getAbbreviation());
  }

  /**
   * Test rgm abbreviation.
   */
  @Test
  public void testRGMAbbreviation() {
    assertEquals("RGT", rgm.getAbbreviation());
  }


  /**
   * Test uni abbreviation.
   */
  @Test
  public void testUNIAbbreviation() {
    assertEquals("UCM", uni.getAbbreviation());
  }


  /**
   * Test cmd abbreviation.
   */
  @Test
  public void testCMDAbbreviation() {
    assertEquals("CMD", cmd.getAbbreviation());
  }


  /**
   * Tests grabbing the extra params of Success message
   */
  @Test
  void testSucParams() {
    assertEquals("[100]", Arrays.toString(suc.extraParams()));
  }

  /**
   * Test suc abbreviation.
   */
  @Test
  void testSUCAbbreviation() {
    assertEquals("SUC", suc.getAbbreviation());
  }



  /**
   * Tests grabbing the extra params of Failure message
   */
  @Test
  void testFaiParams() {
    assertEquals("[200]", Arrays.toString(fai.extraParams()));
  }

  /**
   * Test Failure message abbreviation.
   */
  @Test
  void testFaiAbbreviation() {
    assertEquals("FAI", fai.getAbbreviation());
  }


  /**
   * Test Failure abbreviation.
   */
  @Test
  public void testFAIAbbreviation() {
    assertEquals("FAI", fai.getAbbreviation());
  }



}