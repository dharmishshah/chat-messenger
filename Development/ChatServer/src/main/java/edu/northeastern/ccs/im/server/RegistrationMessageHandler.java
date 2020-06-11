package edu.northeastern.ccs.im.server;

import edu.northeastern.ccs.im.Message;

/**
 * The type Registration message handler.
 */
public class RegistrationMessageHandler extends ChatMessageHandler {

    /**
     * Instantiates a new Registration message handler.
     *
     * @param msg the msg
     */
    RegistrationMessageHandler(Message msg){super(msg);}
    @Override
    void handleMessage(ClientRunnable clientRunnable) {
        Prattle.getInstance().registerClient(msg);
    }
}
