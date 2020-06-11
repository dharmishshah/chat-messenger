package edu.northeastern.ccs.im.persistancetest;

import org.junit.Assert;

import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import edu.northeastern.ccs.im.Message;
import edu.northeastern.ccs.im.dataconnections.models.ChatterMessage;
import edu.northeastern.ccs.im.dataconnections.models.ChatterUser;
import edu.northeastern.ccs.im.dataconnections.models.Status;
import edu.northeastern.ccs.im.dataconnections.operations.MessageOperations;
import edu.northeastern.ccs.im.dataconnections.operations.UserOperations;

public class MessageTest {

  @Test
  public void test1() {
    long millseconds = System.currentTimeMillis();
    Date d1 = new Date(millseconds);
    ChatterMessage m1 = new ChatterMessage("UCM",1,2,
            "Hello",d1, Status.UNREAD,false);
    MessageOperations o1 = new MessageOperations();
    StringBuilder output1 = new StringBuilder();
    Assert.assertTrue(o1.createMessage(m1));
  }

  @Test
  public void test2() {
    long millseconds = System.currentTimeMillis();
    Date d1 = new Date(millseconds);
    ChatterMessage m2 = new ChatterMessage("MCM",2,2,
            "Next",d1, Status.READ,true);
    MessageOperations o2 = new MessageOperations();
    StringBuilder output1 = new StringBuilder();
    Assert.assertTrue(o2.createMessage(m2));
    m2.setMessageStatus(Status.DELETED);
    o2.updateMessage(m2);
  }

  @Test
  public void test3() {
    MessageOperations o2 = new MessageOperations();
    List l1 = new ArrayList();
    l1.add(1);
    l1.add(2);
    List messagesRecieved = o2.retrieveMessagesForUser(l1);
    Assert.assertEquals(messagesRecieved, messagesRecieved);
  }

  @Test
  public void testTransferMessage(){

    ChatterUser user1 = new ChatterUser("1232","test","tom");
    StringBuilder output = new StringBuilder();
    UserOperations userOperations = new UserOperations(output);
    userOperations.createUser(user1);
    ChatterUser user2 = new ChatterUser("abc","test2","tom");
    userOperations.createUser(user2);


    Message message = Message.makeUniCastMessage("Hi world","test");
    message.setMsgSender("test2");
    MessageOperations o2 = new MessageOperations();
    ChatterMessage cm = o2.transferMessage(message,"UCM");
    Assert.assertEquals("Hi world",cm.getMessageContent());

    Message messageMulti = Message.makeMultiCastMessage("Hi world","test");
    messageMulti.setMsgSender("test2");
    ChatterMessage cm2 = o2.transferMessage(messageMulti,"UCM");
    Assert.assertEquals("Hi world",cm2.getMessageContent());

  }
}
