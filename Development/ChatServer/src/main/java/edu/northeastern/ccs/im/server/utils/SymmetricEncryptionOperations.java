package edu.northeastern.ccs.im.server.utils;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

// Consulted the Java Docs for encryption documentation
// https://docs.oracle.com/javase/8/docs/api/javax/crypto/package-summary.html

/**
 * Provides various operations in relation to encrypting and decrypting a String
 */
public class SymmetricEncryptionOperations {

  /**
   * Hiding the default public constructor.
   */
  private SymmetricEncryptionOperations() {
  }

  /**
   * Generates at 256-Bit AES Encryption Key
   *
   * @return a generated 256-Bit Encryption Key for use in the AES Algorithm
   * @throws NoSuchAlgorithmException will not be thrown, as "AES" is guaranteed.
   */
  public static SecretKey generateAES256Key() throws NoSuchAlgorithmException {
    KeyGenerator genKey = KeyGenerator.getInstance("AES");
    genKey.init(256);
    return genKey.generateKey();
  }

  /**
   * Takes in a SecretKey and encodes it to a Base-64 String for transport.
   *
   * @param key the SecretKey the method is provided with.
   * @return a Base-64 representation of the ingested SecretKey.
   */
  public static String keyToString(SecretKey key) {
    return Base64.getEncoder().encodeToString(key.getEncoded());
  }

  /**
   * Given a Base-64 encoded String, it turns that String back into a SecretKey.
   *
   * @param key the Base-64 string we are providing.
   * @return the Base-64 string encoded back into a SecretKey object.
   */
  public static SecretKey stringToKey(String key) {
    byte[] keyToByte = Base64.getDecoder().decode(key);
    return new SecretKeySpec(keyToByte, 0, keyToByte.length, "AES");
  }

  /**
   * Provided with a SecretKey and String to encrypt, we encrypt the String with AES Encryption.
   *
   * @param key     the SecretKey being provided, using the generateAES256Key method preferably.
   * @param message the message we want to have encrypted.
   * @return an encrypted version of the supplied message.
   * @throws NoSuchPaddingException    thrown if a bad encoding happens.
   * @throws NoSuchAlgorithmException  will never be thrown as "AES" is guaranteed.
   * @throws InvalidKeyException       thrown if a bad SecretKey is provided.
   * @throws BadPaddingException       thrown if bad encoding happens.
   * @throws IllegalBlockSizeException thrown if bad encoding happens.
   */
  public static String encryptMessageAES(SecretKey key, String message) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
    Cipher encryptCipher = Cipher.getInstance("AES");
    encryptCipher.init(Cipher.ENCRYPT_MODE, key);
    return Base64.getEncoder().encodeToString(encryptCipher.doFinal(message.getBytes()));
  }

  /**
   * Given an AES encrypted message, we return a decoded String.
   *
   * @param key     the SecretKey used to encrypt the message.
   * @param message the encrypted message text.
   * @return a decrypted message reconstructed from our encrypted message.
   * @throws NoSuchPaddingException    thrown if a bad encoding happens.
   * @throws NoSuchAlgorithmException  will never be thrown as "AES" is guaranteed.
   * @throws InvalidKeyException       thrown if a bad SecretKey is provided.
   * @throws BadPaddingException       thrown if a bad encoding happens.
   * @throws IllegalBlockSizeException thrown if a bad encoding happens.
   */
  public static String decryptMessageAES(SecretKey key, String message) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
    Cipher decryptCipher = Cipher.getInstance("AES");
    decryptCipher.init(Cipher.DECRYPT_MODE, key);
    byte[] toByteMessage = Base64.getDecoder().decode(message);
    return new String(decryptCipher.doFinal(toByteMessage));
  }

}
