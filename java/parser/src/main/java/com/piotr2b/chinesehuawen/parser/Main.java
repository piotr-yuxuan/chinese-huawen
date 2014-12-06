package com.piotr2b.chinesehuawen.parser;

import static com.piotr2b.chinesehuawen.entities.Tables.SINOGRAM;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import com.beust.jcommander.JCommander;
import com.piotr2b.chinesehuawen.parser.Node.TreeType;

public class Main {

	public static void main(String[] args) {

		database();

		JCommanderParser arguments = new JCommanderParser();
		new JCommander(arguments, args);

		Substrate substrate = new Substrate();

		if (arguments.getDirect() != null && !arguments.getDirect().contentEquals("")) {
			getDirect(substrate, arguments.getDirect());
		} else if (arguments.getFiles() != null && !arguments.getFiles().contentEquals("")) {
			getFiles(substrate, arguments.getFiles());
		}

		if (arguments.getOutput() != null && arguments.getOutput().contentEquals("files")) {
			outFiles(substrate);
		} else if (arguments.getOutput() != null && arguments.getOutput().contentEquals("visual")) {
			outVisual(substrate);
		} else if (arguments.getOutput() != null && arguments.getOutput().contentEquals("terminal")) {
			outTerminal(substrate);
		}

		Scanner sc = new Scanner(System.in);
		System.out.println("Appuyer sur entrée pour terminer");
		sc.nextLine();
		sc.close();
		System.exit(0);
	}

	private static void database() {

		Connection conn = null;

		String userName = "huawen";
		String password = "huawen";
		String url = "jdbc:mysql://localhost:3306/huawen";

		try {
			new org.mariadb.jdbc.Driver();
			conn = DriverManager.getConnection(url, userName, password);
			DSLContext create = DSL.using(conn, SQLDialect.MYSQL);

			// create.insertInto(SINOGRAM, SINOGRAM.CP, SINOGRAM.INDUCED)//
			// .values("U+0001", (byte) 1)//
			// .values("U+0002", (byte) 2)//
			// .execute();

			// Use stream() not to use java 8-like jooq embedded methods.
			ArrayList<Node> result = create.select().from(SINOGRAM).fetch().stream()//
					.map(Node::new)//
					.collect(Collectors.toCollection(ArrayList::new));

			for (Node r : result) {
				System.out.print(r);
			}
		} catch (Exception e) {
			// For the sake of this tutorial, let's keep exception handling
			// simple
			e.printStackTrace();
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException ignore) {
				}
			}
		}
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

			fparser = new Parser(files, Integer.MAX_VALUE);
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
