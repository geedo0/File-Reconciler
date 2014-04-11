package ec504project.application;

import java.io.File;
import java.util.ArrayList;

public class FileObj {

	class fileListElement {	
		public int index;
		public File filePath;
		public String fileHash;
	}
	
	ArrayList<fileListElement> fileList;
	
	public FileObj(File path) {
		this.fileList = generateFileList(path);
	}
	
	public static ArrayList<fileListElement> generateDiffList(ArrayList<fileListElement> senderList) {
		
	}
	
	private ArrayList<fileListElement> generateFileList(File path) {
		ArrayList<fileListElement> list = new ArrayList<fileListElement>();
		fileListElement listElement;
		
		File[] filesInPath = path.listFiles();
		
		for(int i = 0; i < filesInPath.length; i++)
		{
			listElement = new fileListElement();
			listElement.fileHash = Checksum.calcChecksum(filesInPath[i].getAbsolutePath());
			listElement.filePath = filesInPath[1];
			
			list.add(listElement);
		}
		
		return list;
	}
	
	
	
	private File alpha;
	private String delta;
	
	public FileObj(File beta, String gamma) {
		this.alpha = beta;
		this.delta = gamma;
	}
	
	public FileObj(){
		this(null,null);
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
	else if(args[0].contentEquals("-path")){
		File inputPath = new File(args[1]);
		if(!inputPath.exists()) {
			System.out.println("Error: Invalid input path:\t" + args[1]);
			return;
		}
		if(!inputPath.isDirectory()) {
			System.out.println("Error: Input is not a path:\t" + args[1]);
			return;
		}
		
		File[] filesInPath = inputPath.listFiles();
		FileObj[] fileList = new FileObj[filesInPath.length];
		
		for(int jj=0; jj<filesInPath.length; jj++)
		{
			fileList[jj] = new FileObj();
			fileList[jj].setFile(filesInPath[jj]);
			
			if(fileList[jj].isFile())
			{
				fileList[jj].setHash(Checksum.calcChecksum(fileList[jj].toString()));
				//System.out.println("File: ["+fileList[jj].toString()+"]["+fileList[jj].getDelta()+"]");
			} else if (fileList[jj].isDirectory()){
				fileList[jj].setHash(null);
				//System.out.println("Folder: ["+fileList[jj].toString()+"]["+fileList[jj].getDelta()+"]");
			}
		}
		
		Arrays.sort(fileList);
		/*for(FileObj file: fileList){
			System.out.println(file);
		}*/

	}
	/* Insert a way to send 2D array to other computer. 
	 * Insert function of being a host, which includes getting the hash values of each file
	 *    and comparing it to the receiver's values.
	 
	timer.stop();
	
	System.out.println("Bandwidth used:\t" + bandwidthUsed + " bytes");
	timer.prettyPrintTime();
	 */
}
