package ec504project.application;

import java.io.Serializable;
import java.util.ArrayList;

import ec504project.application.BlockMatcher.Instruction;

public class ReconcileStep implements Serializable{
	Instruction step;
	ArrayList<Byte> data = null;
	int blockIndex = -1;
}
