package edu.northeastern.ccs.im.server.models;

/**
 * The type User.
 */
public class User implements Client {

  private String uniqueId;

  private String name;

  private String password;

  /**
   * Instantiates a new User.
   *
   * @param uniqueId the unique id
   * @param name     the name
   * @param password the password
   */
  public User(String uniqueId, String name, String password){
    this.uniqueId = uniqueId;
    this.name = name;
    this.password = password;
  }

  @Override
  public String getUniqueId() {
    return uniqueId;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public void setPassword(String password) {
    this.password = password;
  }
}
