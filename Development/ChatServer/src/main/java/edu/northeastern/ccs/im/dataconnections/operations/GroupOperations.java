package edu.northeastern.ccs.im.dataconnections.operations;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.northeastern.ccs.im.ChatLogger;
import edu.northeastern.ccs.im.dataconnections.models.ChatterClient;
import edu.northeastern.ccs.im.dataconnections.models.ChatterGroup;
import edu.northeastern.ccs.im.dataconnections.models.ChatterUser;
import edu.northeastern.ccs.im.server.models.Client;
import edu.northeastern.ccs.im.server.models.Group;
import edu.northeastern.ccs.im.server.models.User;


/**
 * The type Group operations.
 */
public class GroupOperations implements DataTransferOperation {

  private Appendable output;
  private DataBaseOperations dataBaseOperation;
  private UserOperations userOperations;

  /**
   * Instantiates a new Group operations.
   */
  public GroupOperations() {
    this.output = new StringBuilder();
    dataBaseOperation = new DataBaseOperations(this.output);
    userOperations = new UserOperations();
  }

  /**
   * Instantiates a new Group operations.
   *
   * @param appendable the appendable
   */
  public GroupOperations(Appendable appendable) {
    this.output = appendable;
    dataBaseOperation = new DataBaseOperations(this.output);
    userOperations = new UserOperations();
  }

  /**
   * Create group object to persist on database.
   *
   * @param chatterGroup the chatter group
   * @return true if successful false otherwise
   */
  public boolean createGroup(ChatterGroup chatterGroup) {
    try {
      return dataBaseOperation.createRecord(chatterGroup);
    } catch (IOException ioe) {
      return false;
    }
  }

  /**
   * Add member to a group.
   *
   * @param chatterGroup the chatter group
   * @return true if successful false otherwise
   */
  public boolean addMemberToGroup(ChatterGroup chatterGroup) {
    try {
      dataBaseOperation.updateRecord(chatterGroup);
      return true;
    } catch (IOException ioe) {

      return false;
    }
  }

  /**
   * Update group in the database.
   *
   * @param chatterGroup the chatter group
   * @return true if successful false otherwise
   */
  public boolean updateGroup(ChatterGroup chatterGroup) {

    chatterGroup = getChatterGroupById(chatterGroup.getId());
    if (chatterGroup == null) {
      return false;
    }
    chatterGroup.setName(chatterGroup.getName());
    chatterGroup.setPassword(chatterGroup.getPassword());
    try {
      dataBaseOperation.updateRecord(chatterGroup);
      return true;
    } catch (IOException ioe) {
      return false;
    }
  }

  /**
   * Get chatter group by id.
   *
   * @param id the id
   * @return chatter group
   */
  public ChatterGroup getChatterGroupById(Integer id) {
    Session sessionObj = SessionFactoryConfiguration.getSessionFactory().openSession();
    ChatterGroup newUser = (ChatterGroup) sessionObj.get(ChatterGroup.class, id);
    sessionObj.close();
    return newUser;
  }

  /**
   * Gets public groups.
   *
   * @return the public groups
   */
  public List<String> getPublicGroups() {
    Session sessionObj = SessionFactoryConfiguration.getSessionFactory().openSession();
    List<String>listOfPublicGroups = new ArrayList<String>();
    SQLQuery query = sessionObj.createSQLQuery("Select name from chatter_group where " +
            "group_type = :groupType");
    query.setParameter("groupType", 1);
    listOfPublicGroups = query.list();
    sessionObj.close();
    return listOfPublicGroups;

  }

  /**
   * Get chatter group by username.
   *
   * @param username the username
   * @return chatter group
   */
  public ChatterGroup getChatterGroupByUsername(String username) {
    Session sessionObj = SessionFactoryConfiguration.getSessionFactory().openSession();
    Criteria criteria = sessionObj.createCriteria(ChatterGroup.class);
    try {
      ChatterGroup yourObject = (ChatterGroup) criteria.add(Restrictions.eq("name", username))
              .uniqueResult();
      sessionObj.close();
      return yourObject;

    } catch (Exception e) {
      ChatLogger.error(e.toString());
    }

    return null;
  }

  /**
   * Get chatter group by admin username boolean.
   *
   * @param groupname the groupname
   * @param username  the username
   * @return the boolean
   */
  public boolean getChatterGroupByAdminUsername(String groupname, String username){
    Session sessionObj = SessionFactoryConfiguration.getSessionFactory().openSession();
    SQLQuery query = sessionObj.createSQLQuery("select cg.* from \n" +
            "chatter_group cg JOIN group_moderators gm ON cg.group_id = gm.group_id, \n" +
            "chatter_user cu JOIN group_moderators gm1 ON cu.user_id = gm1.group_id\n" +
            "where cg.name = :groupname and cu.name = :username");
    query.setParameter("groupname",groupname);
    query.setParameter("username",username);
    List<Object> groups = query.list();
    sessionObj.close();
    return groups != null && !groups.isEmpty();
  }

  /**
   * Delete group from database.
   *
   * @param group the group
   */
  public void deleteGroup(ChatterGroup group) {
    dataBaseOperation.deleteRecord(group);
  }

  /**
   * Gets all groups.
   *
   * @return all groups
   */
  public List getAllGroups() {
    return dataBaseOperation.getAllRecords("ChatterGroup");
  }

  @Override
  public ChatterClient transferClient(Client client) {

    Group group = (Group) client;
    ChatterGroup chatterGroup = new ChatterGroup();
    String groupName = group.getName();
    chatterGroup.setName(groupName);
    chatterGroup.setPassword(group.getPassword());
    chatterGroup.setUniqueId(group.getUniqueId());
    List<ChatterClient> chatterClients = new ArrayList<>();
    for (Client c : group.getMembers()) {
      if (c.getClass().equals(User.class)) {
        chatterClients.add(userOperations.transferClient(c));
      } else if (c.getClass().equals(Group.class)) {
        chatterClients.add(transferClient(c));
      }
    }
    chatterGroup.setMembers(chatterClients);

    List<ChatterClient> chatterMods = new ArrayList<>();
    for (Client c : group.getModerators()) {
      if (c.getClass().equals(User.class)) {
        chatterMods.add(userOperations.transferClient(c));
      } else if (c.getClass().equals(Group.class)) {
        chatterMods.add(transferClient(c));
      }
    }
    chatterGroup.setModerators(chatterMods);


    return chatterGroup;
  }


  /**
   * Transfer group chatter client.
   *
   * @param group the group
   * @return the chatter client
   */
  public ChatterClient transferGroup(Group group) {

    ChatterGroup chatterGroup = new ChatterGroup();
    chatterGroup.setName(group.getName());
    chatterGroup.setPassword(group.getPassword());
    chatterGroup.setUniqueId(group.getUniqueId());
    List<ChatterClient> chatterClients = new ArrayList<>();
    Map<String, ChatterUser> users = new HashMap<>();
    for (Client c : group.getMembers()) {
      transferAbstract(chatterClients, users, c);
    }
    chatterGroup.setMembers(chatterClients);

    List<ChatterClient> chatterMods = new ArrayList<>();
    for (Client c : group.getModerators()) {
      transferAbstract(chatterMods, users, c);
    }
    chatterGroup.setModerators(chatterMods);


    return chatterGroup;
  }

  /**
   * Helper method for transferGroup
   * @param chatterClients a list of all the chatter clients in process
   * @param users a list of all users in map format
   * @param c the client runnable to use
   */
  private void transferAbstract(List<ChatterClient> chatterClients, Map<String, ChatterUser> users, Client c) {
    if (c.getClass().equals(User.class)) {
      if (users.get(c.getName()) == null) {
        ChatterUser user = userOperations.getChatterUserByUsername(c.getName());
        chatterClients.add(user);
        users.put(c.getName(), user);
      } else {
        chatterClients.add(users.get(c.getName()));
      }
    } else if (c.getClass().equals(Group.class)) {
      chatterClients.add(transferClient(c));
    }
  }
}
