package edu.northeastern.ccs.im.server;

import static org.junit.jupiter.api.Assertions.*;

import edu.northeastern.ccs.im.Message;
import org.junit.jupiter.api.Test;

/**
 * The type Incoming message handler test.
 */
public class IncomingMessageHandlerTest {

    /**
     * Test that invalid MessageTypes are not accepted.
     */
    @Test
    public void testChooseWithInvalidMessageType() {
        Message helloMsg = Message.makeSimpleLoginMessage("user");
        assertThrows(IllegalArgumentException.class, () -> {
            IncomingMessageHandler.choose(helloMsg);
        });
    }

    /**
     * Test that Broadcast messages return a BroadcastMessageHandler.
     */
    @Test
    public void testChooseWithBroadcastMessage() {
        Message broadcastMsg = Message.makeBroadcastMessage("user", "message");

        IncomingMessageHandler handler = IncomingMessageHandler.choose(broadcastMsg);

        assertTrue(handler instanceof BroadcastMessageHandler);
    }

    /**
     * Test that Quit messages return a QuitMessageHandler.
     */
    @Test
    public void testChooseWithQuitMessage() {
        Message quitMsg = Message.makeQuitMessage("user");

        IncomingMessageHandler handler = IncomingMessageHandler.choose(quitMsg);

        assertTrue(handler instanceof QuitMessageHandler);
    }


    /**
     * Test choose with register message.
     */
    @Test
    void testChooseWithRegisterMessage(){
        Message registerMessage = Message.makeRegisterMessage("John","qwerty");
        IncomingMessageHandler handler = IncomingMessageHandler.choose(registerMessage);
        assertTrue(handler instanceof RegistrationMessageHandler);
    }


    /**
     * Test choose with login message.
     */
    @Test
    void testChooseWithLoginMessage(){
        Message loginMessage = Message.makeLoginMessage("Tom","bad");
        IncomingMessageHandler handler = IncomingMessageHandler.choose(loginMessage);
        assertTrue(handler instanceof LoginMessageHandler);
    }

    /**
     * Test choose with unicast message.
     */
    @Test
    void testChooseWithUnicastMessage(){
        Message unicastMessage = Message.makeUniCastMessage("Sample","server");
        IncomingMessageHandler handler = IncomingMessageHandler.choose(unicastMessage);
        assertTrue(handler instanceof UnicastMessageHandler);
    }

    /**
     * Test choose with multicast message.
     */
    @Test
    void testChooseWithMulticastMessage(){
        Message multicastMessage = Message.makeMultiCastMessage("Hi", "Group 1");
        IncomingMessageHandler handler = IncomingMessageHandler.choose(multicastMessage);
        assertTrue(handler instanceof MulticastMessageHandler);

    }

    /**
     * Test choose with command message.
     */
    @Test
    void testChooseWithCommandMessage(){
        String[] fol = new String[2];
        fol[0] = "Admin";
        fol[1] = "User";
        Message addMem = Message.makeCommandMessage("ADM","group5",fol);
        IncomingMessageHandler handler = IncomingMessageHandler.choose(addMem);
        assertTrue(handler instanceof CommandMessageHandler);

    }
}
