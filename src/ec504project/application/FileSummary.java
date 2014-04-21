package ec504project.application;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

import ec504project.communication.HashObject;
import ec504project.communication.SenderData;

public class FileSummary {
	public ArrayList<byte[]> fileBlocks;
	public HashMap<Integer, HashObject> blockHashes;
	public int blockSize;
	
	public FileSummary(File input) {
		fileBlocks = createBlocks(input);
		blockHashes = computeHashes(fileBlocks);
	}
	

	

	
	private int computeBlockSize(File input) {
		//Computes optimal block size based on rsync algorithm.
		//Replace 5 with the expected number of edits in a file
		return (int) Math.sqrt((double) ((input.length() * 24) / 5));
	}
	
	//Computes a single block of the Adler-32 Hash
	public static int computeAdler32(byte[] data, long length) {
		int A = 1;
		int B = 0;
		
		int addlerMod = 65521;
		for(int i = 0; i < length; i++) {
	        A = (A + data[i]) % addlerMod;
	        B = (B + A) % addlerMod;
		}

		int retVal = (int) ((B << 16) | A);
		
		//Return format: [B 31:16][A 15:0]
		return retVal;
	}
	
	private ArrayList<byte[]> createBlocks(File input) {
		blockSize = computeBlockSize(input);
		long numBlocks = input.length() / blockSize;
		int tmp = 0;
		
		ArrayList<byte[]> blocks = new ArrayList<byte[]>((int) numBlocks + 1);
		try {
			byte[] buffer = new byte[blockSize];
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(input));
			
			while((tmp = bis.read(buffer)) > 0) {
				blocks.add(buffer);
				if(tmp != blockSize)
					break;
				else
					buffer = new byte[blockSize];
			}
			bis.close();
			
		} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
				
		
		return blocks;
	}
	
	private HashMap<Integer, HashObject> computeHashes(ArrayList<byte[]> blocks) {
		HashMap<Integer, HashObject> hashes = new HashMap<Integer, HashObject>();
		HashObject newHash;
		int weakHash;
		for(int i = 0; i < blocks.size(); i++) {
			
			newHash = new HashObject();
			newHash.blockIndex = i;
			newHash.strongHash = computeStrongHash(blocks.get(i));
			weakHash = computeAdler32(blocks.get(i), blockSize);
			hashes.put(weakHash, newHash);
		}
		return hashes;
	}
	
	public static String computeStrongHash(byte[] data) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			md.update(data);
			return new BigInteger(1, md.digest()).toString(16);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "ERROR HASH";
	}

	public SenderData getSenderData(int index) {
		SenderData out = new SenderData();
		out.fileIndex = index;
		out.blockSize = this.blockSize;
		out.hashes = blockHashes;
		return out;
	}
}
