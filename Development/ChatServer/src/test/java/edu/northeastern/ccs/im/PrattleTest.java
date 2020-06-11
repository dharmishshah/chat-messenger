package edu.northeastern.ccs.im;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import edu.northeastern.ccs.im.dataconnections.models.ChatterGroup;
import edu.northeastern.ccs.im.dataconnections.operations.DataBaseOperations;
import edu.northeastern.ccs.im.dataconnections.operations.GroupOperations;
import edu.northeastern.ccs.im.server.models.Client;
import edu.northeastern.ccs.im.server.models.Group;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledExecutorService;

import edu.northeastern.ccs.im.server.*;


/**
 * Test for the Prattle class
 */
public class PrattleTest {

    /**
     * Testing the Prattle main class.
     */
    @BeforeAll
    public static void testMain() {
        mainThread = new Thread(()->{
            String[] argsInput = {"4555"};
            Prattle.main(argsInput);
        });
        mainThread.start();
    }

    /**
     * After each.
     */
    @AfterEach
    public  void afterEach()
    {
        DataBaseOperations dbo = new DataBaseOperations();
        dbo.deleteAllRecords("ChatterGroup");
        dbo.deleteAllRecords("ChatterUser");
    }

    /**
     * Test reset users.
     *
     * @throws NoSuchFieldException   the no such field exception
     * @throws IllegalAccessException the illegal access exception
     */
    @BeforeEach
    public void testResetUsers() throws NoSuchFieldException, IllegalAccessException {
        Prattle p = Prattle.getInstance();
        Field f= p.getClass().getDeclaredField("active");
        f.setAccessible(true);
        ConcurrentLinkedQueue<ClientRunnable> active = (ConcurrentLinkedQueue<ClientRunnable>)f.get(p);
        active.clear();

        Field f1= p.getClass().getDeclaredField("activeUsers");
        f1.setAccessible(true);
        Map<String, Client> activeUsers = new HashMap<>();
        active.clear();
        activeUsers.clear();

        f.set(p,active);
        f1.set(p,activeUsers);
        p.stopServer();
    }

    /**
     * Test unicast message.
     *
     * @throws NoSuchFieldException   the no such field exception
     * @throws IllegalAccessException the illegal access exception
     * @throws IOException            the io exception
     * @throws ClassNotFoundException the class not found exception
     */
    @Test
    public void testUnicastMessage() throws NoSuchFieldException, IllegalAccessException, IOException, ClassNotFoundException {

        Prattle p = Prattle.getInstance();

        SocketChannel sc = SocketChannel.open();
        sc.configureBlocking(false);
        sc.connect(new InetSocketAddress("localhost", 4545));
        NetworkConnection nc = new NetworkConnection(sc);
        ClientRunnable clientRunnable = new ClientRunnable(nc);

        ClientRunnable clientRunnable2 = new ClientRunnable(nc);




        Field cli= clientRunnable.getClass().getDeclaredField("initialized");
        cli.setAccessible(true);
        cli.set(clientRunnable,true);
        Field cli2= clientRunnable.getClass().getDeclaredField("name");
        cli2.setAccessible(true);
        cli2.set(clientRunnable,"user");
        Field f= p.getClass().getDeclaredField("active");
        f.setAccessible(true);
        ConcurrentLinkedQueue<ClientRunnable> active = (ConcurrentLinkedQueue<ClientRunnable>)f.get(p);
        active.add(clientRunnable);


        Field cli3= clientRunnable2.getClass().getDeclaredField("initialized");
        cli3.setAccessible(true);
        cli3.set(clientRunnable2,true);
        Field cli4= clientRunnable2.getClass().getDeclaredField("name");
        cli4.setAccessible(true);
        cli4.set(clientRunnable2,"active1");

        active.add(clientRunnable2);
        f.set(p,active);

        Message msgR = Message.makeRegisterMessage("user", "message");
        p.registerClient(msgR);
        Message msgH = Message.makeLoginMessage("user", "message");
        p.loginClient(msgH, clientRunnable);


        Message msgR1 = Message.makeRegisterMessage("user1", "message");
        p.registerClient(msgR1);
        Message msgH1 = Message.makeLoginMessage("user1", "message");
        p.loginClient(msgH1, clientRunnable2);



        Field f4=  p.getClass().getDeclaredField("activeUsers");
        f4.setAccessible(true);
        Map<String,Client> act = (Map<String,Client>)f4.get(p);


        p.unicastMessage(Message.makeUniCastMessage("Hello World","user1"),clientRunnable);


        Field f3= ClientRunnable.class.getDeclaredField("waitingList");
        f3.setAccessible(true);
        Queue<Message> h = (Queue<Message>) f3.get(clientRunnable2);
        if(h != null && h.peek() != null){
            assertEquals("Hello World",h.peek().getText());
        }




        p.stopServer();


    }


    /**
     * Multi cast message test.
     *
     * @throws NoSuchFieldException   the no such field exception
     * @throws IllegalAccessException the illegal access exception
     * @throws IOException            the io exception
     * @throws ClassNotFoundException the class not found exception
     */
    @Test
    public void multiCastMessageTest() throws NoSuchFieldException, IllegalAccessException,
            IOException, ClassNotFoundException
    {
        Prattle p = Prattle.getInstance();

        SocketChannel sc = SocketChannel.open();
        sc.configureBlocking(false);
        sc.connect(new InetSocketAddress("localhost", 4545));
        NetworkConnection nc = new NetworkConnection(sc);
        ClientRunnable clientRunnable = new ClientRunnable(nc);
        ClientRunnable clientRunnable2 = new ClientRunnable(nc);
        ClientRunnable clientRunnable3 = new ClientRunnable(nc);


        Field cli= clientRunnable.getClass().getDeclaredField("initialized");
        cli.setAccessible(true);
        cli.set(clientRunnable,true);
        Field cli2= clientRunnable.getClass().getDeclaredField("name");
        cli2.setAccessible(true);
        cli2.set(clientRunnable,"active");
        Field f= p.getClass().getDeclaredField("active");
        f.setAccessible(true);
        f.set(p,new ConcurrentLinkedQueue<ClientRunnable>());
        ConcurrentLinkedQueue<ClientRunnable> active = (ConcurrentLinkedQueue<ClientRunnable>)f.get(p);
        active.add(clientRunnable);


        Field cli3= clientRunnable2.getClass().getDeclaredField("initialized");
        cli3.setAccessible(true);
        cli3.set(clientRunnable2,true);
        Field cli4= clientRunnable2.getClass().getDeclaredField("name");
        cli4.setAccessible(true);
        cli4.set(clientRunnable2,"active1");

        active.add(clientRunnable2);
        f.set(p,active);


        Field cli5= clientRunnable3.getClass().getDeclaredField("initialized");
        cli5.setAccessible(true);
        cli5.set(clientRunnable3,true);
        Field cli6= clientRunnable3.getClass().getDeclaredField("name");
        cli6.setAccessible(true);
        cli6.set(clientRunnable3,"active2");

        active.add(clientRunnable3);
        f.set(p,active);

        Message msgR = Message.makeRegisterMessage("user3", "message");
        p.registerClient(msgR);
        Message msgH = Message.makeLoginMessage("user3", "message");
        p.loginClient(msgH, clientRunnable);


        Message msgR1 = Message.makeRegisterMessage("user4", "message");
        p.registerClient(msgR1);
        Message msgH1 = Message.makeLoginMessage("user4", "message");
        p.loginClient(msgH1, clientRunnable2);

        Message msgR2 = Message.makeRegisterMessage("user5", "message");
        p.registerClient(msgR2);
        Message msgH2 = Message.makeLoginMessage("user5", "message");
        p.loginClient(msgH2, clientRunnable3);



        p.createGroup("group1",clientRunnable);
        p.addMember("group1","user5",clientRunnable);
        Message multicast = Message.makeMultiCastMessage("Hello group","group1");
        multicast.setMsgSender("user3");
//        p.multicastMessage(multicast,clientRunnable);
        p.stopServer();

    }

    /**
     * Create group message test.
     *
     * @throws NoSuchFieldException   the no such field exception
     * @throws IOException            the io exception
     * @throws IllegalAccessException the illegal access exception
     */
    @Test
    public void createGroupMessageTest() throws NoSuchFieldException, IOException, IllegalAccessException {
        Prattle p= Prattle.getInstance();
        SocketChannel sc = SocketChannel.open();
        sc.configureBlocking(false);
        sc.connect(new InetSocketAddress("localhost", 4545));
        NetworkConnection nc = new NetworkConnection(sc);

        ClientRunnable clientRunnable= new ClientRunnable(nc);
        Field cli= clientRunnable.getClass().getDeclaredField("initialized");
        cli.setAccessible(true);
        cli.set(clientRunnable,true);
        Field cli2= clientRunnable.getClass().getDeclaredField("name");
        cli2.setAccessible(true);
        cli2.set(clientRunnable,"active");

        Field f= p.getClass().getDeclaredField("active");
        f.setAccessible(true);
        ConcurrentLinkedQueue<ClientRunnable> active = (ConcurrentLinkedQueue<ClientRunnable>)f.get(p);
        active.add(clientRunnable);
        f.set(p,active);


        //register and login client
        Message msgR = Message.makeRegisterMessage("user6", "message");
        p.registerClient(msgR);
        Message msgH = Message.makeLoginMessage("user6", "message");
        p.loginClient(msgH, clientRunnable);


        p.createGroup("group1",clientRunnable);

        Field f1= p.getClass().getDeclaredField("activeUsers");
        f1.setAccessible(true);
        Map<String,Client> activeUsers = (Map<String,Client> )f1.get(p);
        for(Map.Entry<String,Client> entry:activeUsers.entrySet())
        {

            if(entry.getValue().getClass().equals("edu.northeastern.ccs.im.server.models.Group"))
            {
                Group g = (Group)entry.getValue();
                assertEquals("group1",g.getName());
                assertEquals("user6",g.getModerators().get(0).getName());
            }
        }
        sc.close();


    }


    /**
     * Join group test.
     *
     * @throws IOException            the io exception
     * @throws NoSuchFieldException   the no such field exception
     * @throws IllegalAccessException the illegal access exception
     */
    @Test
    public void joinGroupTest() throws IOException,NoSuchFieldException,IllegalAccessException
    {
        Prattle p = Prattle.getInstance();
        SocketChannel sc = SocketChannel.open();
        sc.configureBlocking(false);
        sc.connect(new InetSocketAddress("localhost", 4545));
        NetworkConnection nc = new NetworkConnection(sc);

        ClientRunnable clientRunnable= new ClientRunnable(nc);
        Field cli= clientRunnable.getClass().getDeclaredField("initialized");
        cli.setAccessible(true);
        cli.set(clientRunnable,true);
        Field cli2= clientRunnable.getClass().getDeclaredField("name");
        cli2.setAccessible(true);
        cli2.set(clientRunnable,"user7");

        ClientRunnable clientRunnable2 = new ClientRunnable(nc);
        Field cli1= clientRunnable.getClass().getDeclaredField("initialized");
        cli1.setAccessible(true);
        cli1.set(clientRunnable,true);
        Field cli3= clientRunnable.getClass().getDeclaredField("name");
        cli3.setAccessible(true);
        cli3.set(clientRunnable2,"user8");
        Field cli4 = clientRunnable.getClass().getDeclaredField("userId");
        cli4.setAccessible(true);
        cli4.set(clientRunnable2,"user8");

        Field f = p.getClass().getDeclaredField("active");
        f.setAccessible(true);
        ConcurrentLinkedQueue<ClientRunnable> active = (ConcurrentLinkedQueue<ClientRunnable>)f.get(p);
        active.add(clientRunnable);
        active.add(clientRunnable2);

        //register and login client
        Message msgR = Message.makeRegisterMessage("user7", "message");
        p.registerClient(msgR);
        Message msgH = Message.makeLoginMessage("user7", "message");
        p.loginClient(msgH, clientRunnable);


        //register and login client
        Message msgR1 = Message.makeRegisterMessage("user8", "message");
        p.registerClient(msgR1);
        Message msgH1 = Message.makeLoginMessage("user8", "message");
        p.loginClient(msgH1, clientRunnable2);

        p.createGroup("group1",clientRunnable);
        p.addMember("group1","user9",clientRunnable);
        p.joinAgroup("group1",clientRunnable2);

        GroupOperations groupOperations = new GroupOperations();
        ChatterGroup cg = groupOperations.getChatterGroupByUsername("group1");
        cg.setGroupType(1);
        groupOperations.addMemberToGroup(cg);

        p.joinAgroup("group1",clientRunnable2);
        p.removeMember("group1","user8",clientRunnable);


        p.deleteGroup("group1",clientRunnable);
    }

    /**
     * Add memeber test.
     *
     * @throws IOException            the io exception
     * @throws NoSuchFieldException   the no such field exception
     * @throws IllegalAccessException the illegal access exception
     */
    @Test
    public void addMemeberTest() throws IOException,NoSuchFieldException,IllegalAccessException
    {
        Prattle p = Prattle.getInstance();
        SocketChannel sc = SocketChannel.open();
        sc.configureBlocking(false);
        sc.connect(new InetSocketAddress("localhost", 4545));
        NetworkConnection nc = new NetworkConnection(sc);

        ClientRunnable clientRunnable= new ClientRunnable(nc);
        Field cli= clientRunnable.getClass().getDeclaredField("initialized");
        cli.setAccessible(true);
        cli.set(clientRunnable,true);
        Field cli2= clientRunnable.getClass().getDeclaredField("name");
        cli2.setAccessible(true);
        cli2.set(clientRunnable,"user7");

        ClientRunnable clientRunnable2 = new ClientRunnable(nc);
        Field cli1= clientRunnable.getClass().getDeclaredField("initialized");
        cli1.setAccessible(true);
        cli1.set(clientRunnable,true);
        Field cli3= clientRunnable.getClass().getDeclaredField("name");
        cli3.setAccessible(true);
        cli3.set(clientRunnable2,"user8");


        Field f = p.getClass().getDeclaredField("active");
        f.setAccessible(true);
        ConcurrentLinkedQueue<ClientRunnable> active = (ConcurrentLinkedQueue<ClientRunnable>)f.get(p);
        active.add(clientRunnable);
        active.add(clientRunnable2);

        //register and login client
        Message msgR = Message.makeRegisterMessage("user7", "message");
        p.registerClient(msgR);
        Message msgH = Message.makeLoginMessage("user7", "message");
        p.loginClient(msgH, clientRunnable);


        //register and login client
        Message msgR1 = Message.makeRegisterMessage("user8", "message");
        p.registerClient(msgR1);
        Message msgH1 = Message.makeLoginMessage("user8", "message");
        p.loginClient(msgH1, clientRunnable2);

        p.createGroup("group1",clientRunnable);




        p.addMember("group1","user8",clientRunnable);
        p.addMember("group1","user9",clientRunnable);
        p.addMember("group1","user8",clientRunnable2);
        p.removeMember("group1","user8",clientRunnable);

        p.removeMember("group1","user9",clientRunnable);

        p.removeMember("group1","user8",clientRunnable2);


        p.deleteGroup("group1",clientRunnable);
    }


    /**
     * Add moderator test.
     *
     * @throws IOException            the io exception
     * @throws NoSuchFieldException   the no such field exception
     * @throws IllegalAccessException the illegal access exception
     */
    @Test
    public void addModeratorTest() throws IOException,NoSuchFieldException,IllegalAccessException
    {
        Prattle p = Prattle.getInstance();
        SocketChannel sc = SocketChannel.open();
        sc.configureBlocking(false);
        sc.connect(new InetSocketAddress("localhost", 4545));
        NetworkConnection nc = new NetworkConnection(sc);

        ClientRunnable clientRunnable= new ClientRunnable(nc);
        Field cli= clientRunnable.getClass().getDeclaredField("initialized");
        cli.setAccessible(true);
        cli.set(clientRunnable,true);
        Field cli2= clientRunnable.getClass().getDeclaredField("name");
        cli2.setAccessible(true);
        cli2.set(clientRunnable,"user7");

        ClientRunnable clientRunnable2 = new ClientRunnable(nc);
        Field cli1= clientRunnable.getClass().getDeclaredField("initialized");
        cli1.setAccessible(true);
        cli1.set(clientRunnable,true);
        Field cli3= clientRunnable.getClass().getDeclaredField("name");
        cli3.setAccessible(true);
        cli3.set(clientRunnable2,"user8");


        Field f = p.getClass().getDeclaredField("active");
        f.setAccessible(true);
        ConcurrentLinkedQueue<ClientRunnable> active = (ConcurrentLinkedQueue<ClientRunnable>)f.get(p);
        active.add(clientRunnable);
        active.add(clientRunnable2);

        //register and login client
        Message msgR = Message.makeRegisterMessage("user7", "message");
        p.registerClient(msgR);
        Message msgH = Message.makeLoginMessage("user7", "message");
        p.loginClient(msgH, clientRunnable);


        //register and login client
        Message msgR1 = Message.makeRegisterMessage("user8", "message");
        p.registerClient(msgR1);
        Message msgH1 = Message.makeLoginMessage("user8", "message");
        p.loginClient(msgH1, clientRunnable2);

        p.createGroup("group1",clientRunnable);




        p.addMember("group1","user8",clientRunnable);
        p.addMember("group1","user9",clientRunnable);
        p.addMember("group1","user8",clientRunnable2);

        p.addModerator("group1","user9",clientRunnable2);


        p.addModerator("group1","user8",clientRunnable);
        p.removeMember("group1","user8",clientRunnable);

        p.removeMember("group1","user9",clientRunnable);
        p.addModerator("group1","user9",clientRunnable);
        p.removeMember("group1","user8",clientRunnable2);


        p.deleteGroup("group1",clientRunnable);
    }


    /**
     * Tests that the instance returned by the singleton retrieval method
     * is actually the same object each time.
     */
    @Test
    void testSingletonSameObject() {
        assertSame(Prattle.getInstance(), Prattle.getInstance());
    }
    private Prattle testPrattle;
    private String prattleLoc;
    private static Thread mainThread;

    /**
     * Initial setup, making a mock Prattle object.
     */
    @BeforeEach
    void setUp(){
        testPrattle = Prattle.getInstance();
        prattleLoc = "edu.northeastern.ccs.im.server.Prattle";

    }

    /**
     * Testing that the message method fires correctly when given a Queue that is not empty.
     *
     * @throws ClassNotFoundException if Prattle is not found.
     * @throws NoSuchFieldException   if the Queue field is not found.
     * @throws IllegalAccessException if the Queue field is attempted to be accessed before being made accessible.
     */
    @Test
    void prattleMessageTest() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        ConcurrentLinkedQueue<ClientRunnable> newActive = new ConcurrentLinkedQueue<>();
        ClientRunnable mockRunnableOne = mock(ClientRunnable.class);
        ClientRunnable mockRunnableTwo = mock(ClientRunnable.class);
        when(mockRunnableOne.isInitialized()).thenReturn(true);
        when(mockRunnableTwo.isInitialized()).thenReturn(true);

        newActive.add(mockRunnableOne);
        newActive.add(mockRunnableTwo);

        Field prField = Class.forName(prattleLoc).getDeclaredField("active");
        prField.setAccessible(true);
        prField.set(testPrattle, newActive);

        testPrattle.broadcastMessage(Message.makeBroadcastMessage("Tom", "hi"));
    }


    /**
     * Testing that the message method fires correctly when given a Queue that is not empty.
     *
     * @throws ClassNotFoundException if Prattle is not found.
     * @throws NoSuchFieldException   if the Queue field is not found.
     * @throws IllegalAccessException if the Queue field is attempted to be accessed before being made accessible.
     */
    @Test
    void prattleRegistrationMessageTest() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        ConcurrentLinkedQueue<ClientRunnable> newActive = new ConcurrentLinkedQueue<>();
        ClientRunnable mockRunnableOne = mock(ClientRunnable.class);
        ClientRunnable mockRunnableTwo = mock(ClientRunnable.class);
        when(mockRunnableOne.isInitialized()).thenReturn(true);
        when(mockRunnableTwo.isInitialized()).thenReturn(true);

        Field prField1 =
                Class.forName("edu.northeastern.ccs.im.server.ClientRunnable").getDeclaredField(
                        "name");
        prField1.setAccessible(true);
        prField1.set(mockRunnableOne, "Tom");

        Field prField2 =
                Class.forName("edu.northeastern.ccs.im.server.ClientRunnable").getDeclaredField(
                        "name");
        prField2.setAccessible(true);
        prField2.set(mockRunnableTwo, "James");

        newActive.add(mockRunnableOne);
        newActive.add(mockRunnableTwo);

        Field prField = Class.forName(prattleLoc).getDeclaredField("active");
        prField.setAccessible(true);
        prField.set(testPrattle, newActive);

        testPrattle.registerClient(Message.makeRegisterMessage("Tom", "hi"));

    }

    /**
     * Testing that the stopServer() method correctly works.
     *
     * @throws ClassNotFoundException if Prattle is not found.
     * @throws NoSuchFieldException   if the isReady field is not found.
     * @throws IllegalAccessException if the isReady field is not set to accessible before access.
     */
    @Test
    void isReadyTest() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        Field isReadyField = Class.forName(prattleLoc).getDeclaredField("isReady");
        isReadyField.setAccessible(true);
        isReadyField.set(testPrattle, true);
        assertEquals("true", isReadyField.get(testPrattle).toString());

        testPrattle.stopServer();
        isReadyField = Class.forName(prattleLoc).getDeclaredField("isReady");
        isReadyField.setAccessible(true);
        assertEquals("false", isReadyField.get(testPrattle).toString());
    }


    /**
     * Tests the createClientThread method inside Prattle
     *
     * @throws NoSuchMethodException     if createClientThread is not found.
     * @throws InvocationTargetException if the invocation fails.
     * @throws IllegalAccessException    if the method is not made accessible before being used.
     * @throws IOException               thrown if mock channel fails
     */
    @Test
    void createThreadTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException {
        Method readMethod = testPrattle.getClass().getDeclaredMethod("createClientThread",ServerSocketChannel.class, ScheduledExecutorService.class);
        readMethod.setAccessible(true);
        ServerSocketChannel mockSerSock = mock(ServerSocketChannel.class);
        when(mockSerSock.accept()).thenReturn(mock(SocketChannel.class));
        ScheduledExecutorService mockService = mock(ScheduledExecutorService.class);

        readMethod.invoke(testPrattle,mockSerSock,mockService);
    }


    /**
     * Change modifier of the given field from final to non-final and set value.
     *
     * @param f the f
     * @throws IllegalAccessException the illegal access exception
     * @throws NoSuchFieldException   the no such field exception
     */
    static void changeModifier(Field f) throws IllegalAccessException, NoSuchFieldException {
        f.setAccessible(true);

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(f, f.getModifiers() & ~Modifier.FINAL);
        f.set(null,6060);
    }

//    /**
//     * Main io exception test.
//     *
//     * @throws NoSuchFieldException   the no such field exception
//     * @throws IllegalAccessException the illegal access exception
//     */
//    @Test
//    public void mainIOExceptionTest() throws NoSuchFieldException, IllegalAccessException {
//        Field f = ServerConstants.class.getDeclaredField("PORT");
//        changeModifier(f);
//        String[] argsInput = {"4545"};
//
//        IllegalStateException thrown =
//                assertThrows(IllegalStateException.class,
//                        () -> {
//                    Prattle.main(argsInput);
//                        },
//                        "Expected method to throw, but it didn't");
//
//    }


    /**
     * Testing the Prattle main class.
     *
     * @throws ClassNotFoundException the class not found exception
     * @throws NoSuchFieldException   the no such field exception
     * @throws IllegalAccessException the illegal access exception
     */
    @Test
    void testPrattleMain() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        Field isReadyField = Class.forName(prattleLoc).getDeclaredField("isReady");
        isReadyField.setAccessible(true);
        isReadyField.set(testPrattle, true);

        try {
            SocketChannel sc = SocketChannel.open();
            sc.configureBlocking(false);
            sc.connect(new InetSocketAddress("localhost", 4545));
            NetworkConnection nc = new NetworkConnection(sc);
            ClientRunnable clientRunnable = new ClientRunnable(nc);
            testPrattle.stopServer();

        } catch (IOException io) {

        }
    }


    /**
     * Test client runnable throwing an IOexception.
     *
     * @throws IOException               the io exception
     * @throws NoSuchMethodException     the no such method exception
     * @throws InvocationTargetException the invocation target exception
     * @throws IllegalAccessException    the illegal access exception
     */
    @Test
    public void testClientRunnableException() throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method readMethod = testPrattle.getClass().getDeclaredMethod("createClientThread",ServerSocketChannel.class, ScheduledExecutorService.class);
        readMethod.setAccessible(true);

        ScheduledExecutorService mockService = mock(ScheduledExecutorService.class);
        ServerSocketChannel socketChannel = ServerSocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.socket().bind(new InetSocketAddress("localhost", 5000));
        socketChannel.socket().close();
        socketChannel.close();
        readMethod.invoke(testPrattle,socketChannel,mockService);

    }


    /**
     * Test prattle main full cover.
     *
     * @throws ClassNotFoundException the class not found exception
     * @throws NoSuchFieldException   the no such field exception
     * @throws IllegalAccessException the illegal access exception
     * @throws IOException            the io exception
     */
    @Test
    public void testPrattleMainFullCover() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, IOException {
        String[] argsInput = {"4545"};

//        testPrattle.main(argsInput);
        Field isReadyField = Class.forName(prattleLoc).getDeclaredField("isReady");
        isReadyField.setAccessible(true);
        isReadyField.set(testPrattle,false);
        ScheduledExecutorService mockService = mock(ScheduledExecutorService.class);
        ServerSocketChannel socketChannel = ServerSocketChannel.open();
        socketChannel.configureBlocking(false);
        Thread thread = new Thread(()->{
            try {
                socketChannel.socket().bind(new InetSocketAddress("localhost", 8989));
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                testPrattle.startServer(socketChannel);
            } catch (IOException e) {
                e.printStackTrace();
            }

        });

        thread.start();

    }

    /**
     * Test retrieve all groups.
     *
     * @throws IOException            the io exception
     * @throws NoSuchFieldException   the no such field exception
     * @throws IllegalAccessException the illegal access exception
     */
    @Test
    public void testRetrieveAllGroups() throws IOException, NoSuchFieldException, IllegalAccessException {
        Prattle p = Prattle.getInstance();

        SocketChannel sc = SocketChannel.open();
        sc.configureBlocking(false);
        sc.connect(new InetSocketAddress("localhost", 4545));
        NetworkConnection nc = new NetworkConnection(sc);
        ClientRunnable clientRunnable = new ClientRunnable(nc);

        ClientRunnable clientRunnable2 = new ClientRunnable(nc);




        Field cli= clientRunnable.getClass().getDeclaredField("initialized");
        cli.setAccessible(true);
        cli.set(clientRunnable,true);
        Field cli2= clientRunnable.getClass().getDeclaredField("name");
        cli2.setAccessible(true);
        cli2.set(clientRunnable,"user");
        Field f= p.getClass().getDeclaredField("active");
        f.setAccessible(true);
        ConcurrentLinkedQueue<ClientRunnable> active = (ConcurrentLinkedQueue<ClientRunnable>)f.get(p);
        active.add(clientRunnable);


        Field cli3= clientRunnable2.getClass().getDeclaredField("initialized");
        cli3.setAccessible(true);
        cli3.set(clientRunnable2,true);
        Field cli4= clientRunnable2.getClass().getDeclaredField("name");
        cli4.setAccessible(true);
        cli4.set(clientRunnable2,"active1");

        active.add(clientRunnable2);
        f.set(p,active);

        Message msgR = Message.makeRegisterMessage("user", "message");
        p.registerClient(msgR);
        Message msgH = Message.makeLoginMessage("user", "message");
        p.loginClient(msgH, clientRunnable);


        Message msgR1 = Message.makeRegisterMessage("user1", "message");
        p.registerClient(msgR1);
        Message msgH1 = Message.makeLoginMessage("user1", "message");
        p.loginClient(msgH1, clientRunnable2);



        Field f4=  p.getClass().getDeclaredField("activeUsers");
        f4.setAccessible(true);
        Map<String,Client> act = (Map<String,Client>)f4.get(p);


        p.retrieveAllPublicGroup(clientRunnable);

        Field f3= ClientRunnable.class.getDeclaredField("waitingList");
        f3.setAccessible(true);
        Queue<Message> h = (Queue<Message>) f3.get(clientRunnable2);
        if(h != null && h.peek() != null){
            assertEquals("SERVER#RAG",h.peek().getMsgSender());
        }




        p.stopServer();
    }

    /**
     * After all.
     *
     * @throws IOException the io exception
     */
    @AfterAll
    public static void afterAll() throws IOException {
        mainThread.interrupt();
    }

}
