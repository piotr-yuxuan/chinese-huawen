package parser;

import java.util.ArrayList;

import org.jsefa.csv.annotation.CsvDataType;
import org.jsefa.csv.annotation.CsvField;

import entities.Allography;
import entities.Sinogram;
import entities.Structure;

/**
 * @author caocoa
 *
 */
@CsvDataType()
public class Row {

	@CsvField(pos = 1)
	String codepoint;

	@CsvField(pos = 2)
	String character;

	@CsvField(pos = 3)
	String sequence;

	private Sinogram toEntities(Node node, boolean induced) {
		// les caractères intermédiaires n'ont pas de cp.
		ArrayList<Sinogram> sons = new ArrayList<>();
		for (Node n : node.getLeaves()) {
			sons.add(toEntities(n, true));
			Main.main++;
		}
		// Le caractère est créé après tous les intermédiaires.
		Sinogram sinogram = new Sinogram(Integer.toString(node.getCharacter()
				.codePointAt(0)), node.toString(), induced);
		if (node.getType() != null) {
			for (int i = 0; i < node.getType().getArity(); i++) {
				Structure structure = new Structure(sinogram, sons.get(i),
						node.getType(), i);
				// Main.structures++;
			}
		}
		return sinogram;
	}

	Node toEntities() {
		Node node = new Node(character, sequence);
		Main.main++;
		if (sequence.length() > 42) {
			System.out.println(node);
		}
		return node;
		// toEntities(node, false);

		/*
		 * if (ids.length() == 4) { if (IDC.contains(ids.charAt(0))) { } } else
		 * if (ids.length() == 3) { if (IDC.contains(ids.charAt(0))) { Sinogram
		 * sinogram = new Sinogram(codepoint, false); Sinogram son1 = new
		 * Sinogram(Integer.toString(ids .codePointAt(1)), true); Sinogram son2
		 * = new Sinogram(Integer.toString(ids .codePointAt(2)), true); IDC idc
		 * = new IDC(ids.charAt(0)); Structure structure1 = new
		 * Structure(sinogram, son1, idc, 0); Structure structure2 = new
		 * Structure(sinogram, son2, idc, 0); Allography allography = new
		 * Allography(sinogram, Allography.getNumber()); Main.processed3++; } }
		 * else if (ids.length() == 1) { Sinogram sinogram = new
		 * Sinogram(codepoint, false); IDC idc = new IDC(ids.charAt(0));
		 * Allography allography = new Allography(sinogram,
		 * Allography.getNumber()); Main.radicals++; } else { if
		 * (IDC.contains(ids.charAt(0))) {
		 * 
		 * // ⿱⿰⿱丿幺幺糸 // ⿳臼一糸 // ⿰糹⿱氏巾 // ⿰糹⿰⿻卩丿⿱龴又
		 * 
		 * Main.ignored++; System.out.println(ids); } else { Main.UTF16++; } }
		 */
	}
}
