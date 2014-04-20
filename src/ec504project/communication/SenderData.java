package ec504project.communication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class SenderData implements Serializable{
	public int fileIndex;
	public int blockSize;
	public HashMap<Integer, ArrayList<HashObject>> hashes;
}
