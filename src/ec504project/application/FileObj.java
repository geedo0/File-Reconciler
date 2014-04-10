package ec504project.application;

import java.io.File;

public class FileObj implements Comparable<FileObj>{
	private File alpha;
	private String delta;
	
	public FileObj(File beta, String gamma) {
		this.alpha = beta;
		this.delta = gamma;
	}
	
	public FileObj(){
		this(null,null);
	}
	
	public FileObj(File beta){
		this.alpha = beta; 
	}
	
	public boolean isFile(){
		if (alpha.isFile()){ return true; }
		else return false;
	}
	
	public boolean isDirectory(){
		if(alpha.isDirectory()) {return true;}
		else return false;
	}
	
	public String toString(){
		return alpha.toString();
	}

	@Override
	public int compareTo(FileObj myFile) {
		int result = alpha.getName().compareTo(myFile.getFile().getName());
		return result;
	}

	public File getFile() {
		return alpha;
	}

	public void setFile(File alpha) {
		this.alpha = alpha;
	}
	
	public String getHash() {
		return delta;
	}

	public void setHash(String delta) {
		this.delta = delta;
	}

}
