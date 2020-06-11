package edu.northeastern.ccs.im.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import edu.northeastern.ccs.im.Message;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MulticastMessageHandlerTest {
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
    Message msg = Message.makeMultiCastMessage("hello","active1");
    MulticastMessageHandler handler = spy(new MulticastMessageHandler(msg));

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
    Message msg = Message.makeMultiCastMessage("user", "active1");
    MulticastMessageHandler handler = spy(new MulticastMessageHandler(msg));

    Prattle prattleMock = mock(Prattle.class);

    // Change prattle instance to our mock to verify that broadcastMessage is called
    Field instanceField = Prattle.class.getDeclaredField("instance");
    instanceField.setAccessible(true);
    instanceField.set(Prattle.class, prattleMock);

    handler.run(clientRunnable);

    verify(handler).handleMessage(clientRunnable);
    verify(prattleMock).multicastMessage(msg,clientRunnable);

    // Reset prattle instance to null so a proper one will be created for any future tests
    instanceField.set(Prattle.class, null);
  }




}
