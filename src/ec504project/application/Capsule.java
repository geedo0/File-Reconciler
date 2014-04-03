package ec504project.application;

import java.io.Serializable;

public class Capsule implements Serializable{
	private String fileName;
	private String hashes[];
	private int strLen;
	private int[] blobLens;
	
	public Capsule(){//FOR TESTING ONLY
		
	}
	
	public Capsule(String[] hs, int[] lens){
		hashes=hs;
		blobLens=lens;
		
		int sum=0;
		for (int i: lens){
			sum+=i;
		}
		strLen=sum;
	}
	
	public int getLength(){
		return this.strLen;
	}
	
	public String[] getHashes(){
		return this.hashes;
	}
	
}
