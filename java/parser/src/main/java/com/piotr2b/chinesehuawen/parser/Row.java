package com.piotr2b.chinesehuawen.parser;

public abstract class Row {

	public abstract String getCharacter();

	public abstract String getSequence();

	public abstract String getCodepoint();

	protected Row() {
	}
}
