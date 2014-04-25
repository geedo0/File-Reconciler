package ec504project.application;

import java.io.File;
import java.io.FileOutputStream;
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
		byte dataToWrite = (byte) 12345; 
		int index = 0;
		boolean restart = false;
		
		if(myPath.isFile() && senderList.size() > 1){
			System.out.println("ERROR: Mismatch between comparing file and directory");
			System.exit(-1);
		}
		
		if(myPath.isFile()){
			if(!myPath.getName().equals(senderList.get(0).filePath.getName()))
			{
				System.out.println("ERROR: Files names do not match!");
				System.exit(-1);
			}
		}
		
		if(senderList.size() == 0){
			if(fileList.size() > 0){
				for(int jj=fileList.size()-1; jj >= 0; jj--){				
					System.out.println("Removing file: "+ fileList.get(jj).filePath);
					fileList.get(jj).filePath.delete();
				}
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
				System.out.println("File size= "+senderList.get(ii).filePath.length());
				System.out.println("Adding file locally: "+myPath+File.separator+senderList.get(ii).filePath.getName());
				
				try {
					FileOutputStream out = new FileOutputStream(myPath+File.separator+senderList.get(ii).filePath.getName());
					out.write(dataToWrite);
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				DiffList.add(ii);
				restart = true;
			}
		}
		
		for(int jj=fileList.size()-1; jj >= 0; jj--){
			if(fileList.get(jj).fileMatch == false){
				System.out.println("Removing file: "+ fileList.get(jj).filePath);
				fileList.get(jj).filePath.delete();
			}
		}
		
		if(restart == true){
			fileList = generateFileList(myPath);
		}
		return DiffList;
		
	}

	private ArrayList<FileListElement> generateFileList(File path) {
		ArrayList<FileListElement> list = new ArrayList<FileListElement>();
		FileListElement listElement;
		
		if(path.isFile()) {
			listElement = new FileListElement();
			listElement.fileHash = Checksum.calcChecksum(path.getAbsolutePath());
			listElement.filePath = path;
			
			list.add(listElement);
		}
		else if(path.isDirectory()) {
			File[] filesInPath = path.listFiles();
			
			for(int i = 0; i < filesInPath.length; i++)
			{
				listElement = new FileListElement();
				listElement.fileHash = Checksum.calcChecksum(filesInPath[i].getAbsolutePath());
				listElement.filePath = filesInPath[i];
				list.add(listElement);
			}
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
