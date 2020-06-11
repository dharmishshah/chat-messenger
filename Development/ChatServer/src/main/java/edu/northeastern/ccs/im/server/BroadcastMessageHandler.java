package edu.northeastern.ccs.im.server;

import edu.northeastern.ccs.im.Message;

/**
 * A handler for incoming broadcast messages.
 */
class BroadcastMessageHandler extends ChatMessageHandler {
    BroadcastMessageHandler(Message message) {
        super(message);
    }

    @Override
    void handleMessage(ClientRunnable clientRunnable) {
        Prattle.getInstance().broadcastMessage(msg);
    }
}
