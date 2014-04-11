package ec504project.application;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import ec504project.application.BlockMatcher.ReconcileStep;

public class ReconcileFile {
	
	public static File regenerateFile(ArrayList<ReconcileStep> steps, ArrayList<byte[]> blocks) {
		File tempFile = new File("v3ry.un1qu3f1l3n4m3");
		try {
			tempFile.createNewFile();
			FileOutputStream fos = new FileOutputStream(tempFile);
			
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
		return tempFile;
		
	}
	
	private static byte[] arrayListToByteArray(ArrayList<Byte> input) {
		byte[] output = new byte[input.size()];
		for(int i = 0; i < input.size(); i++) {
			output[i] = input.get(i);
		}
		return output;
	}
}
