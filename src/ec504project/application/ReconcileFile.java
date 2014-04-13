package ec504project.application;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


public class ReconcileFile {
	
	public static void regenerateFile(ArrayList<ReconcileStep> steps, ArrayList<byte[]> blocks, File out, String verifyHash) {
		try {
			out.delete();
			out.createNewFile();
			FileOutputStream fos = new FileOutputStream(out);
			
			ReconcileStep currentStep;
			
			for(int i = 0; i < steps.size(); i++) {
				currentStep = steps.get(i);
				switch(currentStep.step) {
				case insertData:
					fos.write(arrayListToByteArray(currentStep.data));
					break;
				case insertBlock:
					fos.write(blocks.get(currentStep.blockIndex));
					break;
				default:
					break;
				}
			}
			fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String newHash = Checksum.calcChecksum(out.getAbsolutePath());
		if(newHash.equals(verifyHash)) {
			System.out.println("Successfully reconciled " + out.getName());
		}
		else {
			System.out.println("We have failed.");
		}
	}
	
	private static byte[] arrayListToByteArray(ArrayList<Byte> input) {
		byte[] output = new byte[input.size()];
		for(int i = 0; i < input.size(); i++) {
			output[i] = input.get(i);
		}
		return output;
	}
}
