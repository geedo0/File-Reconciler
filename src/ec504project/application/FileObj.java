package ec504project.application;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class FileObj {
	
	class FileListElement {	
		public File filePath;
		public String fileHash;
	}
	
	ArrayList<FileListElement> fileList;
	File myPath;
	
	public FileObj(File path) {
		this.fileList = generateFileList(path);
		myPath = path;
	}
	
	public ArrayList<Integer> generateDiffList(ArrayList<FileListElement> senderList) {
		ArrayList<Integer> DiffList = new ArrayList<Integer>();
		File f;
		
		for(int ii=0; ii < fileList.size(); ii++){
			int index = 0;
			if(senderList.get(ii).filePath.getName().compareTo(fileList.get(ii).filePath.getName()) == 0){
				if(Checksum.verifyChecksum(senderList.get(ii).fileHash, fileList.get(ii).fileHash) == false){
					System.out.println("Found a diff! Added "+ senderList.get(ii).filePath.getName() +  " to difflist");
					DiffList.add(ii);
				}
				else{
					System.out.println("Matching file and hash!: "+ senderList.get(ii).filePath.getName());
				}
			} else{
				System.out.println("There is a mismatch during filelist diff!");
				index = fileSearch(senderList.get(ii).filePath.getName(),fileList);
				if(index == -1){
					System.out.println("File "+senderList.get(ii).filePath.getName()+" does not exist locally!!");
					if(senderList.get(ii).filePath.length() <= 5){
						System.out.println("File size= "+senderList.get(ii).filePath.length());
						System.out.println("Adding file locally: "+MainApplication.inputPath+File.separator+
								senderList.get(ii).filePath.getName());
						
						f=new File(MainApplication.inputPath+File.separator+
								senderList.get(ii).filePath.getName());
						try {
							f.createNewFile();
						} catch (IOException e) {
							e.printStackTrace();
						}
						//System.out.println("myPath= "+senderList.get(ii).filePath.getPath()+"!!");
						FileObj receiverFileList = new FileObj(myPath);
						receiverFileList.generateDiffList(senderList);
						System.exit(3);
					}
					else{
						System.err.println("Error, file: '"+senderList.get(ii).filePath.getName()+
								" cannot be added to the list");
						System.exit(-1);
					}
				}
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
	
	private int fileSearch(String fileName, ArrayList<FileListElement> senderList) {
		for(int index=0; index<senderList.size(); index++){
			if(senderList.get(index).filePath.getName().compareTo(fileName) == 0){
				return index;
			}
		}
		return -1;
	}
	
}
