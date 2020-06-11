package edu.northeastern.ccs.im.massagers;

/**
 * Allows for any sorts of modification to be performed on Message text.
 */
public interface IMessageStrategy {
  /**
   * Any sort of modification can be made to incoming Message text, and re-emitted.
   * @param text the text we are ingesting for modification.
   * @return the text, after being enhanced by our selected Message modification strategy.
   */
  String performTextEnhancement(String...text);
}
