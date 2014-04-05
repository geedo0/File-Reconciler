package ec504project.application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class DifferentialServer {
	File				fil;
	public String		text;
	private String[]	blobs;
	int					numBlobs;
	Capsule				myCapsule;		//contains a summarized version of the string
	Capsule				theirCapsule;

	public int getFileLength(File f) {
		int size = 0;
		try (BufferedReader br = new BufferedReader(new FileReader(f))) {
			int chr = 0;
			while (chr != -1) {
				chr = br.read();
				size++;
			}
		}
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return size - 1;
	}

	public String getHash(String sb) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.reset();
		md.update(sb.getBytes());

		byte[] digest = md.digest();
		BigInteger bigInt = new BigInteger(1, digest);
		String hashtext = bigInt.toString(16);
		return hashtext;
	}

	private void blobify() {
		int blobSize = text.length() / numBlobs;
		blobs = text.split("(?<=\\G.{" + blobSize + "})");// split the string into numBlobs different parts
	}

	public DifferentialServer(File f, int parts) {
		fil = f;
		numBlobs = parts;
		try (BufferedReader br = new BufferedReader(new FileReader(f))) {
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
				line = br.readLine();
			}
			text = sb.toString();
		}
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void sendCapsule() {
		//send the capsule to another server running this same code
	}

	public void recieveCapsule() {
		theirCapsule = new Capsule();
	}

	private void compareCapsules() {
		if (myCapsule.getLength() != theirCapsule.getLength()) {//The strings are off by n<5 characters, probably need new hashes
			//still should try to compare hashes assuming the difference is on th end of the file
			//if the difference is at the beginning, we need a new method or need to compute hashes with character offsets 
		}
		else {//same length, try comparing the hashes
			int[] nonMatchingBlobs = new int[numBlobs];// 1 means the blobs didnt match
			Arrays.fill(nonMatchingBlobs, 0);
			String[] myHashes = myCapsule.getHashes();
			String[] theirHashes = myCapsule.getHashes();
			int count = 0;
			for (int i = 0; i < numBlobs; i++) {
				if (!myHashes[i].equals(theirHashes[i])) {
					nonMatchingBlobs[i] = 1;
					count++;
				}
			}

			if (count == 0) {
				//all blobs match, the strings are the same 
			}
			else if (count == numBlobs) {//all blobs mismatch, something is off

			}
			else {//only some blobs mismatch, that means we only need to request or send those which did

			}


		}
	}

	public void encapsulate() throws NoSuchAlgorithmException {
		this.blobify();
		String[] hashes = new String[numBlobs];
		int[] blobLens = new int[numBlobs];
		String blob;

		for (int i = 0; i < numBlobs; i++) {
			blob = blobs[i];
			blobLens[i] = blob.length();
			hashes[i] = getHash(blob);
		}

		myCapsule = new Capsule(hashes, blobLens);


	}
}
