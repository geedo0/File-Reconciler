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
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainApplication {
	private static int destPort = 8888;
	
	private static InetAddress ipAddress;

	public static void main(String[] args) {
		Timer timer = new Timer(true);
		/*
		 * Process and verify input arguments
		 * Usage: reconcile -file [file1] -to [IP address of computer 2]
		 */
		if(!((args.length == 4) && (args[0].contentEquals("-file") || args[2].contentEquals("-to")))) {
			System.out.println("Error, invalid parameters");
			System.out.println("Usage: reconcile -file [file1] -to [IP address of computer 2]");
			return;
		}
		File inputFile = new File(args[1]);
		if(!inputFile.exists()) {
			System.out.println("Error: Invalid input file:\t" + args[1]);
			return;
		}
		
		try {
			ipAddress = InetAddress.getByName(args[3]);
		} catch (UnknownHostException e) {
			System.out.println("Error: Could not resolve IP:\t" + args[3]);
			return;
		}
		
		
		//Check to see if server is listening
		if (serverListening(ipAddress,destPort)){
			//server is listening
			try {
				Socket localSocket = new Socket(ipAddress, destPort);
				
				//send outgoing file
				SendFile(inputFile,localSocket);
				
				
				localSocket.close();
				
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}else{
			//server is not listening
			//become server
			try{
				//start listening and wait for connection    
				ServerSocket listenSocket = new ServerSocket(destPort);

				System.out.println("server start listening... ... ...");

				Socket clientSocket = listenSocket.accept();//First connection is a dummy connection
                

				clientSocket = listenSocket.accept();//this is a blocking call
				//The accept method waits until a client starts up and requests a connection.
				
                //write incoming file
				System.out.println("connection accepted");
				
				ReceiveFile(clientSocket);
				
				
				listenSocket.close();
				clientSocket.close();
				
			}
			catch(IOException e) {
				System.out.println("Listen :"+e.getMessage());
			}			

		}
				

		timer.stop();
		
		System.out.println("Valid parameters passed!");
		System.out.println("Input File:\t" + inputFile.getPath());
		System.out.println("IP Address:\t" + ipAddress.toString());
		timer.prettyPrintTime();
		return;
	}
	
	private static void SendFile(File inputFile,Socket clientSocket){
		try {
			
			InputStream inStream = new FileInputStream(inputFile);  
			PrintWriter out =  new PrintWriter(clientSocket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(inStream));
			String inputString;
			
			//Send filename
			out.println(inputFile.getName());
			
			String md5Hash = checksum.calc_checksum(inputFile.getAbsolutePath());
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
	
	private static void ReceiveFile(Socket clientSocket){

		try {

			BufferedReader in = new BufferedReader(
	                new InputStreamReader(clientSocket.getInputStream()));
			
			int fileSize = 0;
			//get filename
			String filename = in.readLine();
			String md5HashIn = in.readLine();
			
			//Write to file
			FileOutputStream fos = new FileOutputStream(filename);
			
			String fileString;
			while((fileString=in.readLine()) !=null){
				fileSize +=fileString.length();
				fos.write(fileString.getBytes(), 0, fileString.length());
			}
			
			System.out.println(filename+" File written.");
			System.out.println("File size = "+fileSize+" bytes.");
			fos.close();
			String md5HashComputed = checksum.calc_checksum(".\\" + filename);
			
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
	
	private static boolean serverListening(InetAddress host, int port)
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
