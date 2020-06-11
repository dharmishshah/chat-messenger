package edu.northeastern.ccs.im.dataconnections.models;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.ManyToAny;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

/**
 * The type Chatter group.
 */
@Entity
@Table(name = "chatter_group",uniqueConstraints = {@UniqueConstraint(columnNames={"group_id"})})
public class ChatterGroup implements ChatterClient{
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "group_id")
  private Integer groupId;

  @Column(name = "unique_id")
  private String uniqueId;

  @Column(name = "name")
  private String name;

  @Column(name = "password")
  private String password;

  @Column(name = "group_type")
  private int groupType;

  @LazyCollection(LazyCollectionOption.FALSE)
  @ManyToMany(targetEntity = ChatterUser.class, cascade = CascadeType.ALL)
  @JoinTable(
          name = "group_users",
          joinColumns = @JoinColumn(name = "group_id"),
          inverseJoinColumns = @JoinColumn(name = "user_id")
  )
  private List<ChatterClient> members;

  @ManyToMany(targetEntity = ChatterUser.class, cascade = CascadeType.ALL,fetch = FetchType.EAGER)
  @JoinTable(
          name = "group_moderators",
          joinColumns = @JoinColumn(name = "group_id"),
          inverseJoinColumns = @JoinColumn(name = "user_id")
  )
  private List<ChatterClient> moderators;

  /**
   * Instantiates a new Chatter group.
   */
  public ChatterGroup() {}


  /**
   * Instantiates a new ChatterGroup.
   *
   * @param creator  the creator of the group
   * @param uniqueId the unique id
   * @param name     the name
   * @param password the password
   */
  public ChatterGroup(ChatterClient creator, String uniqueId, String name, String password)
  {
    this.name = name;
    this.uniqueId = uniqueId;
    this.password = password;
    this.members = new ArrayList<>();
    this.moderators = new ArrayList<>();
    moderators.add(creator);
    members.add(creator);
    this.groupType = 0;
  }

  /**
   * Gets members.
   *
   * @return the members
   */
  public List<ChatterClient> getMembers() {
    return this.members;
  }


  /**
   * Sets name.
   *
   * @param name the name
   */

  @Override
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Sets password.
   *
   * @param password the password
   */
  @Override
  public void setPassword(String password) {
    this.password = password;
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
   * Gets unique id.
   *
   * @return the unique id
   */
  @Override
  public String getUniqueId() {
    return this.uniqueId;
  }

  /**
   * Gets name.
   *
   * @return the name
   */
  @Override
  public String getName() {
    return this.name;
  }

  public int getGroupType() {
    return this.groupType;
  }

  public void setGroupType(int groupType) {
    this.groupType = groupType;
  }

  /**
   * Gets password.
   *
   * @return the password
   */
  @Override
  public String getPassword() {
    return this.password;
  }

  /**
   * Sets members.
   *
   * @param members the members
   */
  public void setMembers(List<ChatterClient> members) {
    this.members = members;
  }

  /**
   * Sets moderators.
   *
   * @param moderators the moderators
   */
  public void setModerators(List<ChatterClient> moderators) {
    this.moderators = moderators;
  }

  /**
   * Gets id of the client.
   *
   * @return the clientId
   */
  @Override
  public Integer getId() {
    return this.groupId;
  }

  /**
   * Gets id of the client.
   *
   * @return the clientId
   */
  public Integer getGroupId() {
    return this.groupId;
  }

  /**
   * Gets all members.
   *
   * @return the all members
   */
  public List getAllMembers() {
    return this.members;
  }

  /**
   * Gets all moderators.
   *
   * @return the all moderators
   */
  public List getAllModerators() {
    return this.moderators;
  }




}
