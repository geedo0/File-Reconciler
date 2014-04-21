package ec504project.application;

import java.io.File;
import java.net.InetAddress;
import java.util.ArrayList;

import ec504project.communication.FileListElement;
import ec504project.communication.Receiver;
import ec504project.communication.Sender;
import ec504project.communication.SenderData;

public class CoreReconciler {
	private InetAddress ipAddress;
	private File inputPath;
	private long bandwidthUsed;
	public Timer timer;
	private boolean waiting;
	private boolean isSender;
	
	public class Metrics {
		long bandwidth;
		long timeInNs;
		boolean success;
	}
	
	public CoreReconciler(File inputFile, InetAddress inputIP) {
		inputPath = inputFile;
		ipAddress = inputIP;
		waiting = false;
		isSender = false;
		
		timer = new Timer(true);
		bandwidthUsed = 0;
		FileObj localFileList = new FileObj(inputPath);

		//Connection is active, become the SENDER(client) and initiate reconciliation.
		if (Receiver.Listening(ipAddress)) {
			timer.start();
			isSender = true;
			System.out.println("Connected to IP Address:\t" + ipAddress.toString());
			System.out.println("Sender process started, sending file list.\nWaiting for receiver hashes.");

			//Send file list and wait
			Sender.SendList(localFileList.fileList, ipAddress);
			waiting = true;
			
			//Perform block matching on file list hashes
			ArrayList<SenderData> receiverHashes = Sender.ReceiveHashes();
			waiting = false;
			System.out.println("Searching for matching blocks and computing reconciliation steps.");
			
			ArrayList<BlockMatcher> blockMatchers = new ArrayList<BlockMatcher>();
			ArrayList<ArrayList<ReconcileStep>> sendSteps = new ArrayList<ArrayList<ReconcileStep>>();
			for(int i = 0; i < receiverHashes.size(); i++) {
				blockMatchers.add(new BlockMatcher(localFileList.fileList.get(receiverHashes.get(i).fileIndex).filePath, receiverHashes.get(i).hashes, receiverHashes.get(i).blockSize));
				sendSteps.add(blockMatchers.get(i).receiverSteps);
			}

			System.out.println("Sending reconciliation steps and waiting for ACK.");
			//Send reconciliation instructions + data and wait
			Sender.SendSteps(sendSteps,ipAddress);
			waiting = true;

			//Receive OK status and terminate
			Sender.ReceiveOk();
			System.out.println("ACK received, reconciliation complete.");
			waiting = false;

			bandwidthUsed = Sender.GetBandwidth();
		}
		else {
			System.out.println("Receiver process started.\nWaiting for file list.");
			waiting = true;
			Receiver.Accept();
			timer.start();

			//Receive file list
			ArrayList<FileListElement> senderFileList = Receiver.ReceiveList();
			waiting = false;

			System.out.println("Comparing file lists and generating hash maps.");
			
			//Compare file list
			ArrayList<Integer> diffList = localFileList.generateDiffList(senderFileList);
			//Generate hashes for non-matching files
			ArrayList<FileSummary> fileSummaries = new ArrayList<FileSummary>();
			ArrayList<SenderData> hashesOnly = new ArrayList<SenderData>(diffList.size());
			for(int i = 0; i < diffList.size(); i++) {
				fileSummaries.add(new FileSummary(localFileList.fileList.get(diffList.get(i)).filePath));
				hashesOnly.add(fileSummaries.get(i).getSenderData(diffList.get(i)));
			}
			//Send hashes
			System.out.println("Sending hash maps and waiting for reconciliation steps.");
			Receiver.SendHashes(hashesOnly);	
			waiting = true;
			//Reconcile files based on steps
			ArrayList<ArrayList<ReconcileStep>> senderSteps = Receiver.ReceiveSteps();
			waiting = false;
			System.out.println("Reconciling files.");
			for(int i = 0; i < diffList.size(); i++) {
				ReconcileFile.regenerateFile(senderSteps.get(i), fileSummaries.get(i).fileBlocks, localFileList.fileList.get(diffList.get(i)).filePath, senderFileList.get(diffList.get(i)).fileHash);
			}

			//Send ok signal and terminate
			System.out.println("File reconciliation complete. Sending ACK.");
			Receiver.SendOk();

			bandwidthUsed = Receiver.GetBandwidth();
		}
		timer.stop();
	}
	
	public Metrics getPerformanceMetrics() {
		Metrics performance = new Metrics();
		performance.bandwidth = bandwidthUsed;
		performance.timeInNs = timer.nanosecondsElapsed;
		performance.success = true;		//Failure is NOT an option
		
		return performance;
	}
	
	public boolean isWaiting() {
		return waiting;
	}
	
	public boolean isSender() {
		return isSender;
	}
}
