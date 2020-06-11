package edu.northeastern.ccs.im.dataconnections.models;


import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The type Chatter message.
 */
@Entity
@Table(name = "message")
public class ChatterMessage {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "message_id")
  private Integer messageId;

  /**
   * The Message type.
   */
  @Column(name = "message_type")
  String messageType;

  @Column(name = "message_sender")
  private Integer messageSender;

  @Column(name = "message_receiver")
  private Integer messageReceiver;

  @Column(name = "message_text")
  private String messageContent;

  @Column(name = "time_stamp")
  private Date timeStamp;

  /**
   * The Message status.
   */
  @Column(name = "status")
  Status messageStatus;

  @Column(name = "isGroupMessage")
  private boolean isGroupMessage;

  /**
   * Instantiates a new Chatter message.
   */
  public ChatterMessage() {}

  /**
   * Gets message id.
   *
   * @return the message id
   */
  public Integer getMessageId() {
    return messageId;
  }

  /**
   * Sets message id.
   *
   * @param messageId the message id
   */
  public void setMessageId(Integer messageId) {
    this.messageId = messageId;
  }

  /**
   * Gets message type.
   *
   * @return the message type
   */
  public String getMessageType() {
    return messageType;
  }

  /**
   * Sets message type.
   *
   * @param messageType the message type
   */
  public void setMessageType(String messageType) {
    this.messageType = messageType;
  }

  /**
   * Gets message sender.
   *
   * @return the message sender
   */
  public Integer getMessageSender() {
    return messageSender;
  }

  /**
   * Sets message sender.
   *
   * @param messageSender the message sender
   */
  public void setMessageSender(Integer messageSender) {
    this.messageSender = messageSender;
  }

  /**
   * Gets message receiver.
   *
   * @return the message receiver
   */
  public Integer getMessageReceiver() {
    return messageReceiver;
  }

  /**
   * Sets message receiver.
   *
   * @param messageReceiver the message receiver
   */
  public void setMessageReceiver(Integer messageReceiver) {
    this.messageReceiver = messageReceiver;
  }

  /**
   * Gets message content.
   *
   * @return the message content
   */
  public String getMessageContent() {
    return messageContent;
  }

  /**
   * Sets message content.
   *
   * @param messageContent the message content
   */
  public void setMessageContent(String messageContent) {
    this.messageContent = messageContent;
  }

  /**
   * Gets time stamp.
   *
   * @return the time stamp
   */
  public Date getTimeStamp() {
    return timeStamp;
  }

  /**
   * Sets time stamp.
   *
   * @param timeStamp the time stamp
   */
  public void setTimeStamp(Date timeStamp) {
    this.timeStamp = timeStamp;
  }

  /**
   * Gets message status.
   *
   * @return the message status
   */
  public Status getMessageStatus() {
    return messageStatus;
  }

  /**
   * Sets message status.
   *
   * @param messageStatus the message status
   */
  public void setMessageStatus(Status messageStatus) {
    this.messageStatus = messageStatus;
  }

  /**
   * Is group message boolean.
   *
   * @return the boolean
   */
  public boolean isGroupMessage() {
    return isGroupMessage;
  }

  /**
   * Sets group message.
   *
   * @param groupMessage the group message
   */
  public void setGroupMessage(boolean groupMessage) {
    isGroupMessage = groupMessage;
  }

  /**
   * Instantiates a new Chatter message.
   *
   * @param messageType     the message type
   * @param messageSender   the message sender
   * @param messageReceiver the message receiver
   * @param messageContent  the message content
   * @param timeStamp       the time stamp
   * @param messageStatus   the message status
   * @param isGroupMessage  the is group message
   */
  public ChatterMessage(String messageType, int messageSender, int messageReceiver,
                        String messageContent, Date timeStamp, Status messageStatus,
                        boolean isGroupMessage) {
    this.messageType = messageType;
    this.isGroupMessage = isGroupMessage;
    this.messageSender = messageSender;
    this.messageContent = messageContent;
    this.messageReceiver = messageReceiver;
    this.timeStamp = timeStamp;
    this.messageStatus = messageStatus;
  }
}
