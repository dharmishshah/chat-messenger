package edu.northeastern.ccs.im.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import edu.northeastern.ccs.im.ChatLogger;
import edu.northeastern.ccs.im.Message;
import edu.northeastern.ccs.im.NetworkConnection;
import edu.northeastern.ccs.im.dataconnections.models.*;
import edu.northeastern.ccs.im.dataconnections.operations.*;
import edu.northeastern.ccs.im.messages.MessageTypes;
import edu.northeastern.ccs.im.server.models.Client;
import edu.northeastern.ccs.im.server.models.Group;
import edu.northeastern.ccs.im.server.models.User;
import edu.northeastern.ccs.im.server.utils.Utils;


/**
 * A network server that communicates with IM clients that connect to it. This
 * version of the server spawns a new thread to handle each client that connects
 * to it. At this point, messages are broadcast to all of the other clients. It
 * does not send a response when the user has gone off-line.
 * <p>
 * This work is licensed under the Creative Commons Attribution-ShareAlike 4.0
 * International License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by-sa/4.0/. It is based on work
 * originally written by Matthew Hertz and has been adapted for use in a class
 * assignment at Northeastern University.
 *
 * @version 1.3
 */
public class Prattle {
	/** Singleton instance */
	private static Prattle instance;

	/** Don't do anything unless the server is ready. */
	private boolean isReady = false;

	/** Collection of threads that are currently being used. */
	private ConcurrentLinkedQueue<ClientRunnable> active;

	private static Map<String, Client> activeUsers = new HashMap<>();


	private UserOperations userOperations;
	private GroupOperations groupOperations;
	private MessageOperations messageOperations;


	private Prattle() {
		// Create the new queue of active threads.
		active = new ConcurrentLinkedQueue<>();
		userOperations = new UserOperations();
		groupOperations = new GroupOperations();
		messageOperations = new MessageOperations();
	}

	/**
	 * Retrieve the singleton instance.
	 *
	 * @return singleton Prattle instance.
	 */
	public static Prattle getInstance() {
		if (instance == null) {
			instance = new Prattle();
		}
		return instance;
	}


	/**
	 * Starts the server in the singleton instance.
	 *
	 * @param args String arguments to the server from the command line. At present             there are no accepted arguments or options.
	 */
	public static void main(String[] args) {
		// Connect to the socket on the appropriate port to which this server connects.
		try ( ServerSocketChannel serverSocket = ServerSocketChannel.open()) {
			serverSocket.configureBlocking(false);
			serverSocket.socket().bind(new InetSocketAddress(ServerConstants.PORT));

			getInstance().startServer(serverSocket);
		} catch (IOException ex) {
			ChatLogger.error("Fatal error: " + ex.getMessage());
			throw new IllegalStateException(ex.getMessage());
		}
	}

	/**
	 * Broadcast a given message to all the other IM clients currently on the
	 * system. This message _will_ be sent to the client who originally sent it.
	 *
	 * @param message messages that the client sent.
	 */
	public void broadcastMessage(Message message) {
		// Loop through all of our active threads
		for (ClientRunnable tt : active) {
			// Do not send the message to any clients that are not ready to receive it.
			if (tt.isInitialized()) {
				tt.enqueueMessage(message);
			}
		}
	}

	/**
	 * Remove the given IM client from the list of active threads.
	 *
	 * @param dead Thread which had been handling all the I/O for a client who has             since quit.
	 */
	public void removeClient(ClientRunnable dead) {
		// Test and see if the thread was in our list of active clients so that we
		// can remove it.
		if (!active.remove(dead)) {
			ChatLogger.info("Could not find a thread that I tried to remove!\n");
		}
	}


	/**
	 * Register client. If a client with same username exists sends a failure message, success otherwise
	 *
	 * @param msg the message
	 */
	public void registerClient(Message msg) {
		String username = msg.getUserName();
		String password = msg.getPassword();
		String uniqueId = Utils.getUniqueId();



		if(userOperations.getChatterUserByUsername(username) == null)
		{
			User user = new User(uniqueId, username, password);
			activeUsers.put(uniqueId, user);
			ChatterUser chatterUser = (ChatterUser) userOperations.transferClient(user);
			boolean result = userOperations.createUser(chatterUser);
			if (result) {

				findAndSendMessage(username,Message.makeSuccessMessage());
				ChatLogger.info("Persisted user");


			} else {
				findAndSendMessage(username,Message.makeFailureMessage());
				ChatLogger.info("Persistance Failed");

			}
		}
		else
		{
			findAndSendMessage(username,Message.makeFailureMessage());
			ChatLogger.info("Username already exists");
		}

	}


	/**
	 * Finds a connected client with given username and sends message to that person.
	 *
	 * @param username Username of the user
	 * @param msg      Message to be sent to the user
	 * @return the boolean
	 */
	public boolean findAndSendMessage(String username, Message msg)
	{
		for(ClientRunnable tt:active)
		{
			if(username.equals(tt.getName()))
			{
				tt.enqueueMessage(msg);

				return true;
			}
		}
		return false;
	}


	/**
	 * Unicast message.
	 *
	 * @param msg            the message
	 * @param clientRunnable the client runnable
	 */
	public void unicastMessage(Message msg,ClientRunnable clientRunnable) {
		String receiverId = msg.getMsgReceiver();
		msg.setMsgSender(clientRunnable.getName());
		ChatterMessage chatterMessage = messageOperations.transferMessage(msg,MessageTypes.UNICAST);
		boolean result = messageOperations.createMessage(chatterMessage);
		if(!result)
		{
			ChatLogger.error("ucm failed to persist");
		}

		if(findAndSendMessage(receiverId,msg))
		{
			chatterMessage.setMessageStatus(Status.READ);
			messageOperations.updateMessage(chatterMessage);
		}
	}


	/**
	 * multicast message.
	 *
	 * @param msg            the msg
	 * @param clientRunnable the client runnable
	 */
	public void multicastMessage(Message msg,ClientRunnable clientRunnable) {

		String receiverId = msg.getMsgReceiver();
		msg.setMsgSender(clientRunnable.getName());

		ChatterGroup chatterGroup = groupOperations.getChatterGroupByUsername(receiverId);
		List<ChatterClient> users = chatterGroup.getMembers();

		ChatterMessage chatterMessage = messageOperations.transferMessage(msg, MessageTypes.MULTICAST);
		boolean result = messageOperations.createMessage(chatterMessage);
		if(result)
		{
			ChatLogger.error("Group message could not be persisted");
		}

		List<String> userIds = new ArrayList<>();
		for(ChatterClient user:users){
			userIds.add(user.getName());
		}

		for(ClientRunnable tt:active)
		{
			if(tt.getUserId() !=null && userIds.contains(tt.getUserId()) && tt.getUserId()!=clientRunnable.getName())
			{
				tt.enqueueMessage(msg);
			}
		}
	}


	/**
	 * Retrieve all public groups.
	 *
	 * @param clientRunnable the client runnable that sends the request
	 */
	public void retrieveAllPublicGroup(ClientRunnable clientRunnable)
	{
		List<String> names = groupOperations.getPublicGroups();
		String groupNames=  String.join(",",names);
		Message msg = Message.makeUniCastMessage(groupNames,clientRunnable.getUserId()) ;
		msg.setMsgSender("SERVER#RAG");
		findAndSendMessage(clientRunnable.getName(), msg);
	}


	/**
	 * login client.
	 *
	 * @param msg            the message
	 * @param clientRunnable the client runnable
	 */
	public void loginClient(Message msg, ClientRunnable clientRunnable)
	{
		String username = msg.getUserName();
		String password = msg.getPassword();

		ChatterUser cu = userOperations.getChatterUserByUsername(username);


		if(cu==null  || (!cu.getPassword().equals(password))){
			findAndSendMessage(username,Message.makeFailureMessage());
			ChatLogger.info("Login Failed");
		}
		else{
			for(ClientRunnable tt:active) {
				if (tt != null && tt.isInitialized() && tt.getName().equals(clientRunnable.getName())) {
					tt.setUserId(username);
					findAndSendMessage(username,Message.makeSuccessMessage());
					ChatLogger.info("Login Success");
				}
			}

			List<ChatterMessage> unreadMessages = messageOperations.getAllUnreadMessages(username);
			for(ChatterMessage cm:unreadMessages)
			{
				ChatterUser u = userOperations.getChatterUserById(cm.getMessageSender());
				Message m = Message.makeUniCastMessage(cm.getMessageContent(),u.getName());
				findAndSendMessage(username,m);
			}
		}
	}


	/**
	 * Terminates the server.
	 */
	public void stopServer() {
		isReady = false;
	}

	/**
	 * Start up the threaded talk server. This class accepts incoming connections on
	 * a specific port specified on the command-line. Whenever it receives a new
	 * connection, it will spawn a thread to perform all of the I/O with that
	 * client. This class relies on the server not receiving too many requests -- it
	 * does not include any code to limit the number of extant threads.
	 *
	 * @param serverSocket the server socket
	 * @throws IOException Exception thrown if the server cannot connect to the port                     to which it is supposed to listen.
	 */
	public void startServer(ServerSocketChannel serverSocket) throws IOException {
		// Create the Selector with which our channel is registered.
		Selector selector = SelectorProvider.provider().openSelector();
		// Register to receive any incoming connection messages.
		serverSocket.register(selector, SelectionKey.OP_ACCEPT);
		// Create our pool of threads on which we will execute.
		ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(ServerConstants.THREAD_POOL_SIZE);
		// If we get this far than the server is initialized correctly
		isReady = true;
		// Now listen on this port as long as the server is ready
		while (isReady) {
			// Check if we have a valid incoming request, but limit the time we may wait.
			while (selector.select(ServerConstants.DELAY_IN_MS) != 0) {
				// Get the list of keys that have arrived since our last check
				Set<SelectionKey> acceptKeys = selector.selectedKeys();
				// Now iterate through all of the keys
				Iterator<SelectionKey> it = acceptKeys.iterator();
				while (it.hasNext()) {
					// Get the next key; it had better be from a new incoming connection
					SelectionKey key = it.next();
					it.remove();
					// Assert certain things I really hope is true
					assert key.isAcceptable();
					if(key.channel() != serverSocket){
						throw new AssertionError();
					}
					// Create new thread to handle client for which we just received request.
					createClientThread(serverSocket, threadPool);
				}
			}
		}

	}


	/**
	 * Add moderator.
	 *
	 * @param groupName      the group name
	 * @param username       the username
	 * @param clientRunnable the client runnable
	 */
	public void addModerator(String groupName,String username,ClientRunnable clientRunnable)
	{

		ChatterGroup cg = groupOperations.getChatterGroupByUsername(groupName);
		List<ChatterClient> members = cg.getAllMembers();
		ChatterUser cu = userOperations.getChatterUserByUsername(username);
		List<ChatterClient> moderators = cg.getAllModerators();
		ChatterClient client = userOperations.getChatterUserByUsername(clientRunnable.getName());

		if(moderators.contains(client))
		{
			if (members.contains(cu)) {
				moderators.add(cu);
				cg.setModerators(moderators);
				groupOperations.addMemberToGroup(cg);

			} else {
				findAndSendMessage(clientRunnable.getName(), Message.makeFailureMessage());
			}
		}
		else
		{
			ChatLogger.info("User is not a moderator");
		}
	}


	/**
	 * Join agroup.
	 *
	 * @param groupName      the group name
	 * @param clientRunnable the client runnable
	 */
	public void joinAgroup(String groupName,ClientRunnable clientRunnable)
	{
		String username = clientRunnable.getName();
		ChatterUser chatterUser = userOperations.getChatterUserByUsername(username);
		ChatterGroup chatterGroup = groupOperations.getChatterGroupByUsername(groupName);
		if(chatterGroup.getGroupType() == 1) {
			List<ChatterClient> members = chatterGroup.getAllMembers();
			if (!members.contains(chatterUser)) {
				members.add(chatterUser);
			}
			chatterGroup.setMembers(members);
			if(groupOperations.addMemberToGroup(chatterGroup))
			{
				findAndSendMessage(clientRunnable.getName(),Message.makeSuccessMessage());
			}

		}
		else
		{
			findAndSendMessage(clientRunnable.getName(),Message.makeFailureMessage());
		}
	}





	/**
	 * Create a new thread to handle the client for which a request is received.
	 * 
	 * @param serverSocket The channel to use.
	 * @param threadPool   The thread pool to add client to.
	 */
	private void createClientThread(ServerSocketChannel serverSocket, ScheduledExecutorService threadPool) {
		try {
			// Accept the connection and create a new thread to handle this client.
			SocketChannel socket = serverSocket.accept();
			// Make sure we have a connection to work with.
			if (socket != null) {
			    NetworkConnection connection = new NetworkConnection(socket);
				ClientRunnable tt = new ClientRunnable(connection);
				// Add the thread to the queue of active threads
				active.add(tt);
				// Have the client executed by our pool of threads.
				ScheduledFuture<?> clientFuture = threadPool.scheduleAtFixedRate(tt, ServerConstants.CLIENT_CHECK_DELAY,
						ServerConstants.CLIENT_CHECK_DELAY, TimeUnit.MILLISECONDS);
				tt.setFuture(clientFuture);
			}
		}
		catch (IOException e) {
			ChatLogger.error("Caught Exception: " + e.toString());
		}
	}


	/**
	 * Create group
	 *
	 * @param groupName      the group name
	 * @param clientRunnable the client runnable
	 */
	public void createGroup(String groupName, ClientRunnable clientRunnable) {

		String password= null;
		String uniqueId = Utils.getUniqueId();
		String creator = clientRunnable.getName();
		Client creatorClient = null;
		ChatterUser chatterUser = userOperations.getChatterUserByUsername(creator);

		if(chatterUser != null) {
			creatorClient = new User(chatterUser.getUniqueId(),chatterUser.getName(),chatterUser.getPassword());
			Group group = new Group(creatorClient, uniqueId, groupName, password);
			activeUsers.put(uniqueId, group);
			ChatterGroup chatterGroup = (ChatterGroup) groupOperations.transferGroup(group);
			chatterGroup.setGroupType(1);
			boolean result = groupOperations.createGroup(chatterGroup);
			if (result) {
				findAndSendMessage(clientRunnable.getUserId(),Message.makeSuccessMessage());
				ChatLogger.info("Group Created: " + group.getName());
			} else {
				ChatLogger.info("Group could not be created");
			}
		}


	}

	/**
	 * Add member.
	 *
	 * @param groupName      the group name
	 * @param userName       the user name
	 * @param clientRunnable the client runnable
	 */
	public void addMember(String groupName, String userName,ClientRunnable clientRunnable) {

		boolean userExists = false ;

		ChatterGroup chatterGroup = groupOperations.getChatterGroupByUsername(groupName);
		boolean inGroup = groupOperations.getChatterGroupByAdminUsername(clientRunnable.getName(),groupName);




		ChatterUser chatterUser = userOperations.getChatterUserByUsername(userName);

		if(chatterUser!=null)
		{
			userExists = true;
		}


		if(userExists && inGroup) {
			List<ChatterClient> chatterClients = chatterGroup.getAllMembers();
			chatterClients.add(chatterUser);
			chatterGroup.setMembers(chatterClients);
			boolean result = groupOperations.addMemberToGroup(chatterGroup);
			if(result)
			{

				ChatLogger.info("User successfully added");
			}

		}

		if(userExists )
		{
			ChatLogger.info("user does not exist in the system");
		}
		if(inGroup)
		{
			ChatLogger.info("User is not a Moderator");
		}


	}


	/**
	 * Remove member.
	 *
	 * @param groupName      the group name
	 * @param userName       the user name
	 * @param clientRunnable the client runnable
	 */
	public void removeMember(String groupName,String userName,ClientRunnable clientRunnable)
	{
		boolean userExists = false;

		ChatterGroup chatterGroup = groupOperations.getChatterGroupByUsername(groupName);
		boolean inGroup = groupOperations.getChatterGroupByAdminUsername(clientRunnable.getName(),groupName);


		ChatterUser chatterUser = userOperations.getChatterUserByUsername(userName);

		if(chatterUser!=null)
		{
			userExists = true;
		}

		if(userExists && inGroup)
		{
			List<ChatterClient> chatterClients = chatterGroup.getAllMembers();
			ChatterClient removeClient = null;
			for(ChatterClient c:chatterClients)
			{
				if(c.getName().equals(chatterUser.getName()))
				{
					removeClient = c;
				}
			}
			chatterClients.remove(removeClient);
			chatterGroup.setMembers(chatterClients);
			boolean result = groupOperations.addMemberToGroup(chatterGroup);
			if(result)
			{
				ChatLogger.info("User successfully removed");
			}



		}

		if(!userExists)
		{
			ChatLogger.info("insider remove User: User does not exist in the system");
		}
		if(!inGroup)
		{
			ChatLogger.info("Inside remove User: User is not a Moderator");
		}

	}


	/**
	 * Delete group by given username.
	 *
	 * @param groupName      the group name
	 * @param clientRunnable the client runnable
	 */
	public void deleteGroup(String groupName,ClientRunnable clientRunnable)
	{

		ChatterGroup chatterGroup = groupOperations.getChatterGroupByUsername(groupName);

		if(chatterGroup != null)
		{
			groupOperations.deleteGroup(chatterGroup);
		}


		else{
			ChatLogger.info("Group does not exist");
		}

	}
}