package edu.northeastern.ccs.im.server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import edu.northeastern.ccs.im.Message;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.lang.reflect.Field;


/**
 * The type Unicast message handler test.
 */
public class UnicastMessageHandlerTest {

    private ClientRunnable clientRunnable;

    /**
     * Sets .
     */
    @BeforeEach
    public void setup() {
        clientRunnable = mock(ClientRunnable.class);
        when(clientRunnable.messageChecks(any(Message.class))).thenReturn(true);
    }

    /**
     * Test that running when ClientRunnable's messageChecks method would return false
     * never calls the handleMessage method and enqueues a new error message to the client.
     * <p>
     * This is because the message should not actually be sent if the messageChecks fail.
     */
    @Test
    public void testRunWithFailingMessageChecks() {
        Message msg = Message.makeUniCastMessage("hello","active");
        UnicastMessageHandler handler = spy(new UnicastMessageHandler(msg));

        when(clientRunnable.messageChecks(msg)).thenReturn(false);

        handler.run(clientRunnable);

        verify(clientRunnable, times(1)).enqueueMessage(any(Message.class));
        verify(handler, never()).handleMessage(clientRunnable);
    }

    /**
     * Test that running with ClientRunnable's messageChecks method would return true
     * calls the handleMessage method and properly queues the message for broadcast.
     *
     * @throws NoSuchFieldException   the no such field exception
     * @throws IllegalAccessException the illegal access exception
     */
    @Test
    public void testRunWithPassingMessageChecks() throws NoSuchFieldException, IllegalAccessException {
        Message msg = Message.makeUniCastMessage("user", "active");
        UnicastMessageHandler handler = spy(new UnicastMessageHandler(msg));

        Prattle prattleMock = mock(Prattle.class);

        // Change prattle instance to our mock to verify that broadcastMessage is called
        Field instanceField = Prattle.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(Prattle.class, prattleMock);

        handler.run(clientRunnable);

        verify(handler).handleMessage(clientRunnable);
        verify(prattleMock).unicastMessage(msg,clientRunnable);

        // Reset prattle instance to null so a proper one will be created for any future tests
        instanceField.set(Prattle.class, null);
    }




}
