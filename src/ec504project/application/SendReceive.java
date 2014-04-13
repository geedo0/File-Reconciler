package ec504project.application;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;




public class SendReceive {
	private static int destPort = 8888;
	private static ServerSocket serverSocket;
	private static Socket server;
	private static Socket client;
	private static int senderBandwidth = 0;
	private static int receiverBandwidth = 0;
	
	public static int getReceiverBandwidth(){
		return receiverBandwidth;
	}
	
	public static int getSenderBandwidth(){
		return senderBandwidth;
	}	
	
	
	public static void receiverAccept(){

		try {
			if (serverSocket == null){
				serverSocket = new ServerSocket(destPort);
			}

			server = serverSocket.accept(); //this is a blocking call
          

		} catch (IOException e) {
			System.out.println("Error during serverAccept.");
			e.printStackTrace();
		}
		
	}

	public static  ArrayList<FileListElement> receiverReceiveList(){
        ArrayList<FileListElement> receivedList = new ArrayList<FileListElement>();
		try {
			if (serverSocket == null){
				serverSocket = new ServerSocket(destPort);
			}

			server = serverSocket.accept(); //this is a blocking call
			ObjectInputStream objectInput = new ObjectInputStream(new BufferedInputStream(server.getInputStream()));

			
			Object object = objectInput.readObject();
			receiverBandwidth = receiverBandwidth + objectInput.readInt();
			    
                receivedList =  (ArrayList<FileListElement>) object;        
		} catch (IOException e) {
			System.out.println("Error during serverReceive.");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("Error with the ArrayList in serverReceive");
			e.printStackTrace();
		}

		return receivedList;

	}


	public static void receiverSendHashes(ArrayList<SenderData> sendHash){
		try {
			DataOutputStream os = new DataOutputStream(new BufferedOutputStream(server.getOutputStream()));
			ObjectOutputStream out = new ObjectOutputStream(os);

			out.writeObject(sendHash);
			out.writeInt(os.size()+4);//Send the length that we are sending including the integer.
			out.flush();
			receiverBandwidth = receiverBandwidth + os.size();
			server.close();


		} catch (IOException e) {
			System.out.println("Error during serverReceive.");
			e.printStackTrace();
		}		

	}
	

	public static  ArrayList<ArrayList<ReconcileStep>> receiverReceiveSteps(){
		ArrayList<ArrayList<ReconcileStep>> receivedList = new ArrayList<ArrayList<ReconcileStep>>();
		try {
			if (serverSocket == null){
				serverSocket = new ServerSocket(destPort);
			}

			server = serverSocket.accept(); //this is a blocking call
			ObjectInputStream objectInput = new ObjectInputStream(new BufferedInputStream(server.getInputStream()));

			
			Object object = objectInput.readObject();
			receiverBandwidth = receiverBandwidth + objectInput.readInt();
			    
                receivedList =  (ArrayList<ArrayList<ReconcileStep>>) object;        
		} catch (IOException e) {
			System.out.println("Error during serverReceive.");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("Error with the ArrayList in serverReceive");
			e.printStackTrace();
		}

		return receivedList;

	}

	public static  ArrayList<SenderData> senderReceiveHashes(){
		ArrayList<SenderData> receivedList = new ArrayList<SenderData>();
		try {
			ObjectInputStream objectInput = new ObjectInputStream(new BufferedInputStream(client.getInputStream()));
			Object object = objectInput.readObject();
            receivedList =  (ArrayList<SenderData>) object;
	        senderBandwidth = senderBandwidth + objectInput.readInt();
			client.close();


		} catch (IOException e) {
			System.out.println("Error during clientReceive.");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("Error with the ArrayList in clientReceive");
			e.printStackTrace();
		}

		return receivedList;

	}


	public static void senderSendList(ArrayList<FileListElement> sendList, InetAddress ipAddress){
		try {
			if ((client == null)||(client.isClosed())){
				client = new Socket(ipAddress, destPort);
			}
			DataOutputStream os = new DataOutputStream(new BufferedOutputStream(client.getOutputStream()));
			ObjectOutputStream out = new ObjectOutputStream(os);
			out.writeObject(sendList);	
			out.writeInt(os.size()+4);//Send the length that we are sending including the integer.
			out.flush();
			senderBandwidth = senderBandwidth + os.size();

		} catch (IOException e) {
			System.out.println("Error during clientSend.");
			e.printStackTrace();
		}		

	}
	
	public static void senderSendSteps(ArrayList<ArrayList<ReconcileStep>> sendSteps, InetAddress ipAddress) {
		try {
			if ((client == null)||(client.isClosed())){
				client = new Socket(ipAddress, destPort);
			}
			DataOutputStream os = new DataOutputStream(new BufferedOutputStream(client.getOutputStream()));
			ObjectOutputStream out = new ObjectOutputStream(os);
			out.writeObject(sendSteps);	
			out.writeInt(os.size()+4);//Send the length that we are sending including the integer.
			out.flush();
			senderBandwidth = senderBandwidth + os.size();

		} catch (IOException e) {
			System.out.println("Error during clientSend.");
			e.printStackTrace();
		}		
		
		
	}		


	public static boolean receiverListening(InetAddress host)
	{
		Socket s = new Socket();

		try
		{    	
			s.connect(new InetSocketAddress(host, destPort), 1000);
			return true;
		}
		catch (Exception e)
		{
			return false;
		}
		finally
		{
			if(s != null)
				try {s.close();}
			catch(Exception e){}
		}
	}


}
