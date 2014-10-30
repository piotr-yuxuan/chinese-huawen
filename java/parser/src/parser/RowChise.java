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

	@Override
	public String getCharacter() {
		return character;
	}

	@Override
	public String getSequence() {
		return sequence;
	}

	public String getCodepoint() {
		return codepoint;
	}
}
