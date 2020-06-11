package edu.northeastern.ccs.im.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentLinkedQueue;

import edu.northeastern.ccs.im.Message;
import edu.northeastern.ccs.im.NetworkConnection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LoginMessageHandlerTest {

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
    Message msg = Message.makeLoginMessage("user", "message");
    LoginMessageHandler handler = spy(new LoginMessageHandler(msg));

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

    // Change prattle instance to our mock to verify that broadcastMessage is called
    Field instanceField = Prattle.class.getDeclaredField("instance");
    instanceField.setAccessible(true);
    instanceField.set(Prattle.class, prattleMock);

    handler.run(clientRunnable);

    verify(handler).handleMessage(clientRunnable);
    verify(prattleMock).registerClient(msg);

    LoginMessageHandler handlerLogin = spy(new LoginMessageHandler(msg));

    handlerLogin.run(clientRunnable);
    verify(handlerLogin).handleMessage(clientRunnable);
    verify(prattleMock).loginClient(msg,clientRunnable);

    // Reset prattle instance to null so a proper one will be created for any future tests
    instanceField.set(Prattle.class, null);


  }


}
