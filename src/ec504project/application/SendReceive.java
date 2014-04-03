package ec504project.application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SendReceive {
	public static void sendFile(File inputFile,Socket clientSocket){
		try {
			
			InputStream inStream = new FileInputStream(inputFile);  
			PrintWriter out =  new PrintWriter(clientSocket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(inStream));
			String inputString;
			
			//Send filename
			out.println(inputFile.getName());
			
			String md5Hash = Checksum.calcChecksum(inputFile.getAbsolutePath());
			out.println(md5Hash);
			
			//Send file
			while ((inputString = in.readLine()) != null) {
			     out.println(inputString);
			}
            in.close();

		} catch (IOException ex) {
			System.out.println("Error in SendFile IO:"+ex.getMessage());
		}
	}
	
	public static void receiveFile(Socket clientSocket){

		try {

			BufferedReader in = new BufferedReader(
	                new InputStreamReader(clientSocket.getInputStream()));
			
			int fileSize = 0;
			//get filename
			String filename = in.readLine();
			String md5HashIn = in.readLine();
			
			//Write to file
			PrintWriter fos = new PrintWriter(filename);
			
			String fileString;
			while((fileString=in.readLine()) !=null){
				fileSize +=fileString.length();
				fos.println(fileString);
			}
			
			System.out.println(filename+" File written.");
			System.out.println("File size = "+fileSize+" bytes.");
			fos.close();
			String md5HashComputed = Checksum.calcChecksum(".\\" + filename);
			
			if(md5HashComputed.equals(md5HashIn)) {
				System.out.println("File verification passed!");
			}
			else {
				System.out.println("File verification failed!");
			}
			
		} catch (IOException ex) {
			System.out.println("Error in ReceiveFile IO:"+ex.getMessage());
		}
	}
	
	public static boolean serverListening(InetAddress host, int port)
	{
		Socket s = new Socket();
		
	    try
	    {    	
	    	s.connect(new InetSocketAddress(host, port), 1000);
	        return true;
	    }
	    catch (Exception e)
	    {
	        return false;
	    }
	    finally
	    {
	        if(s != null)
	            try {s.close();}
	            catch(Exception e){}
	    }
	}	
}
