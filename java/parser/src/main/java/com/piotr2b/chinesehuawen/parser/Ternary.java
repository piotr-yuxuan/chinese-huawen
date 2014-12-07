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

	public static final Ternary TRUE = new Ternary(VALUE.TRUE);
	public static final Ternary FALSE = new Ternary(VALUE.FALSE);
	public static final Ternary MIDDLE = new Ternary(VALUE.MIDDLE);

	private static enum VALUE {
		TRUE("true", 1), FALSE("false", -1), MIDDLE("unknown", 0);

		private final String LITTERAL;
		public final int NUMERIC;

		private VALUE(String value, int figure) {
			this.LITTERAL = value;
			this.NUMERIC = figure;
		}

		private VALUE(int figure) {
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

		public static int toNumeric(VALUE value) {
			return value.NUMERIC;
		}

		public static VALUE fromNumeric(int figure) {
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

		public static int compare(Ternary.VALUE x, Ternary.VALUE y) {
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
			return new Ternary(VALUE.TRUE);
		case "false":
			return new Ternary(VALUE.FALSE);
		default:
		case "unknown":
			return new Ternary(VALUE.MIDDLE);
		}
	}

	public static Ternary valueOf(boolean b) {
		return (b ? new Ternary(VALUE.TRUE) : new Ternary(VALUE.FALSE));
	}

	public static Ternary valueOf(Boolean b) {
		return (b ? new Ternary(VALUE.TRUE) : new Ternary(VALUE.FALSE));
	}

	public static Ternary valueOf(String s) {
		switch (parseBoolean(s).value) {
		case FALSE:
			return new Ternary(VALUE.FALSE);
		case TRUE:
			return new Ternary(VALUE.TRUE);
		default:
		case MIDDLE:
			return new Ternary(VALUE.MIDDLE);
		}
	}

	public static Ternary AND(Boolean a, Ternary b) {
		return AND(new Ternary(a), b);
	}

	public static Ternary AND(Ternary a, Boolean b) {
		return AND(a, new Ternary(b));
	}

	public static Ternary AND(Boolean a, Boolean b) {
		return AND(new Ternary(a), new Ternary(b));
	}

	public static Ternary AND(Ternary a, Ternary b) {
		switch (operativeLogic) {
		case Bochvar:
			if (a.value == VALUE.MIDDLE || b.value == VALUE.MIDDLE) {
				return new Ternary(Ternary.VALUE.MIDDLE);
			} else if (a.value == VALUE.FALSE || b.value == VALUE.FALSE) {
				return new Ternary(Ternary.VALUE.FALSE);
			} else {
				return new Ternary(Ternary.VALUE.TRUE);
			}
		default:
		case Kleene_Strong:
		case Łukasiewicz:
		case Priest:
			return new Ternary(VALUE.fromNumeric(Math.min(VALUE.toNumeric(a.value), VALUE.toNumeric(b.value))));
		}
	}

	public static Ternary OR(Boolean a, Ternary b) {
		return OR(new Ternary(a), b);
	}

	public static Ternary OR(Ternary a, Boolean b) {
		return OR(a, new Ternary(b));
	}

	public static Ternary OR(Boolean a, Boolean b) {
		return OR(new Ternary(a), new Ternary(b));
	}

	public static Ternary OR(Ternary a, Ternary b) {
		switch (operativeLogic) {
		case Bochvar:
			if (a.value == VALUE.MIDDLE || b.value == VALUE.MIDDLE) {
				return new Ternary(Ternary.VALUE.MIDDLE);
			} else if (a.value == VALUE.TRUE || b.value == VALUE.TRUE) {
				return new Ternary(Ternary.VALUE.TRUE);
			} else {
				return new Ternary(Ternary.VALUE.FALSE);
			}
		default:
		case Kleene_Strong:
		case Łukasiewicz:
		case Priest:
			return new Ternary(VALUE.fromNumeric(Math.max(VALUE.toNumeric(a.value), VALUE.toNumeric(b.value))));
		}
	}

	public static Ternary NOT(Boolean b) {
		return NOT(new Ternary(b));
	}

	public static Ternary NOT(Ternary b) {
		switch (operativeLogic) {
		case Bochvar:
		case Kleene_Strong:
		case Łukasiewicz:
		case Priest:
		default:
			return new Ternary(VALUE.fromNumeric(-1 * VALUE.toNumeric(b.value)));
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
		return Ternary.VALUE.compare(a.value, b.value);
	}

	private final VALUE value;

	public Ternary(Ternary.VALUE value) {
		switch (value) {
		case FALSE:
			this.value = VALUE.FALSE;
			break;
		default:
		case MIDDLE:
			this.value = VALUE.MIDDLE;
			break;
		case TRUE:
			this.value = VALUE.TRUE;
			break;
		}
	}

	public Ternary(Ternary value) {
		this.value = value.value;
	}

	public Ternary(Boolean value) {
		this.value = (value) ? VALUE.TRUE : VALUE.FALSE;
	}

	public Ternary(boolean value) {
		this.value = (value) ? VALUE.TRUE : VALUE.FALSE;
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
		return Ternary.VALUE.compare(this.value, o.value);
	}

	@Override
	public String toString() {
		return this.value.toString();
	}
}
