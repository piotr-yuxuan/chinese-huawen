package com.piotr2b.chinesehuawen.parser;

public class Pair<K0, K1> {
	private K0 k1;
	private K1 k2;

	public Pair(K0 K1, K1 K2) {
		this.k1 = K1;
		this.k2 = K2;
	}

	public K0 getK1() {
		return k1;
	}

	public void setK1(K0 k1) {
		this.k1 = k1;
	}

	public K1 getK2() {
		return k2;
	}

	public void setK2(K1 k2) {
		this.k2 = k2;
	}

	@Override
	public String toString() {
		return "(" + k1.toString() + ", " + k2.toString() + ")";
	}

	public boolean equals(Pair<?, ?> pair) {
		return (pair.getK1() == null ? this.getK1() == null : pair.getK1().equals(this.getK1()))
				&& (pair.getK2() == null ? this.getK2() == null : pair.getK2().equals(this.getK2()));
	}
}
