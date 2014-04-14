package ec504project.communication;


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

import ec504project.application.ReconcileStep;




public class Receiver {
	private static int destPort = 8888;
	private static ServerSocket serverSocket;
	private static Socket server;
	private static int Bandwidth = 0;
	
	public static int GetBandwidth(){
		return Bandwidth;
	}
	
	
	
	public static void Accept(){

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

	public static  ArrayList<FileListElement> ReceiveList(){
        ArrayList<FileListElement> receivedList = new ArrayList<FileListElement>();
		try {
			if (serverSocket == null){
				serverSocket = new ServerSocket(destPort);
			}

			server = serverSocket.accept(); //this is a blocking call
			ObjectInputStream objectInput = new ObjectInputStream(new BufferedInputStream(server.getInputStream()));

			
			Object object = objectInput.readObject();
			Bandwidth = Bandwidth + objectInput.readInt();
			    
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


	public static void SendHashes(ArrayList<SenderData> sendHash){
		try {
			DataOutputStream os = new DataOutputStream(new BufferedOutputStream(server.getOutputStream()));
			ObjectOutputStream out = new ObjectOutputStream(os);

			out.writeObject(sendHash);
			out.flush();			
			out.writeInt(os.size()+6);//Send the length that we are sending including the integer and 2 byte header.
			out.flush();
			Bandwidth = Bandwidth + os.size();
			server.close();


		} catch (IOException e) {
			System.out.println("Error during serverReceive.");
			e.printStackTrace();
		}		

	}
	
	public static void SendOk() {
		try {
			DataOutputStream os = new DataOutputStream(new BufferedOutputStream(server.getOutputStream()));
			ObjectOutputStream out = new ObjectOutputStream(os);
            out.writeUTF("Ok");
            out.flush();
            out.writeInt(os.size()+6);//Send the length that we are sending including the integer and 2 byte header.
            out.flush();
			Bandwidth = Bandwidth + os.size();
			server.close();


		} catch (IOException e) {
			System.out.println("Error during serverReceive.");
			e.printStackTrace();
		}
		
	}		
	

	public static  ArrayList<ArrayList<ReconcileStep>> ReceiveSteps(){
		ArrayList<ArrayList<ReconcileStep>> receivedList = new ArrayList<ArrayList<ReconcileStep>>();
		try {
			if (serverSocket == null){
				serverSocket = new ServerSocket(destPort);
			}

			server = serverSocket.accept(); //this is a blocking call
			ObjectInputStream objectInput = new ObjectInputStream(new BufferedInputStream(server.getInputStream()));

			
			Object object = objectInput.readObject();
			Bandwidth = Bandwidth + objectInput.readInt();
			    
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


	public static boolean Listening(InetAddress host)
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
