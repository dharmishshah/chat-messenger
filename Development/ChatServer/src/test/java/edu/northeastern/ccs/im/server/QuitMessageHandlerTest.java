package edu.northeastern.ccs.im.server;

import static org.mockito.Mockito.*;

import edu.northeastern.ccs.im.Message;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

public class QuitMessageHandlerTest {
    private ClientRunnable clientRunnable;

    @BeforeEach
    public void setup() {
        clientRunnable = mock(ClientRunnable.class);
    }

    /**
     * Verify that quit handler tells client runnable to terminate and responds with
     * a quit message.
     */
    @Test
    public void testQuitMessageHandler() {
        when(clientRunnable.getName()).thenReturn("user");
        Message quitMsg = Message.makeQuitMessage("user");
        QuitMessageHandler handler = new QuitMessageHandler(quitMsg);

        handler.run(clientRunnable);

        verify(clientRunnable).terminate();
        verify(clientRunnable).enqueueMessage(any(Message.class));
    }
}
