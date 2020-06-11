package edu.northeastern.ccs.im;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.*;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;

import edu.northeastern.ccs.im.server.*;

/**
 * Tests for the ClientRunnable class.
 */
class ClientRunnableTest {
	private NetworkConnection mockConnection;

	private ClientRunnable clientRunnable;

	private String username = "username";
	private String password = "password";

	private String clientRunnableLoc;


	/**
	 * Simple iterator implementation for a list of messages.
	 * Allows easy mocking of NetworkConnection.iterator method.
	 */
	private class SimpleMessageIterator implements Iterator<Message> {
		private List<Message> messages;

		/**
		 * Instantiates a new Simple message iterator.
		 */
		SimpleMessageIterator() {
			messages = new ArrayList<>();
		}

		/**
		 * Instantiates a new Simple message iterator.
		 *
		 * @param messages the messages
		 */
		SimpleMessageIterator(Collection<? extends Message> messages) {
			this.messages = new ArrayList<>(messages.size());
			this.messages.addAll(messages);
		}

		@Override
		public boolean hasNext() {
			return !messages.isEmpty();
		}

		@Override
		public Message next() {
			if (messages.isEmpty()) {
				throw new NoSuchElementException();
			}
			return messages.remove(0);
		}
	}

	/**
	 * Setup mocks and common objects.
	 */
	@BeforeEach
	void testSetup() {
		mockConnection = mock(NetworkConnection.class);
		when(mockConnection.iterator()).thenReturn(new SimpleMessageIterator());
		when(mockConnection.sendMessage(any(Message.class))).thenReturn(true);

		clientRunnable = new ClientRunnable(mockConnection);
		clientRunnableLoc = "edu.northeastern.ccs.im.server.ClientRunnable";
	}

	/**
	 * Tests the enqueueMessage method.
	 */
	@Test
	void testEnqueueMessage() {
		// Runnable needs to be initialized for userId to be set.
		Message[] messages = {Message.makeRegisterMessage(username, password)};
		when(mockConnection.iterator()).thenReturn(new SimpleMessageIterator(Arrays.asList(messages)));
		clientRunnable.run();

		Message msg = Message.makeBroadcastMessage(username, "enqueued message");
		clientRunnable.enqueueMessage(msg);

		clientRunnable.run();

		verify(mockConnection).sendMessage(msg);
	}

	/**
	 * Tests the getName method.
	 */
	@Test
	void testGetName() {
		// Runnable needs to be initialized for userId to be set.
		Message[] messages = {Message.makeRegisterMessage(username, password)};
		when(mockConnection.iterator()).thenReturn(new SimpleMessageIterator(Arrays.asList(messages)));
		clientRunnable.run();

		assertEquals(username, clientRunnable.getName());
	}

	/**
	 * Tests the setName method.
	 */
	@Test
	void testSetName() {
		clientRunnable.setName("arbitrary");
		assertEquals("arbitrary", clientRunnable.getName());
	}

	/**
	 * Tests the getUserId method.
	 */
	@Test
	void testGetUserId() {
		// Runnable needs to be initialized for userId to be set.
		Message[] messages = {Message.makeRegisterMessage(username, password)};
		when(mockConnection.iterator()).thenReturn(new SimpleMessageIterator(Arrays.asList(messages)));
		clientRunnable.run();

		assertNotNull(clientRunnable.getUserId());
	}

	/**
	 * Tests isInitialized returns false when the ClientRunnable is not initialized.
	 */
	@Test
	void testIsInitializedWhenUninitialized() {
		assertFalse(clientRunnable.isInitialized());
	}

	/**
	 * Tests isInitialized returns true when the ClientRunnable is initialized.
	 */
	@Test
	void testIsInitializedWhenInitialized() {
		Message[] messages = {Message.makeRegisterMessage(username, password)};
		when(mockConnection.iterator()).thenReturn(new SimpleMessageIterator(Arrays.asList(messages)));

		clientRunnable.run();
		assertTrue(clientRunnable.isInitialized());
	}

	/**
	 * Tests the run method when it's been initialized but there's no outstanding messages in or out.
	 */
	@Test
	void testRunInitializedWithoutMessages() {
		Message[] messages = {Message.makeRegisterMessage(username, password)};
		when(mockConnection.iterator()).thenReturn(new SimpleMessageIterator(Arrays.asList(messages)));

		clientRunnable.run();
		assertTrue(clientRunnable.isInitialized());

		clientRunnable.run();
	}

	/**
	 * Tests the run method when it's not been initialized and there's a hello message.
	 * It should not initialize the ClientRunnable.
	 */
	@Test
	void testRunUninitializedWithHelloMessage() {
		Message[] messages = {Message.makeHelloMessage("John","qwerty")};
		when(mockConnection.iterator()).thenReturn(new SimpleMessageIterator(Arrays.asList(messages)));

		clientRunnable.run();
		assertFalse(clientRunnable.isInitialized());
	}

	/**
	 * Tests the run method when it's not been initialized and there's a login message.
	 * It should initialize the ClientRunnable.
	 */
	@Test
	void testRunUninitializedWitLoginMessage() {
		Message[] messages = {Message.makeRegisterMessage(username, password)};
		when(mockConnection.iterator()).thenReturn(new SimpleMessageIterator(Arrays.asList(messages)));

		assertFalse(clientRunnable.isInitialized());
		clientRunnable.run();
		assertTrue(clientRunnable.isInitialized());
	}

	/**
	 * Tests the run method when it's not been initialized and there's no login message.
	 */
	@Test
	void testRunUninitializedWithoutMessage() {
		clientRunnable.run();
		assertFalse(clientRunnable.isInitialized());
	}

	/**
	 * Tests the run method works when there's an incoming message from the user.
	 */
	@Test
	void testRunInitializedWithIncomingMessages() {
		Message[] message1 = {Message.makeRegisterMessage(username, password)};
		Message[] message2 = {Message.makeBroadcastMessage(username, "my message")};
		when(mockConnection.iterator()).thenReturn(new SimpleMessageIterator(Arrays.asList(message1)));

		clientRunnable.run();
		assertTrue(clientRunnable.isInitialized());

		when(mockConnection.iterator()).thenReturn(new SimpleMessageIterator(Arrays.asList(message2)));

		clientRunnable.run();
	}

	/**
	 * Tests the run method works when there's an invalid incoming message (invalid username).
	 */
	@Test
	void testRunInitializedWithInvalidIncomingMessage() {
		Message[] message1 = {Message.makeRegisterMessage(username, password)};
		Message[] message2 = {Message.makeBroadcastMessage("fake_user", "my message")};
		when(mockConnection.iterator()).thenReturn(new SimpleMessageIterator(Arrays.asList(message1)));

		clientRunnable.run();
		assertTrue(clientRunnable.isInitialized());

		when(mockConnection.iterator()).thenReturn(new SimpleMessageIterator(Arrays.asList(message2)));

		clientRunnable.run();
	}

	/**
	 * Tests the run method terminates the client when there's an incoming quit message.
	 */
	@Test
	void testRunInitializedWithIncomingQuitMessage() {
		Message[] message1 = {Message.makeRegisterMessage(username, password)};
		Message[] message2 = {Message.makeQuitMessage(username)};
		when(mockConnection.iterator()).thenReturn(new SimpleMessageIterator(Arrays.asList(message1)));

		ClientRunnable spyRunnable = spy(clientRunnable);

		spyRunnable.setFuture(mock(ScheduledFuture.class));

		spyRunnable.run();
		assertTrue(spyRunnable.isInitialized());

		when(mockConnection.iterator()).thenReturn(new SimpleMessageIterator(Arrays.asList(message2)));

		spyRunnable.run();

		verify(spyRunnable).terminateClient();
	}

	/**
	 * Tests the run method works when there's an outgoing message to send.
	 */
	@Test
	void testRunInitializedWithOutgoingMessages() {
		Message[] message1 = {Message.makeRegisterMessage(username, password)};
		Message message2 = Message.makeBroadcastMessage(username, "my message");
		when(mockConnection.iterator()).thenReturn(new SimpleMessageIterator(Arrays.asList(message1)));

		clientRunnable.run();
		assertTrue(clientRunnable.isInitialized());

		clientRunnable.enqueueMessage(message2);

		clientRunnable.run();
	}

	/**
	 * Tests the run method terminates the client when there's an outgoing message that fails to send.
	 */
	@Test
	void testRunInitializedWithOutgoingMessageFailure() {
		when(mockConnection.sendMessage(any(Message.class))).thenReturn(false);

		Message[] message1 = {Message.makeRegisterMessage(username, password)};
		Message message2 = Message.makeBroadcastMessage(username, "my message");
		when(mockConnection.iterator()).thenReturn(new SimpleMessageIterator(Arrays.asList(message1)));

		ClientRunnable spyRunnable = spy(clientRunnable);

		spyRunnable.setFuture(mock(ScheduledFuture.class));

		spyRunnable.run();
		assertTrue(spyRunnable.isInitialized());

		spyRunnable.enqueueMessage(message2);

		spyRunnable.run();

		verify(spyRunnable).terminateClient();
	}

	/**
	 * Tests the setFuture accepts a ScheduledFuture.
	 */
	@Test
	void testSetFuture() {
		ScheduledExecutorService threadPool = Executors.newSingleThreadScheduledExecutor();
		// Set timeout so it won't run, just want the object to make sure it's accepted properly.
		ScheduledFuture<?> future = threadPool.schedule(clientRunnable, 10, TimeUnit.MINUTES);
		clientRunnable.setFuture(future);
		// Make sure it won't run
		future.cancel(true);
	}

	/**
	 * Tests the terminateClient method closes the connection.
	 */
	@Test
	void testTerminateClient() {
		// pass a mock future so that the method call on the future
		// doesn't fail.
		clientRunnable.setFuture(mock(ScheduledFuture.class));

		clientRunnable.terminateClient();
		verify(mockConnection).close();
	}

	/**
	 * Tests correctly going into the isBehind() true branch in the ClientRunnable class.
	 *
	 * @throws ClassNotFoundException thrown if ClientRunnable is not found.
	 * @throws NoSuchFieldException   thrown if the timer field in ClientRunnable is not found.
	 * @throws IllegalAccessException thrown if the timer field is not set as accessible before being used.
	 */
	@Test
	void timerBehindForceTest() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
		mockConnection = mock(NetworkConnection.class);
		when(mockConnection.iterator()).thenReturn(new SimpleMessageIterator());
		when(mockConnection.sendMessage(any(Message.class))).thenReturn(true);
		ClientRunnable newRunnable = new NewRunnable(mockConnection);

		Field timerField = Class.forName(clientRunnableLoc).getDeclaredField("timer");
		timerField.setAccessible(true);
		ClientTimer newClient = new NewTimer();

		timerField.set(newRunnable, newClient);
		Field newTerminateVar = Class.forName(clientRunnableLoc).getDeclaredField("terminate");
		newTerminateVar.setAccessible(true);

		assertEquals("false", newTerminateVar.get(newRunnable).toString());
		newRunnable.run();
		newTerminateVar = Class.forName(clientRunnableLoc).getDeclaredField("terminate");
		newTerminateVar.setAccessible(true);
		assertEquals("true", newTerminateVar.get(newRunnable).toString());
	}

	/**
	 * Test set user id method.
	 */
	@Test
	public void testSetUserId()
	{
		mockConnection = mock(NetworkConnection.class);
		when(mockConnection.iterator()).thenReturn(new SimpleMessageIterator());
		when(mockConnection.sendMessage((String) any())).thenReturn(true);
		ClientRunnable newRunnable = new NewRunnable(mockConnection);
		newRunnable.setUserId("tom");
		assertEquals("tom",newRunnable.getUserId());
	}

	/**
	 * Teardown common test objects.
	 */
	@AfterEach
	void testTeardown() {
		mockConnection = null;
		clientRunnable = null;
	}

	/**
	 * Extending the ClientTimer, and forcing it's isBehind() method to return true, for testing.
	 */
	private class NewTimer extends ClientTimer {
		/**
		 * The isBehind() timing method, forced to return true.
		 *
		 * @return returns true, for testing purposes only.
		 */
		@Override
		public boolean isBehind() {
			return true;
		}
	}


	private class NewRunnable extends ClientRunnable {

		/**
		 * Create a new thread with which we will communicate with this single client.
		 *
		 * @param network NetworkConnection used by this new client
		 */
		NewRunnable(NetworkConnection network) {
			super(network);
		}


		@Override
		public void terminateClient() {
			/**
			 * Empty for testing purposes.
			 */

		}
	}
}