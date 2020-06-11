package edu.northeastern.ccs.im.server;

import edu.northeastern.ccs.im.Message;

/**
 * The type Unicast message handler.
 */
public class UnicastMessageHandler extends ChatMessageHandler {

    /**
     * Instantiates a new Unicast message handler.
     *
     * @param msg the msg
     */
    public UnicastMessageHandler(Message msg){
        super(msg);
    }
    @Override
    void handleMessage(ClientRunnable clientRunnable) {
        Prattle.getInstance().unicastMessage(msg,clientRunnable);
    }
}
