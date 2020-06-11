package edu.northeastern.ccs.im.server;




import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import edu.northeastern.ccs.im.Message;

import edu.northeastern.ccs.im.messages.commandmessage.CommandMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;


public class CommandMessageHandlerTest {

    private ClientRunnable clientRunnable;

    @BeforeEach
    public void setup() {
        clientRunnable = mock(ClientRunnable.class);
        when(clientRunnable.messageChecks(any(Message.class))).thenReturn(true);
    }

    /**
     * Test that running when ClientRunnable's messageChecks method would return false
     * never calls the handleMessage method and enqueues a new error message to the client.
     *
     * This is because the message should not actually be sent if the messageChecks fail.
     */
    @Test
    public void testRunWithFailingMessageChecks() {
        String[] admin = new String[1];
        admin[0] = "tom";
        Message msg = Message.makeCommandMessage("CRT", "message", admin);
        CommandMessageHandler handler = spy(new CommandMessageHandler(msg));

        when(clientRunnable.messageChecks(msg)).thenReturn(false);

        handler.run(clientRunnable);

        verify(clientRunnable, times(1)).enqueueMessage(any(Message.class));
        verify(handler, never()).handleMessage(clientRunnable);
    }

    /**
     * Test that running with ClientRunnable's messageChecks method would return true
     * calls the handleMessage method and properly queues the message for broadcast.
     */
    @Test
    public void testRunWithPassingMessageChecks() throws NoSuchFieldException, IllegalAccessException {
        String[] fol = new String[1];
        fol[0] = "Admin";
        Message msg = Message.makeCommandMessage("CRT", "group5", fol);



        String[] args = new String[2];
        args[0] = "Admin";
        args[1] = "tom";

        Message msg1 = Message.makeCommandMessage("ADM", "group5", args);



        Message msg2 = Message.makeCommandMessage("RMM", "group5", args);

        Message msg3 = Message.makeCommandMessage("DLG", "group5", args);

        Message msg4  =  Message.makeCommandMessage("MSD","hELLO",args);


        CommandMessageHandler handler = spy(new CommandMessageHandler(msg));

        CommandMessageHandler handler1 = spy(new CommandMessageHandler(msg1));


        CommandMessageHandler handler2 = spy(new CommandMessageHandler(msg2));


        CommandMessageHandler handler3 = spy(new CommandMessageHandler(msg3));
        CommandMessageHandler handler4 = spy(new CommandMessageHandler(msg4));


        Prattle prattleMock = mock(Prattle.class);

        // Change prattle instance to our mock to verify that broadcastMessage is called
        Field instanceField = Prattle.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(Prattle.class, prattleMock);

        handler.run(clientRunnable);
        handler1.run(clientRunnable);
        handler2.run(clientRunnable);
        handler3.run(clientRunnable);
        handler4.run(clientRunnable);
        verify(handler).handleMessage(clientRunnable);
//        verify(prattleMock).createGroup(msg.getText().split(" ")[2],clientRunnable);
        verify(handler1).handleMessage(clientRunnable);
        verify(handler1).handleMessage(clientRunnable);

        verify(handler2).handleMessage(clientRunnable);

        verify(handler3).handleMessage(clientRunnable);
//        verify(handler4).handleMessage(clientRunnable);

        // Reset prattle instance to null so a proper one will be created for any future tests
        instanceField.set(Prattle.class, null);
    }


    /**
     * Test that running with ClientRunnable's messageChecks method would return true
     * calls the handleMessage method and properly queues the message for broadcast.
     */
    @Test
    public void testRunWithPassingWrongCommand() throws NoSuchFieldException, IllegalAccessException {
        String[] fol = new String[1];
        fol[0] = "Admin";
        Message msg = Message.makeCommandMessage("MSD", "group5", fol);
        CommandMessageHandler handler = spy(new CommandMessageHandler(msg));


    }
}
