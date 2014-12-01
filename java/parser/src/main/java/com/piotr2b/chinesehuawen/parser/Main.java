package com.piotr2b.chinesehuawen.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main {

	public static int main = 0;
	public static int induced = 0;
	public static int parserError = 0;

	/***
	 * Implements double-keyed dictionary. I need to consider that a character
	 * and its sequence are equivalent. We have the chain String → Integer →
	 * Node so we can query a Node by Integer or by String.
	 */
	// public static HashMap<String, Integer> alias = new HashMap<>();
	// public static HashMap<Integer, Node> dictionary = new HashMap<>();
	public static int errorType1 = 0;
	public static int errorType2 = 0;
	public static int errorType3 = 0;
	public static int errorType4 = 0;

	public static void main(String[] args) {

		// Jcommander

		parseNode("⿱一⿰⿵冂丶⿵冂丶");

		if (args.length >= 2) {
			switch (args[1]) {
			case "node":
				// We could use knowledge of local database
				break;
			case "network":
				// Main case
				break;
			default:
				break;
			}
		}

		Substrate substrate = new Substrate();
		String idsPath = "../../data/ids/chise/ids/";
		String exportPath = "../../gephi/files/";

		ArrayDeque<File> files = new ArrayDeque<File>();

		// More to be found here: http://www.chise.org/ids/index.html or here
		// http://git.chise.org/gitweb/?p=chise/ids.git;a=tree
		// Order should better not matter ~
		files.addLast(new File("test.txt"));

		// files.addLast(new File(idsPath + "IDS-UCS-Basic.txt"));
		// files.addLast(new File(idsPath + "IDS-UCS-Ext-A.txt"));
		// files.addLast(new File(idsPath + "IDS-UCS-Compat.txt"));
		// files.addLast(new File(idsPath + "IDS-UCS-Ext-B-1.txt"));
		// files.addLast(new File(idsPath + "IDS-UCS-Ext-B-2.txt"));
		// files.addLast(new File(idsPath + "IDS-UCS-Ext-B-3.txt"));
		// files.addLast(new File(idsPath + "IDS-UCS-Ext-B-4.txt"));
		// files.addLast(new File(idsPath + "IDS-UCS-Ext-B-5.txt"));
		// files.addLast(new File(idsPath + "IDS-UCS-Ext-B-6.txt"));
		// files.addLast(new File(idsPath + "IDS-UCS-Ext-C.txt"));
		// files.addLast(new File(idsPath + "IDS-UCS-Ext-D.txt"));
		// files.addLast(new File(idsPath + "IDS-UCS-Ext-E.txt"));
		// files.addLast(new File(idsPath + "IDS-UCS-Compat-Supplement.txt"));

		Parser fparser = null;
		try {
			fparser = new Parser(files, 50);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

		fparser.lines().forEach(row -> {
			Node node = new Node(row.getCharacter(), row.getSequence());
			Main.main++;
			System.out.print(Main.main + ", ");
			substrate.flatten(node);
			Database.insert(node);
		});

		System.out.print("");

		try {
			substrate.exportGephi(exportPath, Node.TreeType.Parsed);
			substrate.exportPdf(exportPath, Node.TreeType.Parsed);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		int cardinality = 0;
		for (Node n : substrate.values()) {
			cardinality += n.getCardinality();
		}

		// 灣 is used as example in README.md but currently seems not correctly
		// processed. To be investigated ~

		System.out.println();
		System.out.println(format("Main nodes  ", Main.main, Main.main));
		System.out.println(format("Set size    ", substrate.size(), Main.main));
		System.out.println(format("Cardinalité ", cardinality, substrate.size()));
		System.out.println(format("Exception   ", Main.parserError, Main.main));
		System.out.println(format("    Type 1  ", Main.errorType1, Main.parserError));
		System.out.println(format("    Type 2  ", Main.errorType2, Main.parserError));
		System.out.println(format("    Type 3  ", Main.errorType3, Main.parserError));
		System.out.println(format("    Type 4  ", Main.errorType4, Main.parserError));

		System.exit(0);
	}

	private static void parseNode(String ids) {
		Substrate s = new Substrate();
		Node node = s.flatten(new Node(null, ids));
		System.out.println(node);

		try {
			s.exportGephi("../../gephi/files/", Node.TreeType.Parsed);
			s.exportPdf("../../gephi/files/", Node.TreeType.Parsed);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private static String format(String label, int field, int total) {
		Double percentage = 100 * field / (double) total;
		return label + ": " + field + " (" + (new DecimalFormat("#.##")).format(percentage) + " %)";
	}
}
