package parser;

import org.jsefa.csv.annotation.CsvDataType;
import org.jsefa.csv.annotation.CsvField;

@CsvDataType()
public class Row {

	@CsvField(pos = 1)
	String codepoint;

	@CsvField(pos = 2)
	String character;

	@CsvField(pos = 3)
	String sequence;

	Node parse() {
		Node node = new Node(character, sequence);
		Main.main++;
		if (Node.split(sequence).size() > 15) {
			// Helps to wait for the parsing to be processed.
			System.out.println(node);
		}
		return node;
	}
}
