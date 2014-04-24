package ec504project.communication;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

import ec504project.application.ReconcileStep;




public class Sender {
	private static int destPort = 8888;
	private static Socket client;
	private static int Bandwidth = 0;
	
	public static int GetBandwidth(){
		return Bandwidth;
	}	
	

	@SuppressWarnings("unchecked")
	public static  ArrayList<SenderData> ReceiveHashes(){
		ArrayList<SenderData> receivedList = new ArrayList<SenderData>();
		try {
			ObjectInputStream objectInput = new ObjectInputStream(new BufferedInputStream(client.getInputStream()));
			Object object = objectInput.readObject();
            receivedList =  (ArrayList<SenderData>) object;
	        Bandwidth = Bandwidth + objectInput.readInt();
			client.close();


		} catch (IOException e) {
			System.out.println("Error during Sender.ReceiveHashes.");
			e.printStackTrace();
			System.exit(-1);
		} catch (ClassNotFoundException e) {
			System.out.println("Error with the ArrayList in Sender.ReceiveHashes");
			e.printStackTrace();
			System.exit(-1);
		}

		return receivedList;

	}


	public static void SendList(ArrayList<FileListElement> sendList, InetAddress ipAddress){
		try {
			if ((client == null)||(client.isClosed())){
				client = new Socket(ipAddress, destPort);
			}
			DataOutputStream os = new DataOutputStream(new BufferedOutputStream(client.getOutputStream()));
			ObjectOutputStream out = new ObjectOutputStream(os);
			out.writeObject(sendList);	
			out.flush();
			out.writeInt(os.size()+6);//Send the length that we are sending including the integer and 2 byte header.
			out.flush();
			Bandwidth = Bandwidth + os.size();

		} catch (IOException e) {
			System.out.println("Error during Sender.SendList.");
			e.printStackTrace();
			System.exit(-1);
		}		

	}
	
	public static void SendSteps(ArrayList<ArrayList<ReconcileStep>> sendSteps, InetAddress ipAddress) {
		try {
			if ((client == null)||(client.isClosed())){
				client = new Socket(ipAddress, destPort);
			}
			DataOutputStream os = new DataOutputStream(new BufferedOutputStream(client.getOutputStream()));
			ObjectOutputStream out = new ObjectOutputStream(os);
			out.writeObject(sendSteps);	
			out.flush();
			out.writeInt(os.size()+6);//Send the length that we are sending including the integer and 2 byte header.
			out.flush();
			Bandwidth = Bandwidth + os.size();

		} catch (IOException e) {
			System.out.println("Error during Sender.SendSteps.");
			e.printStackTrace();
			System.exit(-1);
		}		
		
		
	}		
	
	public static void ReceiveOk() {
		try {
			ObjectInputStream objectInput = new ObjectInputStream(new BufferedInputStream(client.getInputStream()));
			String receivedMessage = objectInput.readUTF();
	        Bandwidth = Bandwidth + objectInput.readInt();
			client.close();
            System.out.println("Received from receiver = "+receivedMessage);
		} catch (IOException e) {
			System.out.println("Error during Sender.ReceiveOk.");
			e.printStackTrace();
			System.exit(-1);
		}
		
	}	





}
