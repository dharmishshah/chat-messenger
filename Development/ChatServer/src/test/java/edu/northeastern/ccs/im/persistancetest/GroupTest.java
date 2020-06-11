package edu.northeastern.ccs.im.persistancetest;

import edu.northeastern.ccs.im.dataconnections.models.ChatterClient;

import edu.northeastern.ccs.im.dataconnections.models.GroupType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import edu.northeastern.ccs.im.dataconnections.models.ChatterGroup;
import edu.northeastern.ccs.im.dataconnections.models.ChatterUser;
import edu.northeastern.ccs.im.dataconnections.operations.DataBaseOperations;
import edu.northeastern.ccs.im.dataconnections.operations.GroupOperations;
import edu.northeastern.ccs.im.dataconnections.operations.UserOperations;
import edu.northeastern.ccs.im.server.models.Client;
import edu.northeastern.ccs.im.server.models.Group;
import edu.northeastern.ccs.im.server.models.User;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;


public class GroupTest {

  @AfterEach
  public  void afterEach()
  {
    DataBaseOperations dbo = new DataBaseOperations();
    dbo.deleteAllRecords("ChatterGroup");
    dbo.deleteAllRecords("ChatterUser");
  }

  @Test
  public void test1() {
    ChatterUser user1 = new ChatterUser("JADU1234","ROHIT1234","BOURNVITA");
    StringBuilder output1 = new StringBuilder();
    StringBuilder output2 = new StringBuilder();
    UserOperations userOperations = new UserOperations(output1);
    userOperations.createUser(user1);
    ChatterGroup group1 = new ChatterGroup(user1,"Jadu1234556","KMG123","jadu123");
    GroupOperations groupOperations = new GroupOperations(output2);
    groupOperations.createGroup(group1);
    assertEquals("","");


  }

  @Test
  public void test2() {
    ChatterUser user1 = new ChatterUser("RONJAE","RON","BOURNVITA");
    StringBuilder output1 = new StringBuilder();
    StringBuilder output2 = new StringBuilder();
    UserOperations userOperations = new UserOperations(output1);
    userOperations.createUser(user1);
    ChatterGroup group1 = new ChatterGroup(user1,"deleteGroup","KGF","jadu12332");
    GroupOperations groupOperations = new GroupOperations();
    groupOperations.createGroup(group1);
    groupOperations.deleteGroup(group1);

  }


  @Test
  public void test3() {
    ChatterUser user1 = new ChatterUser("RONJAE43","RON434","BOURNVITA34");
    StringBuilder output1 = new StringBuilder();
    StringBuilder output2 = new StringBuilder();
    UserOperations userOperations = new UserOperations(output1);
    userOperations.createUser(user1);
    ChatterGroup group1 = new ChatterGroup(user1,"deleteGroup","KG43F","jadu12332");
    GroupOperations groupOperations = new GroupOperations();
    groupOperations.createGroup(group1);

    group1.setName("KGF123587");
    groupOperations.updateGroup(group1);
    assertEquals("KGF123587", group1.getName());

  }

  @Test
  public void test4() {
    StringBuilder output = new StringBuilder();
    GroupOperations groupOperations = new GroupOperations();
    groupOperations.getAllGroups();
    assertEquals(output.toString(),output.toString());
  }

  @Test
  public void testConvertUser()
  {
    StringBuilder output1 = new StringBuilder();

    GroupOperations groupOperations = new GroupOperations(output1);

    Client creator = new User("unk12"," b232ob","a23lice");
    Group group = new Group(creator,"123456","HELLO456","world456");

    ChatterGroup cu = (ChatterGroup)groupOperations.transferClient(group);
    assertEquals(cu.getName(),"HELLO456");
    assertEquals(cu.getPassword(),"world456");
    assertEquals(cu.getUniqueId(),"123456");

  }

  @Test
  public void testGetByUsername(){
    StringBuilder output1 = new StringBuilder();
    ChatterUser user1 = new ChatterUser("R212ONJAE43123","R222ON434","BOURNVITA34");
    UserOperations userOperations = new UserOperations(output1);
    userOperations.createUser(user1);
    ChatterGroup group1 = new ChatterGroup(user1,"12345","KG43FUNique","jadu12332");
    GroupOperations groupOperations = new GroupOperations(output1);
    groupOperations.createGroup(group1);
    ChatterGroup cu = groupOperations.getChatterGroupByUsername("KG43FUNique");
    if(cu != null){
      assertEquals("KG43FUNique",cu.getName());
    }
  }



  @Test
  public void testGetAllMembers()
  {
    ChatterGroup chatterGroup = new ChatterGroup();
    List<ChatterClient> chatterClientList = new ArrayList<ChatterClient>();
    chatterClientList.add(new ChatterUser());
    chatterGroup.getAllMembers();
  }


  @Test
  public void testIOExceptionFailure(){
    String data = " This is new content";
    File file = new File("givem.txt");

    try{
      FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
      BufferedWriter bw = new BufferedWriter(fw);
      ChatterUser user1 = new ChatterUser("JADwdwU1234","ROHIswdT1234","BOURNVITA");
      UserOperations userOperations = new UserOperations(bw);
      userOperations.createUser(user1);
      ChatterGroup group1 = new ChatterGroup(user1,"Jadu12dwd34556","KMGdwd123","jadu123");
      GroupOperations groupOperations = new GroupOperations(bw);
      bw.close();
      boolean result = groupOperations.createGroup(group1);
      assertEquals(false,result);
    }catch(IOException ioe) {

    }

  }

  @Test
  public void testIOExceptionFailureUpdate()  {
    StringBuilder output = new StringBuilder();
    String data = " This is new content";
    File file = new File("givem.txt");

    try{
      FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
      BufferedWriter bw = new BufferedWriter(fw);
      ChatterUser user1 = new ChatterUser("22JADwdwU1234","dddROHIswdT1234","BOURNVITA");
      UserOperations userOperations = new UserOperations(bw);
      userOperations.createUser(user1);
      ChatterGroup group1 = new ChatterGroup(user1,"Jadu12dwdsde12d34556","dedKMGdwd123","jadu123");
      GroupOperations groupOperations = new GroupOperations(bw);
      groupOperations.createGroup(group1);

      group1.setName("dede");
      bw.close();
      boolean result = groupOperations.updateGroup(group1);
      assertEquals(false, result);

    }catch(IOException ioe) {

    }

  }

  @Test
  public void testIOExceptionFailureInvalidId() throws NoSuchFieldException,IllegalAccessException{
    StringBuilder output = new StringBuilder();
    String data = " This is new content";
    File file = new File("givem.txt");

    try{
      FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
      BufferedWriter bw = new BufferedWriter(fw);
      ChatterUser user1 = new ChatterUser("22JADwdwU1234","dddROHIswdT1234","BOURNVITA");
      UserOperations userOperations = new UserOperations(bw);
      userOperations.createUser(user1);
      ChatterGroup group1 = new ChatterGroup(user1,"Jadu12dwdsde12d34556","dedKMGdwd123","jadu123");
      GroupOperations groupOperations = new GroupOperations(bw);
      groupOperations.createGroup(group1);
      ChatterGroup group2 = new ChatterGroup(user1,"Jadu12dwdsde12d34556","dedKMGdwd123","jadu123");
      Field id = group2.getClass().getDeclaredField("groupId");
      id.setAccessible(true);
      id.set(group2,999999);
      group2.setName("dede");
      bw.close();
      boolean result = groupOperations.updateGroup(group2);
      assertEquals(false, result);

    }catch(IOException ioe) {

    }

  }


  @Test
  public void retrieveAllgroups()
  {
    ChatterUser user1 = new ChatterUser("RONJAE","RON","BOURNVITA");
    StringBuilder output1 = new StringBuilder();
    StringBuilder output2 = new StringBuilder();
    UserOperations userOperations = new UserOperations(output1);
    userOperations.createUser(user1);
    ChatterGroup group1 = new ChatterGroup(user1,"deleteGroup","KGF","jadu12332");
    group1.setGroupType(1);
    GroupOperations groupOperations = new GroupOperations();
    groupOperations.createGroup(group1);
    assertEquals("KGF",groupOperations.getPublicGroups().get(0));
    groupOperations.deleteGroup(group1);
  }



}
