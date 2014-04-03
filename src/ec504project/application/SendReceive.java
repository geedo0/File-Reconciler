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
	public static long sendFile(File inputFile,Socket clientSocket){
		long bandwidth = 0;
		try {
			
			InputStream inStream = new FileInputStream(inputFile);  
			PrintWriter out =  new PrintWriter(clientSocket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(inStream));
			String inputString;
			
			String md5Hash = Checksum.calcChecksum(inputFile.getAbsolutePath());
			bandwidth += md5Hash.length();
			out.println(md5Hash);

			/*
			 * Gerardo 4/2/14 - We shouldn't be dependent on newline characters. This causes problems with Windows/Unix setups
			 * Also, consider the case where we have an n byte ASCII file without a single newline. What does this do to our running time?
			 * Conersely what happens if we have an n byte file of just newline characters?
			 */
			while ((inputString = in.readLine()) != null) {
			    out.println(inputString);
				bandwidth += inputString.length();
			}
            in.close();

		} catch (IOException ex) {
			System.out.println("Error in SendFile IO:"+ex.getMessage());
		}
		return bandwidth;
	}
	
	public static long receiveFile(File inputFile, Socket clientSocket){
		long fileSize = 0;
		
		try {
			BufferedReader in = new BufferedReader(
	                new InputStreamReader(clientSocket.getInputStream()));
			
			
			String md5HashIn = in.readLine();
			fileSize += md5HashIn.length();
			
			String md5HashLocal = Checksum.calcChecksum(inputFile.getAbsolutePath());
			System.out.println("Local Hash:\t" + md5HashLocal);
			System.out.println("Received Hash:\t" + md5HashIn);
			
			if(Checksum.verifyChecksum(md5HashIn, md5HashLocal)) {
				//The files are equivalent, our work here is done
				System.out.println("No reconciliation necessary");
			}
			else {
				/*
				 * We must now reconcile the file somehow.
				 * For the naive implementation, this means deleting the local file and receiving the file via TCP/IP
				 */
				System.out.println("Initial hash check failed, reconciling files");
				
				//Naive Reconciler Start - Delete this code in the future
				inputFile.delete();
				
				FileOutputStream fos = new FileOutputStream(inputFile);
				
				/*
				 * Gerardo 4/2/14 - Do we account for the size of newline and unprintable characters in the file if we track bndwidth in this manner?
				 */
				String fileString;
				while((fileString=in.readLine()) !=null){
					fileSize += fileString.length();
					fos.write(fileString.getBytes(), 0, fileString.length());
				}
				
				fos.close();
				//Naive Reconciler End

				md5HashLocal = Checksum.calcChecksum(inputFile.getAbsolutePath());
				System.out.println("Reconciled Hash:\t" + md5HashLocal);
				if(md5HashLocal.equals(md5HashIn)) {
					System.out.println("File verification passed!");
				}
				else {
					System.out.println("File verification failed!");
				}
			}
		} catch (IOException ex) {
			System.out.println("Error in ReceiveFile IO:"+ex.getMessage());
		}
		return fileSize;
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
