package edu.northeastern.ccs.im.messages;

/**
 * Centralized location for message type abbreviation constants.
 */
public class MessageTypes {

  /** Private constructor, no instantiation. */
  private MessageTypes() {}

  // Client side message types
  /** A broadcast message */
  public static final String BROADCAST = "BCT";
  /** A login message */
  public static final String LOGIN = "LOG";
  /** A multicast message */
  public static final String MULTICAST = "MCM";
  /** A quit message */
  public static final String QUIT = "BYE";
  /** A registration message */
  public static final String REGISTER = "RGT";
  /** A simple login message */
  public static final String SIMPLE_LOGIN = "HLO";
  /** A unicast message */
  public static final String UNICAST = "UCM";

  // Command message type
  /** A command message */
  public static final String COMMAND = "CMD";

  // Response message types
  /** A failure response message */
  public static final String FAILURE = "FAI";
  /** A success response message */
  public static final String SUCCESS = "SUC";
}
