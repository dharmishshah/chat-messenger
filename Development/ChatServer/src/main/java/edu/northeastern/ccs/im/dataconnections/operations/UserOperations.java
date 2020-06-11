package edu.northeastern.ccs.im.dataconnections.operations;


import edu.northeastern.ccs.im.dataconnections.models.ChatterClient;
import edu.northeastern.ccs.im.server.models.Client;
import edu.northeastern.ccs.im.server.models.User;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.io.IOException;
import java.util.List;

import edu.northeastern.ccs.im.dataconnections.models.ChatterUser;


/**
 * The type User operations.
 */
public class UserOperations implements DataTransferOperation{

  private Appendable output;
  private DataBaseOperations dataBaseOperation;

  /**
   * Instantiates a new User operations.
   */
  public UserOperations(){
    this.output = new StringBuilder();
    dataBaseOperation = new DataBaseOperations(this.output);
  }

  /**
   * Instantiates a new User operations.
   *
   * @param appendable the appendable
   */
  public UserOperations(Appendable appendable) {
    this.output = appendable;
    dataBaseOperation = new DataBaseOperations(this.output);
  }

  /**
   * Create user object in database.
   *
   * @param chatterUser the chatter user
   * @return true if successful false otherwise
   */
  public boolean createUser (ChatterUser chatterUser) {
    try{
      return dataBaseOperation.createRecord(chatterUser);
    }catch(IOException ioe){
      return false;
    }
  }

  /**
   * Update user object in database.
   *
   * @param chatterUser the chatter user
   * @return true if successful false otherwise
   */
  public boolean updateUser (ChatterUser chatterUser){

    chatterUser = getChatterUserById(chatterUser.getId());
    chatterUser.setName(chatterUser.getName());
    chatterUser.setPassword(chatterUser.getPassword());
    try{
      dataBaseOperation.updateRecord(chatterUser);
    }catch(IOException ioe){
      return false;
    }
    return true;
  }


  /**
   * Get chatter user by id chatter user.
   *
   * @param id the id
   * @return the chatter user
   */
  public ChatterUser getChatterUserById(Integer id){
    Session sessionObj = SessionFactoryConfiguration.getSessionFactory().openSession();
    return (ChatterUser) sessionObj.get(ChatterUser.class, id);
  }

  /**
   * Get chatter user by username chatter user.
   *
   * @param username the username
   * @return the chatter user
   */
  public ChatterUser getChatterUserByUsername(String username){
    Session sessionObj = SessionFactoryConfiguration.getSessionFactory().openSession();
    Criteria criteria = sessionObj.createCriteria(ChatterUser.class);
    ChatterUser yourObject = (ChatterUser) criteria.add(Restrictions.eq("name", username))
            .uniqueResult();
    sessionObj.close();
    return yourObject;
  }


  /**
   * Delete user from database.
   *
   * @param user the user
   */
  public void deleteUser(ChatterUser user) {
    dataBaseOperation.deleteRecord(user);
  }

  /**
   * Gets all users.
   *
   * @return all users
   */
  public List getAllUsers() {
    return dataBaseOperation.getAllRecords("ChatterUser");
  }


  @Override
  public ChatterClient transferClient(Client client) {
    User user = (User)client;
    ChatterUser chatterUser = new ChatterUser();
    chatterUser.setName(user.getName());
    chatterUser.setPassword(user.getPassword());
    chatterUser.setUniqueId(user.getUniqueId());
    return chatterUser;
  }

  /**
   * Get chatter user by username and password chatter user.
   *
   * @param username the username
   * @param password the password
   * @return the chatter user
   */
  public ChatterUser getChatterUserByUsernameAndPassword(String username, String password){
    Session sessionObj = SessionFactoryConfiguration.getSessionFactory().openSession();
    Criteria criteria = sessionObj.createCriteria(ChatterUser.class);
    ChatterUser yourObject = (ChatterUser) criteria.add(Restrictions.eq("name", username))
            .add(Restrictions.eq("password", password))
            .uniqueResult();
    sessionObj.close();
    return yourObject;
  }

}

