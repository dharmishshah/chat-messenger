package edu.northeastern.ccs.im.persistancetest;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import edu.northeastern.ccs.im.dataconnections.models.ChatterGroup;
import edu.northeastern.ccs.im.dataconnections.models.ChatterUser;
import edu.northeastern.ccs.im.dataconnections.operations.DataBaseOperations;
import edu.northeastern.ccs.im.dataconnections.operations.GroupOperations;
import edu.northeastern.ccs.im.dataconnections.operations.UserOperations;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GroupTestAdmin {

  @BeforeAll
  public  static void beforeAll()
  {
    DataBaseOperations dbo = new DataBaseOperations();
    dbo.deleteAllRecords("ChatterUser");
    dbo.deleteAllRecords("ChatterGroup");

    UserOperations userOperations = new UserOperations();
    GroupOperations groupOperations = new GroupOperations();

    ChatterUser adminUser = new ChatterUser("adminUserId","adminUser","userPassword");
    userOperations.createUser(adminUser);
    ChatterGroup group1 = new ChatterGroup(adminUser,"adminGroupId","adminGroup","groupPassword");

    groupOperations.createGroup(group1);
  }

  @Test
  public void testChatterGroupForAdmin(){
    GroupOperations groupOperations = new GroupOperations();
    assertEquals(true,groupOperations.getChatterGroupByAdminUsername("adminGroup","adminUser"));
    assertEquals(false,groupOperations.getChatterGroupByAdminUsername("adminGroup","adminUser12"));
  }
}
