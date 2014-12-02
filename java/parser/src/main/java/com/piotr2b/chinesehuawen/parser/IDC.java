package com.piotr2b.chinesehuawen.parser;

// Like an enum but easier to work with.
public final class IDC {

	public static final char LEFT_TO_RIGHT = '⿰';

	public static final char ABOVE_TO_BELOW = '⿱';

	public static final char LEFT_TO_MIDDLE_AND_RIGHT = '⿲';

	public static final char ABOVE_TO_MIDDLE_AND_BELOW = '⿳';

	public static final char SURROUND = '⿴';

	public static final char SURROUND_FROM_ABOVE = '⿵';

	public static final char SURROUND_FROM_BELOW = '⿶';

	public static final char SURROUND_FROM_LEFT = '⿷';

	public static final char SURROUND_FROM_UPPER_LEFT = '⿸';

	public static final char SURROUND_FROM_UPPER_RIGHT = '⿹';

	public static final char SURROUND_FROM_LOWER_LEFT = '⿺';

	public static final char OVERLAID = '⿻';

	public static final char[] IDC = { '⿰', '⿱', '⿲', '⿳', '⿴', '⿵', '⿶', '⿷', '⿸', '⿹', '⿺', '⿻' };

	public static final boolean contains(char c) {
		for (char d : IDC) {
			if (c == d)
				return true;
		}
		return false;
	}

	@SuppressWarnings("unused")
	private IDC() {
	}

	private char idc;

	public IDC(char character) {
		if (contains(character)) {
			this.idc = character;
		} else { // null
		}
	}

	public int getArity() {
		switch (idc) {
		case '⿰':
		case '⿱':
		case '⿴':
		case '⿵':
		case '⿶':
		case '⿷':
		case '⿸':
		case '⿹':
		case '⿺':
		case '⿻':
			return 2;
		case '⿲':
		case '⿳':
			return 3;
		default:
			return -1;
		}
	}

	@Override
	public String toString() {
		return Character.toString(idc);
	}
}
