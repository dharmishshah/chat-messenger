package edu.northeastern.ccs.im.server.utils;

import org.junit.jupiter.api.Test;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Testing the Encryption Utilities provided for Encrypting and Decrypting.
 */
class SymmetricEncryptionOperationsTest {


  /**
   * Seeing if we can properly generate an AES SecretKey
   *
   * @throws NoSuchAlgorithmException will not be thrown as AES is hard-coded
   */
  @Test
  void testKeyGeneration() throws NoSuchAlgorithmException {
    SecretKey generateKey = SymmetricEncryptionOperations.generateAES256Key();
    assertNotNull(generateKey);
    assertEquals("AES", generateKey.getAlgorithm());
  }

  /**
   * Tests encrypting a message, and decrypting it back.
   *
   * @throws IllegalBlockSizeException thrown if bad encoding occurs.
   * @throws InvalidKeyException       thrown if an invalid key is specified.
   * @throws BadPaddingException       thrown if bad encoding occurs.
   * @throws NoSuchAlgorithmException  will not be thrown as AES is hard-coded.
   * @throws NoSuchPaddingException    thrown if bad encoding occurs.
   */
  @Test
  void testEncryptDecrypt() throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
    SecretKey genKey = SymmetricEncryptionOperations.generateAES256Key();
    String testString = "Hello this is a test!";
    String encrypted = SymmetricEncryptionOperations.encryptMessageAES(genKey, testString);
    String decrypted = SymmetricEncryptionOperations.decryptMessageAES(genKey, encrypted);
    assertEquals("Hello this is a test!", decrypted);
  }

  /**
   * Testing if converting a SecretKey to a String and back preserves it.
   *
   * @throws NoSuchAlgorithmException will not be thrown, as AES is hard-coded.
   */
  @Test
  void keyToStringAndBack() throws NoSuchAlgorithmException {
    SecretKey genKey = SymmetricEncryptionOperations.generateAES256Key();
    String keyToString = SymmetricEncryptionOperations.keyToString(genKey);
    SecretKey newKey = SymmetricEncryptionOperations.stringToKey(keyToString);
    String newKeyToString = SymmetricEncryptionOperations.keyToString(newKey);
    assertEquals(keyToString, newKeyToString);
    assertEquals(newKeyToString, keyToString);
  }

  /**
   * Generating a SecretKey from a provided String as a seed.
   *
   * @throws IllegalBlockSizeException thrown if bad encoding occurs.
   * @throws InvalidKeyException       thrown if an invalid key is specified.
   * @throws BadPaddingException       thrown if bad encoding occurs.
   * @throws NoSuchAlgorithmException  will not be thrown as AES is hard-coded.
   * @throws NoSuchPaddingException    thrown if bad encoding occurs.
   */
  @Test
  void keyFromStringInput() throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
    String input = "this is a sample";
    String message = Base64.getEncoder().encodeToString(input.getBytes());
    SecretKey genKey = SymmetricEncryptionOperations.stringToKey(message);
    String testString = "Hello this is a test!";
    String encrypted = SymmetricEncryptionOperations.encryptMessageAES(genKey, testString);
    String decrypted = SymmetricEncryptionOperations.decryptMessageAES(genKey, encrypted);
    assertEquals("Hello this is a test!", decrypted);
  }

}