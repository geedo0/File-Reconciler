package ec504project.application;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class MainApplication {
	private static InetAddress ipAddress;
	private static File inputPath;

	public static void main(String[] args) {
		parseArguments(args);
		
		CoreReconciler reconciler = new CoreReconciler(inputPath, ipAddress);
		
		reconciler.timer.prettyPrintTime();
		System.out.println("Bandwidth used:\t" + reconciler.getPerformanceMetrics().bandwidth + " bytes");
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
