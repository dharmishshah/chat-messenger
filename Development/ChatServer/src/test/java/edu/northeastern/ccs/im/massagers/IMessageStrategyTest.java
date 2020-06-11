package edu.northeastern.ccs.im.massagers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IMessageStrategyTest {

  IMessageStrategy prof;

  /**
   * Profanity list of words downloaded from here,
   * https://www.frontgatemedia.com/a-list-of-723-bad-words-to-blacklist-and-how-to-use-facebooks-moderation-tool/
   * @throws IOException if the file is not found.
   */
  @BeforeEach
  void setUp() throws IOException {
    prof = new ProfanityFilter("profanity.csv");

  }

  @Test
  void allSwear() {
    assertEquals("**** ******* yo ****** tom", prof.performTextEnhancement("fuck fucking yo fucker tom"));
  }

  @Test
  void noSwear() {
    assertEquals("Hello Tom, this is Jonathan!", prof.performTextEnhancement("Hello Tom, this is Jonathan!"));
  }

  @Test
  void badFile() {
    assertThrows(IOException.class, () -> new ProfanityFilter("bad"));
  }
}