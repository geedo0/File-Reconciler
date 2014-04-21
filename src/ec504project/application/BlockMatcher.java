package ec504project.application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import ec504project.communication.HashObject;


public class BlockMatcher {
	private HashMap<Integer, ArrayList<HashObject>> receiverHashes;
	public ArrayList<ReconcileStep> receiverSteps;
	
	private int blockSize;
	
	
	public BlockMatcher(File input, HashMap<Integer, ArrayList<HashObject>> receiverHashes, int blockSize) {
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
	
	
	private ArrayList<ReconcileStep> computeDiff(byte[] data) {
		ArrayList<ReconcileStep> steps = new ArrayList<ReconcileStep>();
		int currentCRC = FileSummary.computeAdler32(data, blockSize);
		int intBlockSize = (int) blockSize;
		int currentOffset = 0;	//CurrentOffset + intBlockSize = end index
		ArrayList<HashObject> possibleMatches;
		String localHash;
		ArrayList<Byte> literals = new ArrayList<Byte>();
		ReconcileStep newStep;
		int index;
		
		for(currentOffset = 0; currentOffset < data.length - intBlockSize; currentOffset++) {
			possibleMatches = receiverHashes.get(currentCRC);
			if(possibleMatches != null) {
				localHash = FileSummary.computeStrongHash(Arrays.copyOfRange(data, currentOffset, currentOffset + intBlockSize));
				if((index = searchStrongHash(possibleMatches, localHash)) > -1) {
					if(!literals.isEmpty()) {
						newStep = new ReconcileStep();
						newStep.step = Instruction.insertData;
						newStep.data = ReconcileFile.arrayListToByteArray(literals);
						steps.add(newStep);
						literals = new ArrayList<Byte>();
					}
					
					newStep = new ReconcileStep();
					newStep.step = Instruction.insertBlock;
					newStep.blockIndex = possibleMatches.get(index).blockIndex;
					steps.add(newStep);
					
					currentOffset += intBlockSize;
					currentCRC = FileSummary.computeAdler32(Arrays.copyOfRange(data, currentOffset, currentOffset + intBlockSize), blockSize);
					
					//For loop adds the decrement back in
					currentOffset--;
				}
				else {
					literals.add(data[currentOffset]);
					currentCRC = computeRollingAdler32(data[currentOffset], data[currentOffset + intBlockSize], currentCRC,intBlockSize);					
				}
			}
			else {
				literals.add(data[currentOffset]);
				currentCRC = computeRollingAdler32(data[currentOffset], data[currentOffset + intBlockSize], currentCRC,intBlockSize);
			}
		}
		//Add in the remaining data
		literals.addAll(bytesToArrayList(Arrays.copyOfRange(data, currentOffset, data.length)));		
		
		//Because these blocks are trivial in size, I really don't care to spend the time trying to match the end piece of the file so let's just send over a carbon copy.
		newStep = new ReconcileStep();
		newStep.step = Instruction.insertData;
		newStep.data = ReconcileFile.arrayListToByteArray(literals);
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
	
	public static int computeRollingAdler32(byte out, byte in, int currentChecksum, int blocksize) {
		
		int addlerMod = 65521;
		
		int A = currentChecksum & 0x0000FFFF;
		int B = (currentChecksum >> 16) & 0x0000FFFF;
		
		
		A =  (A - out + in-1) % addlerMod;
		B =  (B -(blocksize*out) + A-1) % addlerMod;
		
		int retVal = (int) ((B << 16) | A);
		
		//Return format: [B 31:16][A 15:0]
		return retVal;
	}
	
	private int searchStrongHash(ArrayList<HashObject> list, String hash) {
		for(int i = 0; i < list.size(); i++) {
			if(list.get(i).strongHash.contentEquals(hash)) {
				return i;
			}
		}
		return -1;
	}
}
