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
		/*
		 * Gerardo 4/5/14: We are dependent on the existence of new line characters and the data being roughly delimited by them.
		 * What happens the BufferedReader.readLine method when the first line is a 100MB ASCII string?
		 * What happens when the data is binary?
		 */
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
		if (myCapsule.getLength() != theirCapsule.getLength()) {
			/*
			 * Gerardo 4/5/14: This is not a safe assumption, I think we need to rethink our strategy for insertions and deletions.
			 * It can take a rather large amount of time for us to determine the actual location of the insertion/deletion. Also, what happens,
			 * when they are spread out evenly across the file? I can insert at the beginning, in the middle, and delete at the end and really
			 * screw things up. One suggestion would be to use a divide and conquer strategy, but that's still prone to uniformly distributed
			 * modifications. Ideally we need to take advantage of the fact that we know how many changes there will be relative to the file size
			 * and take that into account when splitting.
			 */
			
			//The strings are off by n<5 characters, probably need new hashes
			//still should try to compare hashes assuming the difference is on the end of the file
			//if the difference is at the beginning, we need a new method or need to compute hashes with character offsets 
		}
		else {
			/*
			 * Gerardo 4/5/14: What happens when I insert and delete the same number of characters? This algorithm will end up with terrible performance.
			 */
			
			//same length, try comparing the hashes
			//Why not a Boolean array? Although they have the same memory footprint it's just better practice
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
