package edu.northeastern.ccs.im.server;

import edu.northeastern.ccs.im.Message;

/**
 * A handler for incoming quit messages.
 */
public class QuitMessageHandler extends IncomingMessageHandler {

    QuitMessageHandler(Message message) {
        super(message);
    }

    @Override
    public void run(ClientRunnable clientRunnable) {
        // Stop sending the poor client message.
        clientRunnable.terminate();
        // Reply with a quit message.
        clientRunnable.enqueueMessage(Message.makeQuitMessage(clientRunnable.getName()));
    }
}
