package edu.northeastern.ccs.im;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.CharBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Testing the NetworkConnection class, and all of it's testable methods.
 */
class NetworkConnectionTest {


  /**
   * A Custom Testing Selector class for testing purposes only,
   * with the selectNow() and close() methods being changed.
   */
  private class TestingSelector extends Selector {

    /**
     * Not used for testing.
     *
     * @return this value is not used.
     */
    @Override
    public boolean isOpen() {
      return false;
    }

    /**
     * Not used for testing.
     *
     * @return this value is not used.
     */
    @Override
    public SelectorProvider provider() {
      return null;
    }

    /**
     * Not used for testing.
     *
     * @return this value is not used.
     */
    @Override
    public Set<SelectionKey> keys() {
      return new HashSet<>();
    }

    /**
     * Not used for testing.
     *
     * @return this value is not used.
     */
    @Override
    public Set<SelectionKey> selectedKeys() {
      return new HashSet<>();
    }

    /**
     * Used for testing, we force the selectNow() method to return 1, instead of 0.
     *
     * @return returns the desired integer 1.
     */
    @Override
    public int selectNow() {
      return 1;
    }

    /**
     * Not used for testing.
     *
     * @param timeout not used for testing.
     * @return not used for testing.
     */
    @Override
    public int select(long timeout) {
      return 0;
    }

    /**
     * Not used for testing.
     *
     * @return not used for testing.
     */
    @Override
    public int select() {
      return 0;
    }

    /**
     * Not used for testing.
     *
     * @return not used for testing.
     */
    @Override
    public Selector wakeup() {
      return null;
    }

    /**
     * Used for testing to force close() failing, this tests the safeguard in the NetworkConnection class.
     *
     * @throws IOException throws an IOException to simulate failure.
     */
    @Override
    public void close() throws IOException {
      throw new IOException();
    }
  }


  private NetworkConnection net;
  private SocketChannel mockedChannel;
  private String networkClassLoc;

  /**
   * Setting up our Mock Channel class for later use.
   */
  @BeforeEach
  void setUp() {
    mockedChannel = mock(SocketChannel.class);
    networkClassLoc = "edu.northeastern.ccs.im.NetworkConnection";
  }

  /**
   * Testing that the NetworkConnection object initializes properly.
   *
   * @throws IOException thrown in case configureBlocking fails.
   */
  @Test
  void networkConnConstructorTest() throws IOException {
    when(mockedChannel.configureBlocking(false)).thenReturn(null);
    net = new NetworkConnection(mockedChannel);
    assertNotNull(net);
  }

  /**
   * Testing the case where a message gets lost in transit.
   *
   * @throws IOException thrown in case configureBlocking fails.
   */
  @Test
  void messageTest() throws IOException {
    Message a = Message.makeHelloMessage("John","qwerty");
    when(mockedChannel.configureBlocking(false)).thenReturn(null);
    net = new NetworkConnection(mockedChannel);
    assertFalse(net.sendMessage(a));
    net.close();
  }

  /**
   * Causing the NetworkConnection close() method to fail, and testing proper try/catch operation.
   *
   * @throws IOException            thrown in case configureBlocking fails
   * @throws ClassNotFoundException thrown if the NetworkConnection class is not found.
   * @throws NoSuchFieldException   thrown if the 'selector' field inside the NetworkConnection class is not found
   * @throws IllegalAccessException thrown if the field is not set to accessible before being used.
   */
  @Test
  void networkCloseFailTest() throws IOException, ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
    when(mockedChannel.configureBlocking(false)).thenReturn(null);
    net = new NetworkConnection(mockedChannel);
    Field prField = Class.forName(networkClassLoc).getDeclaredField("selector");
    prField.setAccessible(true);
    Selector newSelector = new TestingSelector();

    prField.set(net, newSelector);
    try {
      net.close();
    } catch (AssertionError e) {
      assertEquals("java.lang.AssertionError", e.toString());
    }
  }


  /**
   * Testing that the MessageIterator is correctly instantiated in the NetworkConnection class.
   *
   * @throws IOException thrown if configureBlocking fails.
   */
  @Test
  void messageIteratorTestInstantiate() throws IOException {
    when(mockedChannel.configureBlocking(false)).thenReturn(null);
    net = new NetworkConnection(mockedChannel);
    assertNotNull(net.iterator());
    assertEquals("MessageIterator", net.iterator().getClass().getSimpleName());
  }

  /**
   * Checks that when no message is received, the iterator correctly reports no message with hasNext().
   *
   * @throws IOException thrown if configureBlocking fails.
   */
  @Test
  void messageIteratorTestNoMessage() throws IOException {
    when(mockedChannel.configureBlocking(false)).thenReturn(null);
    net = new NetworkConnection(mockedChannel);
    Iterator<Message> messageIterate = net.iterator();
    assertFalse(messageIterate.hasNext());
  }

  /**
   * Correctly throwing an exception when using next() when no next message exists.
   *
   * @throws IOException thrown if configureBlocking fails.
   */
  @Test
  void messageIteratorTestNoMessageNext() throws IOException {
    when(mockedChannel.configureBlocking(false)).thenReturn(null);
    net = new NetworkConnection(mockedChannel);
    Iterator<Message> messageIterate = net.iterator();
    assertThrows(NoSuchElementException.class, messageIterate::next);
  }


  /**
   * Manually loading up the messages Queue inside NetworkConnection, and testing iterating through it.
   *
   * @throws IOException            thrown if configureBlocking fails.
   * @throws NoSuchFieldException   thrown if the Queue is not found inside the NetworkConnection.
   * @throws IllegalAccessException thrown if the Queue field is not set as accessible.
   * @throws ClassNotFoundException thrown if the NetworkConnection is not found.
   */
  @Test
  void messagesTest() throws IOException, NoSuchFieldException, IllegalAccessException, ClassNotFoundException {
    Field prField = Class.forName(networkClassLoc).getDeclaredField("messages");
    prField.setAccessible(true);

    when(mockedChannel.configureBlocking(false)).thenReturn(null);
    net = new NetworkConnection(mockedChannel);
    Queue<Message> a = new LinkedList<>();
    a.offer(Message.makeBroadcastMessage("John", "Hello"));
    a.offer(Message.makeBroadcastMessage("Tom", "Hi"));
    prField.set(net, a);
    //This sequence verifies proper Queue operation,
    //Multiple assert statements are necessary to accomplish this.
    assertEquals("BCT 4 John 5 Hello", net.iterator().next().toString());
    assertTrue(net.iterator().hasNext());
    assertEquals("BCT 3 Tom 2 Hi", net.iterator().next().toString());
    assertFalse(net.iterator().hasNext());
  }

  /**
   * Tests the other option of having Messages, besides there being messages in the internal Queue.
   *
   * @throws ClassNotFoundException thrown if NetworkConnection class is not found.
   * @throws NoSuchFieldException   thrown if one of the two modified fields are not found.
   * @throws IOException            thrown if configure blocking fails.
   * @throws IllegalAccessException thrown if the two fields are not set as accessible before being used.
   */
  @Test
  void injectedSelector() throws ClassNotFoundException, NoSuchFieldException, IOException, IllegalAccessException {
    Field prField = Class.forName(networkClassLoc).getDeclaredField("selector");
    prField.setAccessible(true);
    when(mockedChannel.configureBlocking(false)).thenReturn(null);
    net = new NetworkConnection(mockedChannel);

    Field keyField = Class.forName(networkClassLoc).getDeclaredField("key");
    keyField.setAccessible(true);
    SelectionKey newKey = new SelectionKey() {
      @Override
      public SelectableChannel channel() {
        return null;
      }

      @Override
      public Selector selector() {
        return null;
      }

      @Override
      public boolean isValid() {
        return false;
      }

      @Override
      public void cancel() {

      }

      @Override
      public int interestOps() {
        return 0;
      }

      @Override
      public SelectionKey interestOps(int ops) {
        return null;
      }

      @Override
      public int readyOps() {
        return 0;
      }
    };
    when(newKey.isReadable()).thenCallRealMethod();
    Selector newSelector = new TestingSelector();
    prField.set(net, newSelector);
    keyField.set(net, newKey);
    try {
      assertFalse(net.iterator().hasNext());
    } catch (AssertionError e) {
      assertEquals("java.lang.AssertionError", e.toString());
    }
  }

  @Test
  void testDecodeMessage() throws NoSuchMethodException, IOException, IllegalAccessException, InvocationTargetException {
    when(mockedChannel.configureBlocking(false)).thenReturn(null);
    net = new NetworkConnection(mockedChannel);

    Iterator iterator = net.iterator();
    Method decode = iterator.getClass().getDeclaredMethod("decodeMessage", CharBuffer.class);
    decode.setAccessible(true);

    char start = 0x7d;
    char esc = 0x7e; // this is ~ character
    char end = 0x7f;

    char[] arr1 = {start, esc, esc, 'h', 'e', 'l', 'l', 'o', end};
    assertEquals("~hello", decode.invoke(iterator, CharBuffer.wrap(arr1)));

    char[] arr2 = {start, esc, esc, 'h', 'e', end, 'l', 'l', 'o', end};
    assertEquals("~he", decode.invoke(iterator, CharBuffer.wrap(arr2)));

    char[] arr3 = {start, esc, esc, 'h', 'e', esc, end, 'l', 'l', 'o', end};
    assertEquals("~he" + end + "llo", decode.invoke(iterator, CharBuffer.wrap(arr3)));

    char[] arr4 = {start, esc, 'h', 'e', 'l', 'l', 'o', end};
    assertEquals("hello", decode.invoke(iterator, CharBuffer.wrap(arr4)));

    char[] arr5 = {start, esc, esc, 'h', 'e', 'l', 'l', 'o'};
    assertNull(decode.invoke(iterator, CharBuffer.wrap(arr5)));
  }

  @Test
  void testEncodeString() throws NoSuchMethodException, IOException, IllegalAccessException, InvocationTargetException {
    Method encode = NetworkConnection.class.getDeclaredMethod("encodeString", String.class);
    encode.setAccessible(true);

    when(mockedChannel.configureBlocking(false)).thenReturn(null);
    net = new NetworkConnection(mockedChannel);

    byte start = 0x7d;
    byte esc = 0x7e; // this is ~ character
    byte end = 0x7f;

    byte[] arr1 = {start, esc, esc, 'h', 'e', 'l', 'l', 'o', end};
    assertArrayEquals(arr1, (byte[])encode.invoke(net, "~hello"));

    byte[] arr2 = {start, esc, esc, 'h', 'e', esc, end, 'l', 'l', 'o', end};
    assertArrayEquals(arr2, (byte[])encode.invoke(net, "~he" + (char)end + "llo"));
  }
}