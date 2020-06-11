package edu.northeastern.ccs.im.dataconnections.models;

import javax.persistence.Entity;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * The type Chatter user.
 */
@Entity
@Table(name="chatter_user",uniqueConstraints = {@UniqueConstraint(columnNames={"user_id"})})
public class ChatterUser implements ChatterClient {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "user_id")
  private Integer userId;

  @Column(name = "name", length = 45,nullable = false,unique = true)
  private String name;

  @Column(name = "password", length = 45,nullable = false)
  private String password;

  @Column(name = "unique_id", length = 45,nullable = false,unique = true)
  private String uniqueId;

  /**
   * Instantiates a new Chatter user.
   */
  public ChatterUser() {}

  /**
   * Instantiates a new Chatter user.
   *
   * @param uniqueId the unique id
   * @param name     the name
   * @param password the password
   */
  public ChatterUser(String uniqueId, String name, String password){
    this.uniqueId = uniqueId;
    this.name = name;
    this.password = password;
  }

  @Override
  public String getUniqueId() {
    return uniqueId;
  }


  /**
   * Sets unique id.
   *
   * @param uniqueId the unique id
   */
  public void setUniqueId(String uniqueId) {
    this.uniqueId = uniqueId;
  }
  /**
   * Gets user name.
   *
   * @return name user name
   */
  @Override
  public String getName() {
    return name;
  }

  /**
   * Sets user name.
   *
   * @param name user name
   */
  @Override
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets password.
   *
   * @return password user password
   */
  @Override
  public String getPassword() {
    return password;
  }

  /**
   * Sets password.
   *
   * @param password user password
   */
  @Override
  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * Gets unique id.
   *
   * @return userId the user id
   */
  public Integer getId() {
    return this.userId;
  }

}
