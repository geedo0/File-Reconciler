package ec504project.application;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

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
	
	public static ArrayList<Integer> generateDiffList(ArrayList<fileListElement> senderList, ArrayList<fileListElement> receiverList) {
		ArrayList<Integer> DiffList = new ArrayList<Integer>();
		Collections.sort(senderList, new fileComparator());
		Collections.sort(receiverList, new fileComparator());
		
		for(int ii=0; ii < receiverList.size(); ii++){
			if(senderList.get(ii).filePath.getName().compareTo(receiverList.get(ii).filePath.getName()) == 0){
				if(Checksum.verifyChecksum(senderList.get(ii).fileHash, receiverList.get(ii).fileHash) == false){
					System.out.println("Found a diff! Added to difflist");
					DiffList.add(ii);
				}
				else{
					System.out.println("Matching file and hash!");
				}
			} else{
				System.out.println("There is a mismatch during filelist diff!");
			}
		}
		
		return DiffList; 
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
