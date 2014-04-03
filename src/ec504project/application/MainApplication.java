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
		Timer timer = new Timer(false);
		long bandwidthUsed = 0;
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
				

		timer.stop();
		
		System.out.println("Bandwidth used:\t" + bandwidthUsed + " bytes");
		timer.prettyPrintTime();
		return;
	}
}
