package com.log.helpers

import java.security.MessageDigest;
import java.util.Arrays;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
 
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
 
public class AppCrypt {
  static String IV = "AAAAAAAAAAAAAAAA";
  static String plaintext = "test text 123\0\0\0"; /*Note null padding*/
  static String encryptionKey = "0123456789abcdef";
  
  static{
	  
	  
  }
  
  //TODO Implement
  public static encrypt(String str){
	  
	  return str;
  
  }
  //TODO Implement
  public static decrypt(String str){
	  
	  return str;
  }
  
  public static String encrypttext(String txt){
	  
	  
	  byte[] cipher = encrypt(txt, encryptionKey);
	  
	  
		  String str = new String(cipher);
		  
		  return str
  }
  
  public static String decrypttext(String txt){
	  
	  
	  byte[] cipher=txt.getBytes();
	  String str = decrypt(cipher, encryptionKey);
	  
	  
		 
		  
		  return str
  }
  
  
static main(args) {
	try {
	  
	  System.out.println("==Java==");
	  System.out.println("plain:   " + plaintext);
 
 String entext=encrypttext(plaintext)
	  System.out.print("cipher:  " +entext);
	  
	  
	  System.out.println("");
 
 
	  System.out.println("decrypt: " + decrypttext(entext));
 
	} catch (Exception e) {
	  e.printStackTrace();
	}
  }
 
  public static byte[] encrypt(String plainText, String encryptionKey) throws Exception {
	Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding", "SunJCE");
	SecretKeySpec key = new SecretKeySpec(encryptionKey.getBytes("UTF-8"), "AES");
	cipher.init(Cipher.ENCRYPT_MODE, key,new IvParameterSpec(IV.getBytes("UTF-8")));
	return cipher.doFinal(plainText.getBytes("UTF-8"));
  }
 
  public static String decrypt(byte[] cipherText, String encryptionKey) throws Exception{
	Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding", "SunJCE");
	SecretKeySpec key = new SecretKeySpec(encryptionKey.getBytes("UTF-8"), "AES");
	cipher.init(Cipher.DECRYPT_MODE, key,new IvParameterSpec(IV.getBytes("UTF-8")));
	return new String(cipher.doFinal(cipherText),"UTF-8");
  }
}