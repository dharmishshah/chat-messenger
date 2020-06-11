package edu.northeastern.ccs.im.dataconnections.operations;
import edu.northeastern.ccs.im.ChatLogger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.IOException;
import java.util.List;


/**
 * The type Data base operations.
 */
public class DataBaseOperations {
  private Appendable app;

  /**
   * Instantiates a new Data base operations.
   */
  public DataBaseOperations(){
    StringBuilder op = new StringBuilder();
    this.app = op;

  }

  /**
   * Instantiates a new Data base operations.
   *
   * @param output the output
   */
  public DataBaseOperations(Appendable output) {
    this.app = output;
  }

  /**
   * Create record for the specified object.
   *
   * @param object the object
   * @throws IOException the io exception
   */
  public boolean createRecord(Object object)  throws IOException{
    boolean result = true;
    Session sessionObject = SessionFactoryConfiguration.getSessionFactory().openSession();
    sessionObject.beginTransaction();
    try {
      sessionObject.saveOrUpdate(object);
      this.app.append("Records Saved Successfully To The Database\n");
      // Committing The Transactions To The Database
      sessionObject.getTransaction().commit();
    } catch (RuntimeException sqlException) {
      ChatLogger.error(sqlException.toString());
      if (sessionObject.getTransaction() != null) {
        sessionObject.getTransaction().rollback();
      }
      result = false;
    } finally {
      sessionObject.close();
    }
    return result;
  }

  /**
   * Update record for a specific object.
   *
   * @param object the object
   * @throws IOException the io exception
   */
  public void updateRecord(Object object)  throws IOException{
    Session sessionObject = SessionFactoryConfiguration.getSessionFactory().openSession();
    sessionObject.beginTransaction();
    try {
      sessionObject.saveOrUpdate(object);
      this.app.append("Records Saved Successfully To The Database\n");
      // Committing The Transactions To The Database
      sessionObject.getTransaction().commit();
    } catch (RuntimeException sqlException) {
      if (sessionObject.getTransaction() != null) {
        sessionObject.getTransaction().rollback();
      }
    } finally {
      sessionObject.close();
    }
  }

  /**
   * Delete record from database based on object model.
   *
   * @param object the object
   */
  public void deleteRecord(Object object)  {
    Session sessionObject = SessionFactoryConfiguration.getSessionFactory().openSession();
    //Creating Transaction Object
    Transaction transObj = sessionObject.beginTransaction();
    sessionObject.delete(object);
    try {
      this.app.append("Successfully deleted record\n");
    } catch (IOException e) {
      throw new IllegalArgumentException("Transaction failed"+e.getMessage()+"\n");
    }
    transObj.commit();
    sessionObject.close();
  }

  /**
   * Get all records list from the database.
   *
   * @param objectType the object type
   * @return the list
   */
  public List getAllRecords (String objectType){
    Session sessionObj = SessionFactoryConfiguration.getSessionFactory().openSession();
    List objectList;
    if (objectType.matches("ChatterUser")) {
      objectList = sessionObj.createQuery("FROM ChatterUser").list();
    }
    else {
      objectList = sessionObj.createQuery("From ChatterGroup").list();
    }
    sessionObj.close();
    return objectList;
  }

  /**
   * Delete all records from the database.
   *
   * @param objectType the object type
   */
  public void deleteAllRecords(String objectType) {
    Session sessionObject = SessionFactoryConfiguration.getSessionFactory().openSession();
    Transaction transObj = sessionObject.beginTransaction();
    if (objectType.matches("ChatterUser")) {
      Query queryObj = sessionObject.createQuery("DELETE FROM ChatterUser");
      queryObj.executeUpdate();
    }
    else{
      Query queryObj = sessionObject.createQuery("DELETE FROM ChatterGroup");
      queryObj.executeUpdate();
    }
    // Transaction Is Committed To Database
    transObj.commit();

    // Closing The Session Object
    sessionObject.close();
  }
}
