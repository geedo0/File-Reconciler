package ec504project.application;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import ec504project.communication.FileListElement;

public class FileObj {
	ArrayList<FileListElement> fileList;
	File myPath;
	
	public FileObj(File path) {
		this.fileList = generateFileList(path);
		myPath = path;
	}
	
	public ArrayList<Integer> generateDiffList(ArrayList<FileListElement> senderList) {
		ArrayList<Integer> DiffList = new ArrayList<Integer>();
		File f;
		int index = 0;
		
		if(senderList.size() == 0){
			for(int jj=fileList.size(); jj > 0; jj--){				
				System.out.println("Removing file: "+ fileList.get(jj).filePath);
				fileList.get(jj).filePath.delete();
			}
			return DiffList;
		}
		
		for(int jj=0; jj < fileList.size(); jj++){
			fileList.get(jj).fileMatch = false;
		}
		
		for(int ii=0; ii < senderList.size(); ii++){
			index = fileSearch(senderList.get(ii).filePath.getName(),fileList);
			if (index >= 0){
				fileList.get(index).fileMatch = true;
				if(Checksum.verifyChecksum(senderList.get(ii).fileHash, fileList.get(index).fileHash) == false){
					System.out.println("Found a diff! Added "+ senderList.get(ii).filePath.getName() +  " to difflist");
					DiffList.add(ii);
				}
				else{
					System.out.println("Matching file and hash!: "+ senderList.get(ii).filePath.getName());
				}
			} 

			if(index == -1){
				System.out.println("File "+senderList.get(ii).filePath.getName()+" does not exist locally!!");
				if(senderList.get(ii).filePath.length() <= 5){
					System.out.println("File size= "+senderList.get(ii).filePath.length());
					System.out.println("Adding file locally: "+myPath+File.separator+
							senderList.get(ii).filePath.getName());
					
					f=new File(myPath+File.separator+
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
		
		for(int jj=fileList.size()-1; jj >= 0; jj--){
			if(fileList.get(jj).fileMatch == false){
				System.out.println("Removing file: "+ fileList.get(jj).filePath);
				fileList.get(jj).filePath.delete();
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
	
	private int fileSearch(String fileName, ArrayList<FileListElement> List) {
		if(List.size() == 0){
			return -1;
		}
		for(int index=0; index<List.size(); index++){
			if(List.get(index).filePath.getName().compareTo(fileName) == 0){
				return index;
			}
		}
		return -1;
	}
	
}
