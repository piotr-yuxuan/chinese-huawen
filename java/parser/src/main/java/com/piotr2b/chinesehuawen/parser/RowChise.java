package com.piotr2b.chinesehuawen.parser;

public class RowChise extends Row {

	protected RowChise() {
		super();
	}

	public RowChise(String[] split) {
		codepoint = split[0];
		character = split[1];
		sequence = split[2];
	}

	private String codepoint;

	private String character;

	private String sequence;

	@Override
	public String getCharacter() {
		return character;
	}

	@Override
	public String getSequence() {
		return sequence;
	}

	@Override
	public String getCodepoint() {
		return codepoint;
	}
}
