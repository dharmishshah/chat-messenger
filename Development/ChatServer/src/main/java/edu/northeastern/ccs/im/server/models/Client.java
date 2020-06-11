package edu.northeastern.ccs.im.server.models;

/**
 * The interface Client.
 */
public interface Client {

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name);

    /**
     * Sets password.
     *
     * @param password the password
     */
    public void setPassword(String password);

    /**
     * Gets unique id.
     *
     * @return the unique id
     */
    public String getUniqueId();

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName();

    /**
     * Gets password.
     *
     * @return the password
     */
    public String getPassword();


}
