package edu.northeastern.ccs.im.massagers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Allows for any swear words in a message to be replaced with all * characters.
 */
public class ProfanityFilter implements IMessageStrategy{
  private Map<String, Integer> swearWordToLength = new HashMap<>();
  private final String file;

  /**
   * Loads the initial profanity word file into a HashMap for later use.
   * @throws IOException thrown if the file cannot be found.
   */
  public ProfanityFilter(String inputFile) throws IOException {
    this.file = inputFile;
    loadFile();
  }

  /**
   * Loads the profanity file.
   * @throws IOException thrown if the file cannot be found.
   */
  private void loadFile() throws IOException {
    try (BufferedReader readFile = new BufferedReader(new FileReader(file))) {
      String eachLine;
      while ((eachLine = readFile.readLine()) != null) {
        String[] wordArr = eachLine.split(",");
        String swearWord = wordArr[1];
        swearWordToLength.put(swearWord, swearWord.length());
      }
    } catch (Exception e) {
      throw new IOException("Error loading file");
    }
  }

  /**
   * For any given text, an equivalent length all * String will be replaced.
   * @param length the length of the text we are replacing.
   * @return a new String of the supplied length, containing all * characters.
   */
  private String generateStars(int length) {
    StringBuilder buildUp = new StringBuilder();
    for (int i = 0; i < length; i++) {
      buildUp.append("*");
    }
    return new String(buildUp);
  }

  /**
   * Replaces any swear word with * characters instead.
   * @param text the text we are ingesting for modification.
   * @return the ingested message, but with every swear word removed.
   */
  public String performTextEnhancement(String...text) {
    String[] messageBroken = text[0].split("\\s+");
    for (int i = 0; i < messageBroken.length; i++) {
      String getWordAtPos = messageBroken[i].toLowerCase();
      if (swearWordToLength.containsKey(getWordAtPos.trim())) {
        messageBroken[i] = generateStars(swearWordToLength.get(getWordAtPos));
      }
    }
    return recombineText(messageBroken);
  }


  /**
   * Correctly recombines an array of String tokens, back into a String.
   * @param tokens the "tokens" we are going to recombine.
   * @return a String made up of the incoming tokens.
   */
  private String recombineText(String[] tokens) {
    StringBuilder buildUp = new StringBuilder();
    for (String eachWord : tokens) {
      buildUp.append(eachWord);
      buildUp.append(" ");
    }
    return new String(buildUp).trim();
  }
}
