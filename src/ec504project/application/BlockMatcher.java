package ec504project.application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import ec504project.application.FileSummary.HashObject;

public class BlockMatcher {
	private HashMap<Integer, HashObject> receiverHashes;
	public ArrayList<ReconcileStep> receiverSteps;
	
	private long blockSize;
	
	
	public BlockMatcher(File input, HashMap<Integer, HashObject> receiverHashes, long blockSize) {
		this.receiverHashes = receiverHashes;
		this.blockSize = blockSize;
		
		byte[] data;
		RandomAccessFile f;
		try {
			f = new RandomAccessFile(input, "r");
			int length = (int) input.length();
			data = new byte[length];
			f.readFully(data);
			f.close();
			receiverSteps = computeDiff(data);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
}
	
	public enum Instruction {
		insertData, insertBlock
	}
	
	public class ReconcileStep {
		Instruction step;
		ArrayList<Byte> data = null;
		int blockIndex = -1;
	}
	
	private ArrayList<ReconcileStep> computeDiff(byte[] data) {
		ArrayList<ReconcileStep> steps = new ArrayList<ReconcileStep>();
		int currentCRC = FileSummary.computeAdler32(data, blockSize);
		int intBlockSize = (int) blockSize;
		int currentOffset = 0;	//CurrentOffset + intBlockSize = end index
		HashObject match;
		String localHash;
		ArrayList<Byte> literals = new ArrayList<Byte>();
		ReconcileStep newStep;
		
		int lastBlockIndex = (int) (data.length - (data.length % blockSize));
		
		for(currentOffset = 0; currentOffset < data.length - intBlockSize; currentOffset++) {
			match = receiverHashes.get(currentCRC);
			if(match != null) {
				localHash = FileSummary.computeStrongHash(Arrays.copyOfRange(data, currentOffset, currentOffset + intBlockSize));
				if(localHash.equals(match.strongHash)) {
					if(!literals.isEmpty()) {
						newStep = new ReconcileStep();
						newStep.step = Instruction.insertData;
						newStep.data = literals;
						steps.add(newStep);
						literals = new ArrayList<Byte>();
					}
					
					newStep = new ReconcileStep();
					newStep.step = Instruction.insertBlock;
					newStep.blockIndex = match.blockIndex;
					steps.add(newStep);
					
					currentOffset += intBlockSize;
					currentCRC = FileSummary.computeAdler32(Arrays.copyOfRange(data, currentOffset, currentOffset + intBlockSize), blockSize);
					
					//For loop adds the decrement back in
					currentOffset--;
				}
				else {
					literals.add(data[currentOffset]);
					currentCRC = computeRollingAdler32(data[currentOffset], data[currentOffset + intBlockSize], currentCRC);					
				}
			}
			else {
				literals.add(data[currentOffset]);
				currentCRC = computeRollingAdler32(data[currentOffset], data[currentOffset + intBlockSize], currentCRC);
			}
		}
		//Add in the remaining data
		literals.addAll(bytesToArrayList(Arrays.copyOfRange(data, currentOffset, data.length)));		
		
		//Because these blocks are trivial in size, I really don't care to spend the time trying to match the end piece of the file so let's just send over a carbon copy.
		newStep = new ReconcileStep();
		newStep.step = Instruction.insertData;
		newStep.data = literals;
		steps.add(newStep);		
		
		return steps;
	}
	
	private ArrayList<Byte> bytesToArrayList(byte[] bytes) {
		ArrayList<Byte> array = new ArrayList<Byte>(bytes.length);
		for(int i = 0; i < bytes.length; i++) {
			array.add(bytes[i]);
		}
		return array;
	}
	
	private int computeRollingAdler32(byte out, byte in, int currentChecksum) {
		int A = currentChecksum >> 16;
		int B = currentChecksum & 0x0000ffff;
		
		A -= out;
		A += in;
		B -= blockSize * out;
		B += A;
		B &= 0xffff;
		
		A <<= 16;
		
		return A | B;
	}
	
	
}
