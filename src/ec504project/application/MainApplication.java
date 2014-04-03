package ec504project.application;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
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
		
		//Hash the input file and see if we even have to do any work
		String aggregateHash = Checksum.calcChecksum(inputFile.getAbsolutePath());
		
		//Check to see if server is listening
		if (SendReceive.serverListening(ipAddress,destPort)){
			//server is listening
			try {
				Socket localSocket = new Socket(ipAddress, destPort);
				
				//send outgoing file
				SendReceive.sendFile(inputFile,localSocket);
				
				
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
				
				SendReceive.receiveFile(clientSocket);
				
				
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
}
