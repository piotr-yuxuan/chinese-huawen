package com.piotr2b.chinesehuawen.parser;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.piotr2b.chinesehuawen.parser.Alias.UndefinedAliasException;

/***
 * Warning: this is still a draft
 *
 * @param <K1>
 * @param <K2>
 *            Si k2 est nulle, ça veut dire que K1 n'a pas d'alias.
 * @param <V>
 */
// K1 and K2 are supposed to be two faces of a same abstract key K = Pair<K1,
// K2>. Here, K1 is the main key and K2 is optionnal key.

// Ca serait cool que ça implémente Stream<Entry<Pair<K1, K2>, V>>.
public class AliasMap<K1, K2, V extends Node> implements Map<Alias<K1, K2>, V> {

	private final Class<?> K1_class;
	private final Class<?> K2_class;
	private final Class<?> V_class;

	// TODO take care about K1 = null
	private HashMap<K1, K2> k1k2; // should not be null
	private HashMap<K2, K1> k2k1; // object can be null
	private HashMap<K1, V> k1v;
	private HashMap<K2, V> k2v; // k1 is main key

	@Override
	public int size() {
		return k1v.size();
	}

	@Override
	public boolean isEmpty() {
		return k1v.isEmpty();
	}

	private Class<K1> persistentClass;

	private AliasMap() {
		K1_class = null;
		K2_class = null;
		V_class = null;
	}

	public AliasMap(Class<?> k1, Class<?> k2, Class<?> V) {
		if (k1 == null || k2 == null || V == null) {
			throw new NullPointerException();
		}

		K1_class = k1;
		K2_class = k2;
		V_class = V;

		k1v = new HashMap<K1, V>();
		k2v = new HashMap<K2, V>();
		k1k2 = new HashMap<K1, K2>();
		k2k1 = new HashMap<K2, K1>();
	}

	@Override
	public boolean containsKey(Object key) {

		if (key == null) {
			return false;
		}

		if (key.getClass().isAssignableFrom(K1_class)) {
			return k1v.containsKey(key);
		} else if (key.getClass().isAssignableFrom(K2_class)) {
			return k2v.containsKey(key);
		} else if (key.getClass().isAssignableFrom((new Alias<K1, K2>()).getClass())) {
			Alias<K1, K2> K = new Alias<>();
			return containsKey(K.getK1()); // You can't have K2 alone without
											// K1, you automatically have K1.
		} else {
			return false;
		}
	}

	@Override
	public boolean containsValue(Object value) {
		return k1v.containsValue(value);
	}

	/***
	 * K1, K2 or Pair<K1, K2> will works
	 */
	@SuppressWarnings("unchecked")
	@Override
	public V get(Object key) {

		if (key == null) {
			return null;
		}

		if (key.getClass().isAssignableFrom(K1_class)) {
			return k1v.get(key);
		} else if (key.getClass().isAssignableFrom(K2_class)) {
			return k2v.get(key);
		} else if (key.getClass().isAssignableFrom((new Alias<K1, K2>()).getClass())) {
			return get(((Alias<K1, K2>) key).getK1());
		} else {
			return null;
		}
	}

	@Override
	public V put(Alias<K1, K2> key, V value) {
		if (key == null || key.getK1() == null) {
			return null;
		}

		k1v.put(key.getK1(), value);

		if (key.getK2() != null) {
			k1k2.put(key.getK1(), key.getK2());

			k2v.put(key.getK2(), value);
			k2k1.put(key.getK2(), key.getK1());
		}
		return value;
	}

	// key = K1 ou K2. Mais si key = K2 alors il y a forcément K1 donc ce n'est
	// pas possible donc on a Pair<K1, K2>.
	public V put(Object key, V value) throws UndefinedAliasException {
		if (key == null) {
			return null;
		}

		if (key.getClass().isAssignableFrom(K1_class)) {
			k1v.put((K1) key, value);
			// k1k2.put((K1) key, null);
		} else if (key.getClass().isAssignableFrom(K2_class)) {
			throw new Alias.UndefinedAliasException();
		}
		return value;
	}

	@SuppressWarnings("unchecked")
	@Override
	public V remove(Object key) {
		V value = null;
		if (key.getClass().isAssignableFrom(K1_class)) {
			K1 k1 = (K1) key;
			K2 k2 = k1k2.get(k1);
			k1v.remove(k1);
			k1k2.remove(k1);
			if (k2 != null) {
				k2v.remove(k2);
				k2k1.remove(k2);
			}
		} else if (key.getClass().isAssignableFrom(K2_class)) {
			K2 k2 = (K2) key;
			K1 k1 = k2k1.get(k2);
			k1v.remove(k1);
			k2v.remove(k2);
			k1k2.remove(k1);
			k2k1.remove(k2);
		} else if (key.getClass().isAssignableFrom((new Alias<K1, K2>()).getClass())) {
			Alias<K1, K2> K = (Alias<K1, K2>) key;
			k1v.remove(K.getK1());
			k2v.remove(K.getK2());
			k1k2.remove(K.getK1());
			k2k1.remove(K.getK2());
		}
		return value;
	}

	@Override
	public void putAll(Map<? extends Alias<K1, K2>, ? extends V> m) {
		for (java.util.Map.Entry<? extends Alias<K1, K2>, ? extends V> entry : m.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}

	}

	// // Because of type erasure, we can't implement that.
	// public void putAll(Map<?, ? extends V> m) {
	// for (java.util.Map.Entry<?, ? extends V> entry : m.entrySet()) {
	// // ...
	// }
	// }

	@Override
	public void clear() {
		k1v.clear();
		k2v.clear();
		k1k2.clear();
		k2k1.clear();
	}

	@Override
	public Set<Alias<K1, K2>> keySet() {
		return k1k2.entrySet().stream().map(entry -> {
			Alias<K1, K2> alias = null;
			try {
				alias = new Alias<K1, K2>(entry.getKey(), entry.getValue());
			} catch (Exception e) {
				e.printStackTrace();
			}
			// Normalement null peut être dans un ensemble;
				return alias;
			}).collect(Collectors.toSet());
	}

	@Override
	public Collection<V> values() {
		return k1v.values();
	}

	@Override
	public Set<Entry<Alias<K1, K2>, V>> entrySet() {
		return k1k2.entrySet().stream().map(entry -> {
			AbstractMap.SimpleEntry<Alias<K1, K2>, V> simpleEntry = null;
			try {
				simpleEntry = new AbstractMap.SimpleEntry<Alias<K1, K2>, V>(new Alias<K1, K2>( //
						entry.getKey(), entry.getValue()), //
						k1v.get(entry.getKey()) //
				);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// Normalement null peut être dans un ensemble;
				return simpleEntry;
			}).collect(Collectors.toSet());
	}
}
