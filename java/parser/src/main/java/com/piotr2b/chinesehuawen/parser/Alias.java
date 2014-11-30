package com.piotr2b.chinesehuawen.parser;

@Deprecated
public class Alias<Kmain, Kalias> {
	private Kmain kMain;
	private Kalias kAlias;

	public Alias() {
		kMain = null;
		kAlias = null;
	}

	public Alias(Kmain kMain) {
		this.kMain = kMain;
		kAlias = null;
	}

	public Alias(Kmain kMain, Kalias kAlias) throws UndefinedAliasException {
		if (kMain == null && kAlias != null) {
			throw new UndefinedAliasException();
		}
		this.kMain = kMain;
		this.kAlias = kAlias; // Can be null
	}

	public Kmain getKMain() {
		return kMain;
	}

	public void setKMain(Kmain kMain) throws UndefinedAliasException {
		if (kMain == null && this.kAlias != null) {
			throw new UndefinedAliasException();
		}
		this.kMain = kMain;
	}

	public Kalias getKAlias() {
		return kAlias;
	}

	public void setKAlias(Kalias kAlias) {
		this.kAlias = kAlias;
	}

	@Override
	public String toString() {
		return "(" + kMain.toString() + ", " + kAlias.toString() + ")";
	}

	public boolean equals(Object o) {
		return (o != null && o.getClass().isAssignableFrom(this.getClass())) ? ((((Alias<?, ?>) o).getKMain() == null ? this.getKMain() == null
				: ((Alias<?, ?>) o).getKMain().equals(this.getKMain())) && (((Alias<?, ?>) o).getKAlias() == null ? this.getKAlias() == null
				: ((Alias<?, ?>) o).getKAlias().equals(this.getKAlias()))) : false;
	}

	public static class UndefinedAliasException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1798624071367902280L;

		public UndefinedAliasException() {
			super("null value can't be aliased.");
		}
	}
}
