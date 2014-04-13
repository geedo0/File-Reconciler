package ec504project.application;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;

import ec504project.application.BlockMatcher.ReconcileStep;
import ec504project.application.FileObj.FileListElement;
import ec504project.application.FileSummary.SenderData;

public class MainApplication {


	private static InetAddress ipAddress;
	private static File inputPath;

	public static void main(String[] args) {
		Timer timer = new Timer(true);
		long bandwidthUsed = 0;
		parseArguments(args);
		FileObj localFileList = new FileObj(inputPath);
		
		
		/***********************
		* File Reconciler DEMO *
		***********************/		
//		//Create File Lists
//		FileObj receiverFileList = new FileObj(inputPath);
//		FileObj senderFileList = new FileObj(new File(".\\SenderTest"));
//		
//		//Create Blocks and Hashes
//		ArrayList<FileSummary> fileSummaries = new ArrayList<FileSummary>();
//		for(int i = 0; i < receiverFileList.fileList.size(); i++) {
//			fileSummaries.add(new FileSummary(receiverFileList.fileList.get(i).filePath));
//		}
//		
//		//Match blocks
//		ArrayList<BlockMatcher> blockMatchers = new ArrayList<BlockMatcher>();
//		for(int i = 0; i < fileSummaries.size(); i++) {
//			blockMatchers.add(new BlockMatcher(senderFileList.fileList.get(i).filePath, fileSummaries.get(i).blockHashes, fileSummaries.get(i).blockSize));
//		}
//		
//		//Regenerate the files
//		for(int i = 0; i < blockMatchers.size(); i++) {
//			ReconcileFile.regenerateFile(blockMatchers.get(i).receiverSteps, fileSummaries.get(i).fileBlocks, receiverFileList.fileList.get(i).filePath, senderFileList.fileList.get(i).fileHash);
//		}
//		timer.stop();
//		timer.prettyPrintTime();
//		
//		System.exit(0);
		
		//Connection is active, become the SENDER(client) and initiate reconciliation.
		if (SendReceive.receiverListening(ipAddress)) {
			System.out.println("Sender process started.");
			System.out.println("Connected IP Address:\t" + ipAddress.toString());
			timer.start();
			//try {
				//Send file list and wait
				SendReceive.senderSend(localFileList.fileList, ipAddress);
				
				//Perform block matching on file list hashes
//				ArrayList<SenderData> receiverHashes = receiveHashes();
//				ArrayList<BlockMatcher> blockMatchers = new ArrayList<BlockMatcher>();
//				ArrayList<ArrayList<ReconcileStep>> sendSteps = new ArrayList<ArrayList<ReconcileStep>>();
//				for(int i = 0; i < receiverHashes.size(); i++) {
//					blockMatchers.add(new BlockMatcher(localFileList.fileList.get(receiverHashes.get(i).fileIndex).filePath, receiverHashes.get(i).hashes, receiverHashes.get(i).blockSize));
//					sendSteps.add(blockMatchers.get(i).receiverSteps);
//				}
//				
//				//Send reconciliation instructions + data and wait
//				send(sendSteps);
//				
//				//Receive OK status and terminate
//				blockForOk();
//				
//				bandwidthUsed = SendReceive.sendFile(inputPath, localSocket);
//				
//				localSocket.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
		}
		else {
			//The connection is not yet active, become the RECEIVE(Server) and await further instructions.
		//	try{
				System.out.println("Receiver process started, awaiting connection...");
				SendReceive.receiverAccept();
				timer.start();
				
				//Receive file list
				ArrayList<FileListElement> senderFileList = SendReceive.receiverReceive();
				//Compare file list
				ArrayList<Integer> diffList = localFileList.generateDiffList(senderFileList);
				//Generate hashes for non-matching files
				ArrayList<FileSummary> fileSummaries = new ArrayList<FileSummary>();
				ArrayList<SenderData> hashesOnly = new ArrayList<SenderData>(diffList.size());
				for(int i = 0; i < diffList.size(); i++) {
					fileSummaries.add(new FileSummary(localFileList.fileList.get(i).filePath));
					hashesOnly.add(fileSummaries.get(i).getSenderData(i));
				}
				//Send hashes and wait
//				send(hashesOnly);
//				
//				//Reconcile files based on steps
//				ArrayList<ArrayList<ReconcileStep>> senderSteps = receiveSteps();
//				for(int i = 0; i < diffList.size(); i++) {
//					ReconcileFile.regenerateFile(senderSteps.get(i), fileSummaries.get(i).fileBlocks, localFileList.fileList.get(diffList.get(i)).filePath, senderFileList.get(diffList.get(i)).fileHash);
//				}
//				
//				//Send ok signal and terminate
//				send("ok");
//				
//				bandwidthUsed = SendReceive.receiveFile(inputPath, clientSocket);
//				
//				listenSocket.close();
//				clientSocket.close();
				
//			}
//			catch(IOException e) {
//				System.out.println("Listen :"+e.getMessage());
//			}			

		}
	timer.stop();
	timer.prettyPrintTime();
	System.out.println("Bandwidth used:\t" + bandwidthUsed + " bytes");
	return;
}
/* (SendReceive.receiverListening(ipAddress)){
			System.out.println("Connected IP Address:\t" + ipAddress.toString());
			timer.start();
			
			FileObj sendObj = new FileObj(new File(".//SenderTest"));
			ArrayList<fileListElement> receivedList = new ArrayList<fileListElement>();
			SendReceive.senderSend(sendObj.fileList, ipAddress);
			
			receivedList = SendReceive.senderReceive();
			
			System.out.println("Client received : "+receivedList);
			
			bandwidthUsed = SendReceive.getSenderBandwidth();
			
		}else{
			// receiver is not listening
			// become receiver
			// start listening and wait for connection    

			System.out.println("Server started, awaiting connection...");
			SendReceive.receiverAccept();//wait for connection
			timer.start();

			FileObj sendObj = new FileObj(new File(".//ReceiverTest"));
			ArrayList<fileListElement> receivedList = new ArrayList<fileListElement>();		
			
			
			receivedList = SendReceive.receiverReceive();			
			System.out.println("Server received : "+receivedList);
			SendReceive.receiverSend(sendObj.fileList);
			bandwidthUsed = SendReceive.getReceiverBandwidth();

		}
		*/

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
		inputPath = new File(args[1]);
		if(!inputPath.isDirectory()) {
			System.out.println("Error: Invalid input path:\t" + args[1] + "\nCheck that you have passed a directory and that it exists.");
			System.exit(-1);
		}
		
		try {
			ipAddress = InetAddress.getByName(args[3]);
		} catch (UnknownHostException e) {
			System.out.println("Error: Could not resolve IP:\t" + args[3]);
			System.exit(-1);
		}
		
		System.out.println("Reconciliation Directory:\t" + inputPath.getPath());
	}
}
