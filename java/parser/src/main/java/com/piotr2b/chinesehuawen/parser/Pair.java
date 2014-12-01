package com.piotr2b.chinesehuawen.parser;

public class Pair<U, V> {
	private U u;
	private V v;

	public Pair() {
		u = null;
		v = null;
	}

	public Pair(U u) {
		this.u = u;
		v = null;
	}

	public Pair(U u, V v) throws UndefinedAliasException {
		if (u == null && v != null) {
			throw new UndefinedAliasException();
		}
		this.u = u;
		this.v = v; // Can be null
	}

	public U getU() {
		return u;
	}

	public void setU(U u) throws UndefinedAliasException {
		if (u == null && this.v != null) {
			throw new UndefinedAliasException();
		}
		this.u = u;
	}

	public V getV() {
		return v;
	}

	public void setV(V v) {
		this.v = v;
	}

	@Override
	public String toString() {
		return "(" + u.toString() + ", " + v.toString() + ")";
	}

	public boolean equals(Object o) {
		return (o != null && o.getClass().isAssignableFrom(this.getClass())) ? ((((Pair<?, ?>) o).getU() == null ? this.getU() == null
				: ((Pair<?, ?>) o).getU().equals(this.getU())) && (((Pair<?, ?>) o).getV() == null ? this.getV() == null : ((Pair<?, ?>) o).getV()
				.equals(this.getV()))) : false;
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
