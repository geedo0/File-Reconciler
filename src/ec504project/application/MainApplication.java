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
	private static File inputFile;

	public static void main(String[] args) {
		Timer timer = new Timer(false);
		long bandwidthUsed = 0;
		
		parseArguments(args);
		//Generate the File List
		
		//Connection is active, become the SENDER(client) and initiate reconciliation.
		if (SendReceive.serverListening(ipAddress, destPort)){
			System.out.println("Sender process started.");
			System.out.println("Connected IP Address:\t" + ipAddress.toString());
			timer.start();
			try {
				Socket localSocket = new Socket(ipAddress, destPort);
				
				//Send file list and wait
				
				//Perform block matching on file list hashes
				//Send reconciliation instructions + data and wait
				
				//Receive OK status and terminate
				
				bandwidthUsed = SendReceive.sendFile(inputFile, localSocket);
				
				localSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else {
			//The connection is not yet active, become the RECEIVE(Server) and await further instructions.
			try{
				ServerSocket listenSocket = new ServerSocket(destPort);
				System.out.println("Receiver process started, awaiting connection...");
				//First connection is a dummy connection
				Socket clientSocket = listenSocket.accept();
				
				clientSocket = listenSocket.accept();
				timer.start();
				System.out.println("Connected IP Address:\t" + clientSocket.getInetAddress().getHostAddress());
				
				//Receive file list
				//Compare file list
				//Generate hashes for non-matching files
				//Send hashes and wait
				
				//Reconcile files based on steps
				//Verify hashes
				//Send ok signal and terminate
				
				bandwidthUsed = SendReceive.receiveFile(inputFile, clientSocket);
				
				listenSocket.close();
				clientSocket.close();
				
			}
			catch(IOException e) {
				System.out.println("Listen :"+e.getMessage());
			}			

		}
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
