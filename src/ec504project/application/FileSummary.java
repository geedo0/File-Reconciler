package ec504project.application;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class FileSummary {
	public ArrayList<byte[]> fileBlocks;
	public ArrayList<HashObject> blockHashes;
	public int blockSize;
	
	public FileSummary(File input) {
		fileBlocks = createBlocks(input);
		blockHashes = computeHashes(fileBlocks);
	}
	
	public class HashObject {
		public int weakHash;		//Rolling Adler-32 hash
		public String strongHash;	//MD5 hash
	}
	
	private int computeBlockSize(File input) {
		//Computes optimal block size based on rsync algorithm.
		//Replace 5 with the expected number of edits in a file
		return (int) Math.sqrt((double) ((input.length() * 24) / 5));
	}
	
	//Computes a single block of the Adler-32 Hash
	public static int computeAdler32(byte[] data, long length) {
		long A = 0;
		long B = 0;
		
		long addlerMod = 65_536;
		for(int i = 0; i < length; i++) {
			if(i >= data.length)
				A += 0;
			else
				A += data[i];
			B += A;
		}
		A %= addlerMod;
		B %= addlerMod;
		int retVal = (int) ((A << 16) | B);
		
		//Return format: [A 31:16][B 15:0]
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
			
		} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
		
		return blocks;
	}
	
	private ArrayList<HashObject> computeHashes(ArrayList<byte[]> blocks) {
		ArrayList<HashObject> hashes = new ArrayList<HashObject>(blocks.size());
		HashObject newHash;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
		
			for(int i = 0; i < blocks.size(); i++) {
				md.reset();
				md.update(blocks.get(i));
				
				newHash = new HashObject();
				newHash.weakHash = computeAdler32(blocks.get(i), blockSize);
				newHash.strongHash = md.digest().toString();;
				hashes.add(newHash);
			}
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return hashes;
	}
}