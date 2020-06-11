package edu.northeastern.ccs.im.persistancetest;

import edu.northeastern.ccs.im.dataconnections.operations.DataBaseOperations;
import edu.northeastern.ccs.im.server.models.User;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

import edu.northeastern.ccs.im.dataconnections.models.ChatterUser;
import edu.northeastern.ccs.im.dataconnections.operations.UserOperations;

public class UserOperationsTest {


  @AfterEach
  public  void afterEach()
  {
    DataBaseOperations dbo = new DataBaseOperations();
    dbo.deleteAllRecords("ChatterUser");
    dbo.deleteAllRecords("ChatterGroup");
  }

    @Test
  public void test1() {
    ChatterUser user1 = new ChatterUser("abc","tom","tom");
    StringBuilder output = new StringBuilder();
    UserOperations userOperations = new UserOperations(output);
    userOperations.createUser(user1);
    assertEquals("Records Saved Successfully To The Database\n",output.toString());
  }

  @Test
  public void test2() {
    ChatterUser user2 = new ChatterUser("abc1","tom1","tom2");
    StringBuilder output =  new StringBuilder();
    UserOperations userOperations = new UserOperations(output);
    userOperations.createUser(user2);
    userOperations.deleteUser(user2);
    assertEquals("Records Saved Successfully To The Database\n" +
            "Successfully deleted record\n",output.toString());
  }

  @Test
  public void test3() {
    ChatterUser generalUser = new ChatterUser("general","general1","general2");
    StringBuilder output = new StringBuilder();
    UserOperations userOperations1 = new UserOperations(output);
    userOperations1.createUser(generalUser);
    StringBuilder output1 = new StringBuilder();
    UserOperations user = new UserOperations(output1);
    generalUser.setName("Jeff");
    user.updateUser(generalUser);
    assertEquals("Records Saved Successfully To The Database\n",output1.toString());
  }

  @Test
  public void test4() {
      StringBuilder output = new StringBuilder();
      UserOperations userOperations = new UserOperations(output);
      userOperations.getAllUsers();
      assertEquals(output.toString(),output.toString());
  }

  @Test
  public void testConvertUser()
  {
      StringBuilder output = new StringBuilder();

      User user = new User("123","HELLO","world");
      UserOperations userOperations = new UserOperations(output);

      ChatterUser cu = (ChatterUser)userOperations.transferClient(user);
      assertEquals(cu.getName(),"HELLO");
      assertEquals(cu.getPassword(),"world");
      assertEquals(cu.getUniqueId(),"123");

  }

  @Test
  public void testGetByUsername(){
    StringBuilder output = new StringBuilder();
    ChatterUser user2 = new ChatterUser("12132","uniqueName","tom24332");
    UserOperations userOperations = new UserOperations(output);
    userOperations.createUser(user2);
    ChatterUser cu = userOperations.getChatterUserByUsername("uniqueName");
    assertEquals("uniqueName",cu.getName());
  }

  @Test
  public void testIOExceptionFailure(){
    ChatterUser user1 = new ChatterUser("abc","tom","tom");
    StringBuilder output = new StringBuilder();

    String data = " This is new content";
    File file = new File("givem.txt");

    try{
      FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
      BufferedWriter bw = new BufferedWriter(fw);
      UserOperations userOperations = new UserOperations(bw);
      bw.close();
      boolean result = userOperations.createUser(user1);
      assertEquals(false,result);
    }catch(IOException ioe) {

    }

  }

  @Test
  public void testIOExceptionFailureDelete(){
    ChatterUser user1 = new ChatterUser("abKKKKKc","tYTTRTRom","toGGBGm");
    StringBuilder output = new StringBuilder();

    String data = " This is new content";
    File file = new File("givem.txt");

    try{
      FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
      BufferedWriter bw = new BufferedWriter(fw);
      UserOperations userOperations = new UserOperations(bw);
      boolean result = userOperations.createUser(user1);
      userOperations.createUser(user1);
      bw.close();
      try{
        userOperations.deleteUser(user1);
      }catch(IllegalArgumentException iae){
        assertEquals("Transaction failedStream closed\n",iae.getMessage());
      }
    }catch(IOException ioe) {

    }

  }

  @Test
  public void testFailureUpdate(){

    ChatterUser user1 = new ChatterUser("abc3234","t1232om","tom2232");
    StringBuilder output = new StringBuilder();

    String data = " This is new content";
    File file = new File("givem.txt");

    try{
      FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
      BufferedWriter bw = new BufferedWriter(fw);
      UserOperations userOperations = new UserOperations(bw);
      userOperations.createUser(user1);
      StringBuilder output1 = new StringBuilder();
      UserOperations user = new UserOperations(output1);
      user1.setName("Jef3EEEf");
      bw.close();
        boolean result = userOperations.updateUser(user1);
        assertEquals(false, result);

    }catch(IOException ioe) {

    }
  }


  @Test
  public void testUsernameAndPassword(){
    ChatterUser user1 = new ChatterUser("userTestUniqueId","userTest","userPassword");
    UserOperations userOperations = new UserOperations();
    userOperations.createUser(user1);
    ChatterUser retrievedUser = userOperations.getChatterUserByUsernameAndPassword("userTest",
            "userPassword");
    assertEquals("userTest",retrievedUser.getName());
    ChatterUser retrievedUserInc = userOperations.getChatterUserByUsernameAndPassword(
            "userTestIncorrect",
            "userPassword");
    assertEquals(null,retrievedUserInc);
  }

 }
