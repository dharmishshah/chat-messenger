package edu.northeastern.ccs.im;

import edu.northeastern.ccs.im.server.Prattle;
import net.bytebuddy.pool.TypePool;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.security.spec.ECField;
import java.util.logging.ConsoleHandler;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * The type Chat logger test.
 */
class ChatLoggerTest {


  /**
   * Sets up.
   */
  @BeforeEach
  void setUp() {

  }

  /**
   * Info read.
   *
   * @throws IOException the io exception
   */
  @Test
  void infoRead() throws IOException {

  }


  /**
   * Info.
   *
   * @throws ClassNotFoundException the class not found exception
   */
  @Test
  void info() throws ClassNotFoundException {

  }

  /**
   * Sets mode.
   */
  @Test
  void setMode() {
  }


  /**
   * Private constructor instantiation.
   *
   * @throws NoSuchMethodException     the no such method exception
   * @throws IllegalAccessException    the illegal access exception
   * @throws InvocationTargetException the invocation target exception
   * @throws InstantiationException    the instantiation exception
   */
  @Test
  public void privateConstructorInstantiation() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {

    assertThrows(Exception.class, () -> {
      Constructor<ChatLogger> chatLoggerConstructor = ChatLogger.class.getDeclaredConstructor();
      chatLoggerConstructor.setAccessible(true);
      chatLoggerConstructor.newInstance();
    });

  }



}