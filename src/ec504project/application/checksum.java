package ec504project.application;

import java.io.*;
import java.math.BigInteger;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class checksum {
	
	public static String calc_checksum(String filepath){
		try{
			MessageDigest md = MessageDigest.getInstance("MD5");
			FileInputStream finstream = new FileInputStream(filepath); 
		    
			//A stream that updates the message digest using the bits going through the stream. 
			DigestInputStream dinstream = new DigestInputStream(finstream, md);
			
			byte[] buffer = new byte[10_485_760]; //10MB
			//Suppression needed to remove warning of unused variable "line"
		    @SuppressWarnings("unused")
			int line = 0;
		    while ((line = dinstream.read(buffer)) != -1) {
		    	//System.out.println(line);
		    }
		    dinstream.close();
		    finstream.close();
			
		    return new BigInteger(1, md.digest()).toString(16);
		    
		} catch (FileNotFoundException e) {
	      System.out.println("There was an exception! The file was not found!");
	    } catch (IOException e) {
	      System.out.println("There was an exception handling the file!");
	    } catch (NoSuchAlgorithmException e) {
	      System.out.println("There was an exception! Algorithm does not exist!");
		}
		return null;
	}

}
