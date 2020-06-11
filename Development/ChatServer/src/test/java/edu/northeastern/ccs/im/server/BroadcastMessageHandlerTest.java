package edu.northeastern.ccs.im.server;

import static org.mockito.Mockito.*;

import edu.northeastern.ccs.im.Message;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.lang.reflect.Field;

public class BroadcastMessageHandlerTest {
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
        Message msg = Message.makeBroadcastMessage("user", "message");
        BroadcastMessageHandler handler = spy(new BroadcastMessageHandler(msg));

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
        Message msg = Message.makeBroadcastMessage("user", "message");
        BroadcastMessageHandler handler = spy(new BroadcastMessageHandler(msg));

        Prattle prattleMock = mock(Prattle.class);

        // Change prattle instance to our mock to verify that broadcastMessage is called
        Field instanceField = Prattle.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(Prattle.class, prattleMock);

        handler.run(clientRunnable);

        verify(handler).handleMessage(clientRunnable);
        verify(prattleMock).broadcastMessage(msg);

        // Reset prattle instance to null so a proper one will be created for any future tests
        instanceField.set(Prattle.class, null);
    }
}
