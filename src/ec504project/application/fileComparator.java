package ec504project.application;

import java.io.File;
import java.util.Comparator;

class fileComparator implements Comparator<Object>{
	@Override
	public int compare(Object file1, Object file2) {
		String fileName1 = ((File) file1).getName();
		String fileName2 = ((File) file2).getName();
		
		if(fileName1.compareTo(fileName2) > 0){
			return 1;
		}
		else if(fileName1.compareTo(fileName2) < 0){
			return -1;
		}
		else
			return 0;
	}
}