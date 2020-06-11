package edu.northeastern.ccs.im.server;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import edu.northeastern.ccs.im.Message;
import edu.northeastern.ccs.im.server.models.Client;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.lang.reflect.Field;
import java.util.Map;

public class RegistrationMessageHandlerTest {

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
        Message msg = Message.makeRegisterMessage("user", "message");
        RegistrationMessageHandler handler = spy(new RegistrationMessageHandler(msg));

        when(clientRunnable.messageChecks(msg)).thenReturn(false);

        handler.run(clientRunnable);

        verify(clientRunnable, times(1)).enqueueMessage(any(Message.class));
        verify(handler, never()).handleMessage(clientRunnable);
    }


    @Test
    public void testRunWithPassingMessageChecks() throws NoSuchFieldException, IllegalAccessException {
        Message msg = Message.makeRegisterMessage("user", "message");
        RegistrationMessageHandler handler = spy(new RegistrationMessageHandler(msg));

        Prattle prattleMock = mock(Prattle.class);

        // Change prattle instance to our mock to verify that register is called
        Field instanceField = Prattle.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(Prattle.class, prattleMock);

        handler.run(clientRunnable);

        verify(handler).handleMessage(clientRunnable);
        verify(prattleMock).registerClient(msg);

        // Reset prattle instance to null so a proper one will be created for any future tests
        instanceField.set(Prattle.class, null);
    }

    @Test
    public void activeUsersTest() throws NoSuchFieldException, IllegalAccessException {
        Message msg = Message.makeRegisterMessage("user", "message");

        Prattle p = Prattle.getInstance();
        p.registerClient(msg);
        Field active = Prattle.class.getDeclaredField("activeUsers");
        active.setAccessible(true);
        Map<String, Client> activeUsers = (Map<String, Client>)active.get(p);
        for(Map.Entry<String,Client> entry:activeUsers.entrySet())
        {
            if(entry.getValue().getName().equals("user") && entry.getValue().getPassword().equals("message"))
            {
                assertEquals("user",entry.getValue().getName());
            }
        }
        p.stopServer();
    }



}
