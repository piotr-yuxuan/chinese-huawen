package com.piotr2b.chinesehuawen.parser;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/***
 * Warning: this is still a draft
 *
 * @param <K1>
 * @param <K2>
 * @param <V>
 */
// K1 and K2 are supposed to be two faces of a same abstract key K.
public class PairMap<K1, K2, V extends Node> implements Map<Pair<K1, K2>, V> {

	private final Class K1_class = String.class;
	private final Class K2_class = Integer.class;
	private final Class V_class = Node.class;

	private HashMap<K1, K2> alias;
	private HashMap<K2, V> dictionary; // So K2 is main key.

	private HashMap<K1, V> k1v;
	private HashMap<K2, V> k2v;

	@Override
	public int size() {
		return dictionary.size();
	}

	@Override
	public boolean isEmpty() {
		return dictionary.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		// TODO Pair<K1, K2>
		if (key.getClass().isAssignableFrom(K1_class)) {
			return alias.containsKey(key);
		} else if (key.getClass().isAssignableFrom(K2_class)) {
			return dictionary.containsKey(key);
		} else {
			return false;
		}
	}

	@Override
	public boolean containsValue(Object value) {
		return dictionary.containsValue(value);
	}

	/***
	 * K1, K2 or Pair<K1, K2> will works
	 */
	@Override
	public V get(Object key) {
		// TODO Pair<K1, K2>
		if (key.getClass().isAssignableFrom(K1_class)) {
			return dictionary.get(alias.get(key));
		} else if (key.getClass().isAssignableFrom(K2_class)) {
			return dictionary.get(key);
		} else {
			return null;
		}
	}

	@Override
	public V put(Pair<K1, K2> key, V value) {
		// alias.put(key.getLeft(), key.getRight());
		// dictionary.put(key.getRight(), value);
		return value;
	}

	@Override
	public V remove(Object key) {
		V value = null;
		if (key.getClass().isAssignableFrom(K1_class)) {
			value = dictionary.get(alias.get(key));
			dictionary.remove(alias.get(key));
			alias.remove(key);
		} else if (key.getClass().isAssignableFrom(K2_class)) {
			value = dictionary.get(key);
			dictionary.remove(key);
			K1 k1key;
		}
		return value;
	}

	@Override
	public void putAll(Map<? extends Pair<K1, K2>, ? extends V> m) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clear() {
		alias.clear();
		dictionary.clear();
	}

	@Override
	public Set<Pair<K1, K2>> keySet() {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<V> values() {
		return (Collection<V>) dictionary.values();
	}

	@Override
	public Set<java.util.Map.Entry<Pair<K1, K2>, V>> entrySet() {
		// TODO Auto-generated method stub
		return null;
	}
}
