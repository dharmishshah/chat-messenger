package edu.northeastern.ccs.im.server;

import org.junit.jupiter.api.Test;

import edu.northeastern.ccs.im.server.models.Client;
import edu.northeastern.ccs.im.server.models.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The type User test.
 */
public class UserTest {

  /**
   * Test user creation.
   */
  @Test
  public void testUserCreation(){

    Client client = new User("user1","bob","mypassword");
    assertEquals("user1",client.getUniqueId());
    assertEquals("bob",client.getName());
    assertEquals("mypassword",client.getPassword());
  }

  /**
   * Test user updation.
   */
  @Test
  public void testUserUpdation(){
    Client client = new User("user1","bob","mypassword");
    assertEquals("user1",client.getUniqueId());
    assertEquals("bob",client.getName());
    assertEquals("mypassword",client.getPassword());

    client.setName("changedUser");
    client.setPassword("changedPassword");
    assertEquals("user1",client.getUniqueId());
    assertEquals("changedUser",client.getName());
    assertEquals("changedPassword",client.getPassword());
  }
}
