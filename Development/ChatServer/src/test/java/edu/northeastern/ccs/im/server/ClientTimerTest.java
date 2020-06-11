package edu.northeastern.ccs.im.server;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

class ClientTimerTest {

  private ClientTimer timerTest;

  @BeforeEach
  public void setUp(){
    timerTest = new ClientTimer();

  }

  @Test
  void isBehind() throws InterruptedException {
    assertFalse(timerTest.isBehind());


  }
}