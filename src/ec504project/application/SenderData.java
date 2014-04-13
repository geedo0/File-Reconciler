package ec504project.application;

import java.io.Serializable;
import java.util.HashMap;

public class SenderData implements Serializable{
	int fileIndex;
	int blockSize;
	HashMap<Integer, HashObject> hashes;
}
