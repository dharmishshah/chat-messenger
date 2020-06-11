package edu.northeastern.ccs.im.dataconnections.operations;

import edu.northeastern.ccs.im.dataconnections.models.ChatterClient;
import edu.northeastern.ccs.im.server.models.Client;

/**
 * This interface converts server user and group model to data persistence user and group model.
 */
public interface DataTransferOperation {

    public ChatterClient transferClient(Client client);
}
