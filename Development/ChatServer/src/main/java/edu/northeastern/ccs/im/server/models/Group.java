package edu.northeastern.ccs.im.server.models;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Group.
 */
public class Group implements Client {

    private String name;
    private String password;
    private String uniqueId;
    private List<Client> members;
    private List<Client> moderators;

    /**
     * Instantiates a new Group.
     *
     * @param creator  the creator of the group
     * @param uniqueId the unique id
     * @param name     the name
     * @param password the password
     */
    public Group(Client creator,String uniqueId,String name,String password)
    {
        this.name = name;
        this.uniqueId = uniqueId;
        this.password = password;
        this.members = new ArrayList<>();
        this.moderators = new ArrayList<>();
        moderators.add(creator);
        members.add(creator);

    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getUniqueId() {
        return uniqueId;
    }

    /**
     * Sets unique id.
     *
     * @param uniqueId the unique id
     */
    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }


    /**
     * Gets members.
     *
     * @return the members
     */
    public List<Client> getMembers() {
        return members;
    }



    /**
     * Gets moderators.
     *
     * @return the moderators
     */
    public List<Client> getModerators() {
        return moderators;
    }


    /**
     * Add member boolean.
     *
     * @param client the client
     * @return the boolean
     */
    public boolean addMember(Client client)
    {
        return this.members.add(client);
    }


    /**
     * Remove member boolean.
     *
     * @param client the client
     * @return the boolean
     */
    public boolean removeMember(Client client)
    {
        if(members.contains(client))
        {
            return members.remove(client);
        }
        else
        {
            return false;
        }
    }


}
