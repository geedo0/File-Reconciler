package ec504project.application;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;


public class MainApplication {


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

		if (SendReceive.receiverListening(ipAddress)){
			System.out.println("Connected IP Address:\t" + ipAddress.toString());
			timer.start();
			
			FileObj sendObj = new FileObj(new File(".//SenderTest"));
			ArrayList<fileListElement> receivedList = new ArrayList<fileListElement>();
			SendReceive.senderSend(sendObj.fileList, ipAddress);
			
			receivedList = SendReceive.senderReceive();
			
			System.out.println("Client received : "+receivedList);
			
			bandwidthUsed = SendReceive.getSenderBandwidth();
			
		}else{
			//receiver is not listening
			//become receiver
			//start listening and wait for connection    

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

		timer.stop();
		
		System.out.println("Bandwidth used:\t" + bandwidthUsed + " bytes");
		timer.prettyPrintTime();
		return;
	}
}
