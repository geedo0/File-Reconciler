package test;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import ec504project.application.DiffEngine;

public class DiffTest {
	public static void Test(){
		File f1= new File("alpha");
		File f2 = new File("alphaRandomTrim");
		DiffEngine de = new DiffEngine(f1,f2);
		try {
			de.run();
		}
		catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
