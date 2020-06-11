package edu.northeastern.ccs.im.dataconnections.operations;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

/**
 * The type Session factory configuration.
 */
public class SessionFactoryConfiguration {

  private SessionFactoryConfiguration() {
  }

  private static SessionFactory sessionFactoryObj;

  private static SessionFactory buildSessionFactory() {


    // Creating Configuration Instance & Passing Hibernate Configuration File
    Configuration configObj = new Configuration();
    configObj.configure("hibernate.cfg.xml");
    ServiceRegistry serviceRegistryObj = new StandardServiceRegistryBuilder().applySettings(configObj.getProperties()).build();

    // Creating Hibernate SessionFactory Instance
    sessionFactoryObj = configObj.buildSessionFactory(serviceRegistryObj);
    return sessionFactoryObj;
  }


  /**
   * Get singleton session factory instance.
   *
   * @return the session factory
   */
  public static SessionFactory getSessionFactory() {
    if (sessionFactoryObj == null)
      sessionFactoryObj = buildSessionFactory();
    return sessionFactoryObj;
  }
}
