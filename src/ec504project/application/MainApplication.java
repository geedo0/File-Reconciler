package ec504project.application;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

public class MainApplication {
	private static int destPort = 8888;
	
	private static InetAddress ipAddress;

	public static void main(String[] args) {
		Timer timer = new Timer(false);
		long bandwidthUsed = 0;
		/*
		 * Process and verify input arguments
		 * Usage: reconcile [-file/-path] [file/path] -to [IP address of computer 2]
		 */
		
		if(!((args.length == 4) && (args[0].contentEquals("-file") || args[0].contentEquals("-path") || args[2].contentEquals("-to")))) {
			System.out.println("Error, invalid parameters");
			System.out.println("Usage: reconcile [-file/-path] [file/path] -to [IP address of computer 2]");
			return;
		}
		
		try {
			ipAddress = InetAddress.getByName(args[3]);
		} catch (UnknownHostException e) {
			System.out.println("Error: Could not resolve IP:\t" + args[3]);
			return;
		}
		
		if(args[0].contentEquals("-file")){
			File inputFile = new File(args[1]);
			if(!inputFile.exists()) {
				System.out.println("Error: Invalid input file:\t" + args[1]);
				return;
			}
			if(inputFile.isDirectory()) {
				System.out.println("Error: Input is not a file:\t" + args[1]);
				return;
			}
			System.out.println("Input File:\t" + inputFile.getPath());
			
			if (SendReceive.serverListening(ipAddress, destPort)){
				System.out.println("Connected IP Address:\t" + ipAddress.toString());
				timer.start();
				try {
					Socket localSocket = new Socket(ipAddress, destPort);
					
					bandwidthUsed = SendReceive.sendFile(inputFile, localSocket);
					
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
	
					System.out.println("Server started, awaiting connection...");
					Socket clientSocket = listenSocket.accept();//First connection is a dummy connection
	                
	
					clientSocket = listenSocket.accept();//this is a blocking call
					timer.start();
	
					System.out.println("Connected IP Address:\t" + clientSocket.getInetAddress().getHostAddress());
					//The accept method waits until a client starts up and requests a connection.
					
					bandwidthUsed = SendReceive.receiveFile(inputFile, clientSocket);
					
					listenSocket.close();
					clientSocket.close();
					
				}
				catch(IOException e) {
					System.out.println("Listen :"+e.getMessage());
				}			
	
			}
		} else if(args[0].contentEquals("-path")){
			File inputPath = new File(args[1]);
			if(!inputPath.exists()) {
				System.out.println("Error: Invalid input path:\t" + args[1]);
				return;
			}
			if(!inputPath.isDirectory()) {
				System.out.println("Error: Input is not a path:\t" + args[1]);
				return;
			}
			
			File[] filesInPath = inputPath.listFiles();
			FileObj[] fileList = new FileObj[filesInPath.length];
			
			for(int jj=0; jj<filesInPath.length; jj++)
			{
				fileList[jj] = new FileObj();
				fileList[jj].setFile(filesInPath[jj]);
				
				if(fileList[jj].isFile())
				{
					fileList[jj].setHash(Checksum.calcChecksum(fileList[jj].toString()));
					//System.out.println("File: ["+fileList[jj].toString()+"]["+fileList[jj].getDelta()+"]");
				} else if (fileList[jj].isDirectory()){
					fileList[jj].setHash(null);
					//System.out.println("Folder: ["+fileList[jj].toString()+"]["+fileList[jj].getDelta()+"]");
				}
			}
			
			Arrays.sort(fileList);
			/*for(FileObj file: fileList){
				System.out.println(file);
			}*/

		}
		/* Insert a way to send 2D array to other computer. 
		 * Insert function of being a host, which includes getting the hash values of each file
		 *    and comparing it to the receiver's values.
		 
		timer.stop();
		
		System.out.println("Bandwidth used:\t" + bandwidthUsed + " bytes");
		timer.prettyPrintTime();
		 */
		return;
	}
}
