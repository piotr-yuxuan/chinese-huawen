package com.piotr2b.chinesehuawen.parser;

public class Alias<K1, K2> {
	private K1 k1;
	private K2 k2;

	public Alias() {
		k1 = null;
		k2 = null;
	}

	public Alias(K1 k1) {
		this.k1 = k1;
		k2 = null;
	}

	public Alias(K1 k1, K2 k2) throws UndefinedAliasException {
		if (k1 == null && k2 != null) {
			throw new UndefinedAliasException();
		}
		this.k1 = k1;
		this.k2 = k2; // Can be null
	}

	public K1 getK1() {
		return k1;
	}

	public void setK1(K1 k1) throws UndefinedAliasException {
		if (k1 == null && this.k2 != null) {
			throw new UndefinedAliasException();
		}
		this.k1 = k1;
	}

	public K2 getK2() {
		return k2;
	}

	public void setK2(K2 k2) {
		this.k2 = k2;
	}

	@Override
	public String toString() {
		return "(" + k1.toString() + ", " + k2.toString() + ")";
	}

	public boolean equals(Object o) {
		return (o != null && o.getClass().isAssignableFrom(this.getClass())) ? ((((Alias<?, ?>) o).getK1() == null ? this.getK1() == null
				: ((Alias<?, ?>) o).getK1().equals(this.getK1())) && (((Alias<?, ?>) o).getK2() == null ? this.getK2() == null : ((Alias<?, ?>) o)
				.getK2().equals(this.getK2()))) : false;
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
