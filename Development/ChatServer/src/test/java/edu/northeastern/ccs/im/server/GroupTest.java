package edu.northeastern.ccs.im.server;



import edu.northeastern.ccs.im.server.models.Group;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import edu.northeastern.ccs.im.server.models.Client;
import edu.northeastern.ccs.im.server.models.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The type Group test.
 */
public class GroupTest {

    private static Client creator;
    private static Client member;
    private static Group group;

    /**
     * Before all.
     */
    @BeforeAll
    public static void beforeAll()
    {

        creator = new User("unk"," bob","alice");
        member = new User("unk"," charlie","pass");
        group = new Group(creator,"unkgrp","group","password");
    }

    /**
     * Group creation test.
     */
    @Test
    public void groupCreationTest()
    {
        assertEquals("unkgrp",group.getUniqueId());
        assertEquals("group",group.getName());
        assertEquals("password",group.getPassword());
        assertEquals(creator,group.getModerators().get(0));
        assertEquals(creator,group.getMembers().get(0));


    }

    /**
     * Add remove member test.
     */
    @Test
    public void addRemoveMemberTest()
    {
        assertEquals(true,group.addMember(member));
        assertEquals(group.getMembers().size(),2);
        assertEquals(group.getModerators().size(),1);
        assertEquals(true,group.removeMember(member));
        assertEquals(group.getMembers().size(),1);


    }


    /**
     * Sets test.
     */
    @Test
    public void setterTest()
    {
        group.setName("HelloGroup");
        assertEquals("HelloGroup",group.getName());
        group.setPassword("pass123");
        assertEquals("pass123",group.getPassword());
        group.setUniqueId("unk1234");
        assertEquals("unk1234",group.getUniqueId());
    }


    /**
     * Remove non existing member test.
     */
    @Test
    public void removeNonExistingMemberTest()
    {
        assertEquals(false,group.removeMember(member));

    }



}
