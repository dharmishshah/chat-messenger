package edu.northeastern.ccs.im.persistancetest;

import edu.northeastern.ccs.im.dataconnections.models.ChatterMessage;
import edu.northeastern.ccs.im.dataconnections.models.Status;
import edu.northeastern.ccs.im.dataconnections.operations.MessageOperations;

import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class ChatterMessageTest {

    @Test
    public void createChatterMessageTest()
    {
        ChatterMessage cm = new ChatterMessage();
        cm.setMessageId(1);
        assertEquals(java.util.Optional.ofNullable(1), java.util.Optional.ofNullable(cm.getMessageId()));
        cm.setMessageStatus(Status.UNREAD);
        assertEquals(Status.UNREAD,cm.getMessageStatus());
        cm.setGroupMessage(false);
        cm.setMessageReceiver(1);
        assertEquals(java.util.Optional.ofNullable(1), java.util.Optional.ofNullable(cm.getMessageReceiver()));
        cm.setMessageSender(2);
        assertEquals(java.util.Optional.ofNullable(2), java.util.Optional.ofNullable(cm.getMessageSender()));
        cm.setMessageContent("hello world");
        assertEquals("hello world",cm.getMessageContent());

        cm.setMessageType("Command Message");
        assertEquals("Command Message",cm.getMessageType());

        cm.setTimeStamp(new Date());
        assertEquals(new Date(),cm.getTimeStamp());




        assertFalse(cm.isGroupMessage());
    }

    @Test
    public void receiveUnreadMessageOfUser() {
        ChatterMessage cm = new ChatterMessage();
        cm.setMessageReceiver(2);
        cm.setMessageSender(1);
        cm.setMessageId(1);
        cm.setMessageStatus(Status.UNREAD);
        cm.setGroupMessage(false);
        cm.setMessageContent("hello world");
        cm.setMessageType("Command Message");
        cm.setTimeStamp(new Date());
        MessageOperations mo = new MessageOperations();
        List unreadMeassages = mo.getAllUnreadMessages("manush");
        System.out.println(unreadMeassages);
    }
}
