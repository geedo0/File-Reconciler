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
		boolean success = false;
		
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
				if(senderList.get(index).filePath.isDirectory()){
					break;
				}
				if(Checksum.verifyChecksum(senderList.get(ii).fileHash, fileList.get(index).fileHash) == false){
					DiffList.add(ii);
				}
				else{
				}
			} 

			if(index == -1){				
				if(senderList.get(ii).filePath.isFile()){
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
				else if(senderList.get(ii).filePath.isDirectory()){
					success = (new File(myPath+File.separator+senderList.get(ii).filePath.getName())).mkdirs();
					if (!success) {
						System.out.println("Error: Could not create directory");
					    System.exit(-1);
					}
					restart = true;	
				}
			}
		}
		
		for(int jj=fileList.size()-1; jj >= 0; jj--){
			if(fileList.get(jj).fileMatch == false){
				if(fileList.get(jj).filePath.isFile()){
					fileList.get(jj).filePath.delete();
				}
				else if(fileList.get(jj).filePath.isDirectory()){
					deleteFolder(fileList.get(jj).filePath);
				}
				
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
				if(filesInPath[i].isFile()){
					listElement.fileHash = Checksum.calcChecksum(filesInPath[i].getAbsolutePath());
				}
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
	
	private static void deleteFolder(File folder) {
	    File[] files = folder.listFiles();
	    if(files!=null) { //some JVMs return null for empty dirs
	        for(File f: files) {
	            if(f.isDirectory()) {
	                deleteFolder(f);
	            } else {
	                f.delete();
	            }
	        }
	    }
	    folder.delete();
	}
	
}
