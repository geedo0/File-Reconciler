package ec504project.application;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class FileObj {
	
	ArrayList<FileListElement> fileList;
	
	public FileObj(File path) {
		this.fileList = generateFileList(path);
	}
	
	public ArrayList<Integer> generateDiffList(ArrayList<FileListElement> senderList) {
		ArrayList<Integer> DiffList = new ArrayList<Integer>();
		//Collections.sort(senderList, new fileComparator());
		//Collections.sort(fileList, new fileComparator());
		
		for(int ii=0; ii < fileList.size(); ii++){
			if(senderList.get(ii).filePath.getName().compareTo(fileList.get(ii).filePath.getName()) == 0){
				if(Checksum.verifyChecksum(senderList.get(ii).fileHash, fileList.get(ii).fileHash) == false){
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
	
	private ArrayList<FileListElement> generateFileList(File path) {
		ArrayList<FileListElement> list = new ArrayList<FileListElement>();
		FileListElement listElement;
		
		File[] filesInPath = path.listFiles();
		
		for(int i = 0; i < filesInPath.length; i++)
		{
			listElement = new FileListElement();
			listElement.fileHash = Checksum.calcChecksum(filesInPath[i].getAbsolutePath());
			listElement.filePath = filesInPath[i];
			
			list.add(listElement);
		}
		return list;
	}
	
}
