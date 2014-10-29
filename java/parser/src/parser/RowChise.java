package parser;

import org.jsefa.csv.annotation.CsvDataType;
import org.jsefa.csv.annotation.CsvField;

@CsvDataType()
public class RowChise extends Row {

	@CsvField(pos = 1)
	private String codepoint;

	@CsvField(pos = 2)
	private String character;

	@CsvField(pos = 3)
	private String sequence;

	public String getCodepoint() {
		return codepoint;
	}

	public String getCharacter() {
		return character;
	}

	public String getSequence() {
		return sequence;
	}
}
