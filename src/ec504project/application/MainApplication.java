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
	private static File inputFile;

	public static void main(String[] args) {
		Timer timer = new Timer(false);
		long bandwidthUsed = 0;
		
		parseArguments(args);
		
		//The connection is not yet active, become the receiver and await further instructions.
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

	private static void parseArguments(String[] args) {
		/*
		 * Process and verify input arguments
		 * Usage: reconcile -path [path to reconcile] -to [IP address of other computer]
		 */
		
		if(!((args.length == 4) && (args[0].contentEquals("-path") || args[2].contentEquals("-to")))) {
			System.out.println("Error, invalid parameters");
			System.out.println("Usage: reconcile -path [path] -to [IP address of other computer]");
			System.exit(-1);
		}
		inputFile = new File(args[1]);
		if(!inputFile.isDirectory()) {
			System.out.println("Error: Invalid input path:\t" + args[1] + "\nCheck that you have passed a directory and that it exists.");
			System.exit(-1);
		}
		
		try {
			ipAddress = InetAddress.getByName(args[3]);
		} catch (UnknownHostException e) {
			System.out.println("Error: Could not resolve IP:\t" + args[3]);
			System.exit(-1);
		}
		
		System.out.println("Reconciliation Directory:\t" + inputFile.getPath());
	}
}
