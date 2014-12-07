package com.piotr2b.chinesehuawen.parser;

import java.util.UnknownFormatConversionException;

/**
 * Implements three-valued logic. May be useful further when qyerying IDS («
 * What are sinograms in traditionnal style which contain smaller sinograms A
 * and B with B on the left side and always above A, with more than three
 * strokes, sorted by speech frequency? »).
 * 
 * @author caocoa
 *
 */
public final class Ternary implements java.io.Serializable, Comparable<Ternary> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static enum V {
		TRUE, FALSE, MIDDLE
	}

	private static enum INTERNAL_VALUES {
		TRUE("true", 1), FALSE("false", -1), MIDDLE("unknown", 0);

		private final String LITTERAL;
		public final int NUMERIC;

		private INTERNAL_VALUES(String value, int figure) {
			this.LITTERAL = value;
			this.NUMERIC = figure;
		}

		private INTERNAL_VALUES(int figure) {
			switch (figure) {
			case 1:
				this.LITTERAL = "true";
				break;
			case 0:
				this.LITTERAL = "unknown";
				break;
			case -1:
				this.LITTERAL = "false";
				break;
			default:
				throw new UnknownFormatConversionException("This value " + figure + " is not allowed.");
			}
			this.NUMERIC = figure;
		}

		@Override
		public String toString() {
			return LITTERAL;
		}

		public static int toNumeric(INTERNAL_VALUES value) {
			return value.NUMERIC;
		}

		public static INTERNAL_VALUES fromNumeric(int figure) {
			switch (figure) {
			case 1:
				return TRUE;
			case 0:
				return MIDDLE;
			case -1:
				return FALSE;
			default:
				throw new UnknownFormatConversionException("This value " + figure + " is not allowed.");
			}
		}

		public static int compare(Ternary.INTERNAL_VALUES x, Ternary.INTERNAL_VALUES y) {
			if (x == y) {
				return 0;
			} else if (x == FALSE) {
				return -1;
			} else if (x == TRUE) {
				return 1;
			} else {
				return (y == FALSE) ? 1 : -1;
			}
		}
	};

	/**
	 * https://en.wikipedia.org/wiki/Many-valued_logic
	 * 
	 * <p>
	 * When talking about operation values:
	 * </p>
	 * <ul>
	 * <li>Priest = Kleene_Strong = Łukasiewicz</li>
	 * <li>Bochvar = Kleene_Weak</li>
	 * </ul>
	 * 
	 * @author caocoa
	 *
	 */
	public static enum LOGIC {
		Bochvar, Kleene_Strong, Kleene_Weak, Łukasiewicz, Priest
	}

	/**
	 * Default: Kleene Logic.
	 */
	public static LOGIC operativeLogic = LOGIC.Kleene_Strong;

	public static Ternary parseBoolean(String s) {
		switch (s.toLowerCase()) {
		case "true":
			return new Ternary(INTERNAL_VALUES.TRUE);
		case "false":
			return new Ternary(INTERNAL_VALUES.FALSE);
		default:
		case "unknown":
			return new Ternary(INTERNAL_VALUES.MIDDLE);
		}
	}

	public static Ternary valueOf(boolean b) {
		return (b ? new Ternary(INTERNAL_VALUES.TRUE) : new Ternary(INTERNAL_VALUES.FALSE));
	}

	public static Ternary valueOf(Boolean b) {
		return (b ? new Ternary(INTERNAL_VALUES.TRUE) : new Ternary(INTERNAL_VALUES.FALSE));
	}

	public static Ternary valueOf(String s) {
		switch (parseBoolean(s).value) {
		case FALSE:
			return new Ternary(INTERNAL_VALUES.FALSE);
		case TRUE:
			return new Ternary(INTERNAL_VALUES.TRUE);
		default:
		case MIDDLE:
			return new Ternary(INTERNAL_VALUES.MIDDLE);
		}
	}

	public static Ternary AND(Ternary a, Ternary b) {
		switch (operativeLogic) {
		case Bochvar:
			if (a.value == INTERNAL_VALUES.MIDDLE || b.value == INTERNAL_VALUES.MIDDLE) {
				return new Ternary(Ternary.INTERNAL_VALUES.MIDDLE);
			} else if (a.value == INTERNAL_VALUES.FALSE || b.value == INTERNAL_VALUES.FALSE) {
				return new Ternary(Ternary.INTERNAL_VALUES.FALSE);
			} else {
				return new Ternary(Ternary.INTERNAL_VALUES.TRUE);
			}
		default:
		case Kleene_Strong:
		case Łukasiewicz:
		case Priest:
			return new Ternary(INTERNAL_VALUES.fromNumeric(Math.min(INTERNAL_VALUES.toNumeric(a.value), INTERNAL_VALUES.toNumeric(b.value))));
		}
	}

	public static Ternary OR(Ternary a, Ternary b) {
		switch (operativeLogic) {
		case Bochvar:
			if (a.value == INTERNAL_VALUES.MIDDLE || b.value == INTERNAL_VALUES.MIDDLE) {
				return new Ternary(Ternary.INTERNAL_VALUES.MIDDLE);
			} else if (a.value == INTERNAL_VALUES.TRUE || b.value == INTERNAL_VALUES.TRUE) {
				return new Ternary(Ternary.INTERNAL_VALUES.TRUE);
			} else {
				return new Ternary(Ternary.INTERNAL_VALUES.FALSE);
			}
		default:
		case Kleene_Strong:
		case Łukasiewicz:
		case Priest:
			return new Ternary(INTERNAL_VALUES.fromNumeric(Math.max(INTERNAL_VALUES.toNumeric(a.value), INTERNAL_VALUES.toNumeric(b.value))));
		}
	}

	public static Ternary NOT(Ternary b) {
		switch (operativeLogic) {
		case Bochvar:
		case Kleene_Strong:
		case Łukasiewicz:
		case Priest:
		default:
			return new Ternary(INTERNAL_VALUES.fromNumeric(-1 * INTERNAL_VALUES.toNumeric(b.value)));
		}
	}

	public static Ternary XOR(Ternary a, Ternary b) {
		return AND(OR(a, b), NOT(AND(a, b)));
	}

	public static Ternary XAND(Ternary a, Ternary b) {
		return OR(AND(a, b), AND(NOT(a), NOT(b)));
	}

	public static String toString(Ternary b) {
		return b.toString();
	}

	public static int compare(Ternary a, Ternary b) {
		return Ternary.INTERNAL_VALUES.compare(a.value, b.value);
	}

	private final INTERNAL_VALUES value;

	public Ternary(Ternary.V value) {
		switch (value) {
		case FALSE:
			this.value = INTERNAL_VALUES.FALSE;
			break;
		default:
		case MIDDLE:
			this.value = INTERNAL_VALUES.MIDDLE;
			break;
		case TRUE:
			this.value = INTERNAL_VALUES.TRUE;
			break;
		}
	}

	public Ternary(Ternary value) {
		this.value = value.value;
	}

	private Ternary(INTERNAL_VALUES value) {
		this.value = value;
	}

	public Ternary(Boolean value) {
		this.value = (value) ? INTERNAL_VALUES.TRUE : INTERNAL_VALUES.FALSE;
	}

	public Ternary(boolean value) {
		this.value = (value) ? INTERNAL_VALUES.TRUE : INTERNAL_VALUES.FALSE;
	}

	public Ternary(String s) {
		this(parseBoolean(s));
	}

	public boolean equals(Object obj) {
		if (obj instanceof Ternary) {
			return value == (new Ternary((Ternary) obj)).value;
		}
		return false;
	}

	@Override
	public int compareTo(Ternary o) {
		return Ternary.INTERNAL_VALUES.compare(this.value, o.value);
	}

	@Override
	public String toString() {
		return this.value.toString();
	}
}
