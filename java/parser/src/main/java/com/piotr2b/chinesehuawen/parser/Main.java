package com.piotr2b.chinesehuawen.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayDeque;
import java.util.Scanner;

import com.beust.jcommander.JCommander;
import com.piotr2b.chinesehuawen.parser.Node.TreeType;

public class Main {

	public static void main(String[] args) {

		JCommanderParser arguments = new JCommanderParser();
		new JCommander(arguments, args);

		Substrate substrate = new Substrate();

		if (arguments.getDirect() != null && !arguments.getDirect().contentEquals("")) {
			getDirect(substrate, arguments.getDirect());
		} else if (arguments.getFiles() != null && !arguments.getFiles().contentEquals("")) {
			getDirect(substrate, arguments.getFiles());
		}

		if (arguments.getOutput() != null && arguments.getOutput().contentEquals("files")) {
			outFiles(substrate);
		} else if (arguments.getOutput() != null && arguments.getOutput().contentEquals("visual")) {
			outVisual(substrate);
		} else if (arguments.getOutput() != null && arguments.getOutput().contentEquals("terminal")) {
			outTerminal(substrate);
		}

		Scanner sc = new Scanner(System.in);
		sc.nextLine();
		sc.close();
		System.exit(0);
	}

	public static void getDirect(Substrate s, String input) {
		s.flatten(new Node(null, input));
	}

	public static void getFiles(Substrate s, String input) {
		Parser fparser = null;
		try {
			ArrayDeque<File> files = new ArrayDeque<File>();
			String[] array = input.split(",");
			for (String st : array) {
				files.addLast(new File(st));
			}

			fparser = new Parser(files, 50);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

		fparser.lines().forEach(row -> {
			Node node = new Node(row.getCharacter(), row.getSequence());
			s.flatten(node);
		});
	}

	public static void outFiles(Substrate s) {
		try {
			s.exportFiles(".", TreeType.Parsed);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void outVisual(Substrate s) {
		s.exportVisual(TreeType.Parsed);
	}

	public static void outTerminal(Substrate s) {
		s.exportSet(TreeType.Parsed).stream().forEach(x -> System.out.println(x));
	}
}
