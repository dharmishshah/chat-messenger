package edu.northeastern.ccs.im.dataconnections.operations;

import edu.northeastern.ccs.im.ChatLogger;
import edu.northeastern.ccs.im.Message;
import edu.northeastern.ccs.im.dataconnections.models.Status;
import edu.northeastern.ccs.im.messages.MessageTypes;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.ccs.im.dataconnections.models.ChatterMessage;


/**
 * The type Message operations.
 */
public class MessageOperations {

  private UserOperations userOperations;
  private GroupOperations groupOperations;

  /**
   * Instantiates a new Message operations.
   */
  public MessageOperations()
  {
    this.userOperations = new UserOperations();
    this.groupOperations = new GroupOperations();
  }

  /**
   * Create message boolean.
   *
   * @param message the message
   * @return true if successful false otherwise
   */
  public boolean createMessage(ChatterMessage message)  {
    Session sessionObject = SessionFactoryConfiguration.getSessionFactory().openSession();
    boolean created = false;
    sessionObject.beginTransaction();
    try {
      sessionObject.save(message);
      created= true;
      sessionObject.getTransaction().commit();
    } catch (RuntimeException sqlException) {
      ChatLogger.error(sqlException.toString());
      if (sessionObject.getTransaction() != null) {
        sessionObject.getTransaction().rollback();
      }
      created =false;
    } finally {
      sessionObject.close();
    }
    return created;
  }


  /**
   * Gets all unread messages.
   *
   * @param receiverName the receiver name
   * @return the all unread messages
   */
  public List getAllUnreadMessages(String receiverName) {
    Session sessionObj = SessionFactoryConfiguration.getSessionFactory().openSession();
    List<List>unreadMessages = new ArrayList<>();
    Query query = sessionObj.createSQLQuery("SELECT cm.* FROM message as cm " +
            "INNER JOIN chatter_user cu on " + "cu.user_id=cm.message_receiver " +
            "where cu.name = :receiverName and cm.status = :status");
    query.setParameter("receiverName", receiverName);
    query.setParameter("status",0);
    unreadMessages.addAll(query.list());
    sessionObj.close();

    return unreadMessages;
  }


  /**
   * The following method updates the status of the message.
   *
   * @param message message to be updated.
   * @return true if operation was successful false otherwise
   */
  public boolean updateMessage(ChatterMessage message) {
    Session sessionObj = SessionFactoryConfiguration.getSessionFactory().openSession();
    Transaction transObj = sessionObj.beginTransaction();
    try{

      sessionObj.saveOrUpdate(message);
      transObj.commit();
    }catch(RuntimeException e){
      transObj.rollback();
      return false;
    } finally {
    sessionObj.close();
  }
    return true;
  }

  /**
   * The following method retrieves messages received by a user.
   *
   * @param idList the id of the receiver.
   * @return the message list
   */
  public List retrieveMessagesForUser(List<Integer>idList) {
    Session sessionObject = SessionFactoryConfiguration.getSessionFactory().openSession();
    List<List>messages = new ArrayList<>();
    for (Integer id:idList) {
      String hql = "SELECT messageContent FROM ChatterMessage where messageReceiver = :id";
      Query query = sessionObject.createQuery(hql);
      query.setParameter("id", id);
      messages.addAll(query.list());
    }
    sessionObject.close();
    return  messages;
  }


  /**
   * Transfer message chatter message.
   *
   * @param message the message
   * @param type    the type
   * @return the chatter message
   */
  public ChatterMessage transferMessage(Message message,String type)
  {
     ChatterMessage chatterMessage = new ChatterMessage();
     chatterMessage.setMessageSender(userOperations.getChatterUserByUsername(message.getMsgSender()).getId());
     if(type.equals(MessageTypes.MULTICAST))
     {
       chatterMessage.setMessageReceiver(groupOperations.getChatterGroupByUsername(message.getMsgReceiver()).getId());
     }
     else {
       chatterMessage.setMessageReceiver(userOperations.getChatterUserByUsername(message.getMsgReceiver()).getId());
     }
     chatterMessage.setGroupMessage(type.equals(MessageTypes.MULTICAST));
     chatterMessage.setMessageContent(message.getText());
     chatterMessage.setMessageType(message.getMsgType().getAbbreviation());
     chatterMessage.setMessageStatus(Status.UNREAD);
     return chatterMessage;

  }



}
