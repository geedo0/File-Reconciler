package ec504project.application;

import java.io.Serializable;
import ec504project.application.BlockMatcher.Instruction;

public class ReconcileStep implements Serializable{
	Instruction step;
	byte[] data;
	int blockIndex = -1;
}
