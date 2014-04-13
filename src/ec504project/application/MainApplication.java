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
			ArrayList<String> my =  new ArrayList<String>();
			my.add("Hello");
			my.add("from");
			my.add("client");
			
			SendReceive.senderSend(my, ipAddress);
			ArrayList<String> myReceived = SendReceive.senderReceive();
			
			System.out.println("Client received : "+myReceived);
			my.clear();
			my.add("How");
			my.add("are");
			my.add("you?");
			
			SendReceive.senderSend(my, ipAddress);
			myReceived = SendReceive.senderReceive();
			System.out.println("Client received : "+myReceived);
			bandwidthUsed = SendReceive.getSenderBandwidth();
			
		}else{
			//receiver is not listening
			//become receiver
			//start listening and wait for connection    

			System.out.println("Server started, awaiting connection...");
			SendReceive.receiverAccept();//wait for connection
			timer.start();

			ArrayList<String> receiverReceived =  new ArrayList<String>();
			ArrayList<String> mySend = new ArrayList<String>();
			
			receiverReceived = SendReceive.receiverReceive();
			System.out.println("Server received : "+receiverReceived);
		    mySend.add("I");
		    mySend.add("am");
		    mySend.add("server");
			SendReceive.receiverSend(mySend);

			receiverReceived = SendReceive.receiverReceive();
			System.out.println("Server received : "+receiverReceived);
			mySend.clear();
			mySend.add("I am great!");
			mySend.add(" Good Bye");
			SendReceive.receiverSend(mySend);
			bandwidthUsed = SendReceive.getReceiverBandwidth();

		}

		timer.stop();
		
		System.out.println("Bandwidth used:\t" + bandwidthUsed + " bytes");
		timer.prettyPrintTime();
		return;
	}
}
