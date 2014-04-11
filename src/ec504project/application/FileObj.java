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
		return new ArrayList<fileListElement>();
	}
	
	private ArrayList<fileListElement> generateFileList(File path) {
		ArrayList<fileListElement> list = new ArrayList<fileListElement>();
		fileListElement listElement;
		
		File[] filesInPath = path.listFiles();
		
		for(int i = 0; i < filesInPath.length; i++)
		{
			listElement = new fileListElement();
			listElement.fileHash = Checksum.calcChecksum(filesInPath[i].getAbsolutePath());
			listElement.filePath = filesInPath[i];
			
			list.add(listElement);
		}
		return list;
	}
	
}
