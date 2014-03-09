package ec504project.application;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class MainApplication {

	private static InetAddress ipAddress;

	public static void main(String[] args) {
		/*
		 * Process and verify input arguments
		 * Usage: reconcile -file [file1] -to [IP address of computer 2]
		 */
		if(!((args.length == 4) && (args[0].contentEquals("-file") || args[2].contentEquals("-to")))) {
			System.out.println("Error, invalid parameters");
			System.out.println("Usage: reconcile -file [file1] -to [IP address of computer 2]\n");
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
		
		System.out.println("Valid parameters passed!");
		System.out.println("Input File:\t" + inputFile.getPath());
		System.out.println("IP Address:\t" + ipAddress.toString());
		return;
	}
}
