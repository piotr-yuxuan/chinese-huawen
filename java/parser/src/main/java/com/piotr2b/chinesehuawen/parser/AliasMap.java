package com.piotr2b.chinesehuawen.parser;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.piotr2b.chinesehuawen.parser.Alias.UndefinedAliasException;

/***
 * Warning: this is still a draft. La fonction performLinkage n'est pas possible
 * rapidement avec cette conception. Il faut se rappeler que Java passe ses
 * références d'objet par copie…
 *
 * @param <Kmain>
 * @param <Kalias>
 *            Si k2 est nulle, ça veut dire que K1 n'a pas d'alias.
 * @param <V>
 */
// K1 and K2 are supposed to be two faces of a same abstract key K = Pair<K1,
// K2>. Here, K1 is the main key and K2 is optionnal key.

// Ca serait cool que ça implémente Stream<Entry<Pair<K1, K2>, V>>.
@Deprecated
public class AliasMap<Kmain, Kalias, V extends Node> implements Map<Alias<Kmain, Kalias>, V> {

	protected final Class<? extends Kmain> KMAIN;
	protected final Class<? extends Kalias> KALIAS;
	protected final Class<? extends V> VALUE;

	private Node.TreeType type;

	public void setTreeType(Node.TreeType type) {
		this.type = type;
	}

	public Node.TreeType getTreeType() {
		return this.type;
	}

	// TODO take care about K1 = null
	// HashMap allows null as key or value. Hashtable doesn't. ConcurrentHashMap
	// is built over Hastable so it doesn't either.
	private HashMap<Kmain, Kalias> ma; // may not be null
	private ConcurrentHashMap<Kalias, Kmain> am; // object can be null
	private ConcurrentHashMap<Kmain, V> mv;
	private ConcurrentHashMap<Kalias, V> av; // k1 is main key

	@Override
	public int size() {
		return mv.size();
	}

	@Override
	public boolean isEmpty() {
		return mv.isEmpty();
	}

	@SuppressWarnings("unused")
	private AliasMap() {
		KMAIN = null;
		KALIAS = null;
		VALUE = null;
	}

	public AliasMap(Class<? extends Kmain> kMain, Class<? extends Kalias> kAlias, Class<? extends V> value, Node.TreeType type) {
		if (kMain == null || kAlias == null || value == null) {
			throw new NullPointerException();
		}

		KMAIN = kMain;
		KALIAS = kAlias;
		VALUE = value;

		mv = new ConcurrentHashMap<Kmain, V>();
		av = new ConcurrentHashMap<Kalias, V>();
		ma = new HashMap<Kmain, Kalias>();
		am = new ConcurrentHashMap<Kalias, Kmain>();

		this.type = type;
	}

	/**
	 * Il faut séparer l'édition de lien de la formation des arbres et respecter
	 * cet ordre. Sinon les types d'arbres ne correspondent pas à leur
	 * définition.
	 */
	public void shapeTree() {
	}

	@Override
	public boolean containsKey(Object key) {

		if (key == null) {
			return false;
		}

		if (key.getClass().isAssignableFrom(KMAIN)) {
			return mv.containsKey(key);
		} else if (key.getClass().isAssignableFrom(KALIAS)) {
			return av.containsKey(key);
		} else if (key.getClass().isAssignableFrom((new Alias<Kmain, Kalias>()).getClass())) {
			Alias<Kmain, Kalias> K = new Alias<>();
			return containsKey(K.getKMain()); // You can't have K2 alone without
												// K1, you automatically have
												// K1.
		} else {
			return false;
		}
	}

	@Override
	public boolean containsValue(Object value) {
		return mv.containsValue(value);
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

		if (key.getClass().isAssignableFrom(KMAIN)) {
			return mv.get(key);
		} else if (key.getClass().isAssignableFrom(KALIAS)) {
			return av.get(key);
		} else if (key.getClass().isAssignableFrom((new Alias<Kmain, Kalias>()).getClass())) {
			return get(((Alias<Kmain, Kalias>) key).getKMain());
		} else {
			return null;
		}
	}

	@Override
	public V put(Alias<Kmain, Kalias> key, V value) {
		if (key == null || key.getKMain() == null) {
			return null;
		}

		mv.put(key.getKMain(), value);

		if (key.getKAlias() != null) {
			ma.put(key.getKMain(), key.getKAlias());

			av.put(key.getKAlias(), value);
			am.put(key.getKAlias(), key.getKMain());
		}
		return value;
	}

	// key = K1 ou K2. Mais si key = K2 alors il y a forcément K1 donc ce n'est
	// pas possible donc on a Pair<K1, K2>.
	public V put(Object key, V value) throws UndefinedAliasException {
		if (key == null) {
			return null;
		}

		if (key.getClass().isAssignableFrom(KMAIN)) {
			@SuppressWarnings({ "unused", "unchecked" })
			V put = mv.put((Kmain) key, value);
			// k1k2.put((K1) key, null);
		} else if (key.getClass().isAssignableFrom(KALIAS)) {
			throw new Alias.UndefinedAliasException();
		}
		return value;
	}

	@SuppressWarnings("unchecked")
	@Override
	public V remove(Object key) {
		V value = null;
		if (key.getClass().isAssignableFrom(KMAIN)) {
			Kmain k1 = (Kmain) key;
			Kalias k2 = ma.get(k1);
			mv.remove(k1);
			ma.remove(k1);
			if (k2 != null) {
				av.remove(k2);
				am.remove(k2);
			}
		} else if (key.getClass().isAssignableFrom(KALIAS)) {
			Kalias k2 = (Kalias) key;
			Kmain k1 = am.get(k2);
			mv.remove(k1);
			av.remove(k2);
			ma.remove(k1);
			am.remove(k2);
		} else if (key.getClass().isAssignableFrom((new Alias<Kmain, Kalias>()).getClass())) {
			Alias<Kmain, Kalias> K = (Alias<Kmain, Kalias>) key;
			mv.remove(K.getKMain());
			av.remove(K.getKAlias());
			ma.remove(K.getKMain());
			am.remove(K.getKAlias());
		}
		return value;
	}

	@Override
	public void putAll(Map<? extends Alias<Kmain, Kalias>, ? extends V> m) {
		for (java.util.Map.Entry<? extends Alias<Kmain, Kalias>, ? extends V> entry : m.entrySet()) {
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
		mv.clear();
		av.clear();
		ma.clear();
		am.clear();
	}

	@Override
	public Set<Alias<Kmain, Kalias>> keySet() {
		return ma.entrySet().stream().map(entry -> {
			Alias<Kmain, Kalias> alias = null;
			try {
				alias = new Alias<Kmain, Kalias>(entry.getKey(), entry.getValue());
			} catch (Exception e) {
				e.printStackTrace();
			}
			/* Normalement null peut être dans un ensemble; */
			return alias;
		}).collect(Collectors.toSet());
	}

	@Override
	public Collection<V> values() {
		return mv.values();
	}

	@Override
	public Set<Entry<Alias<Kmain, Kalias>, V>> entrySet() {
		return mv.entrySet().stream().map(entry -> {
			AbstractMap.SimpleEntry<Alias<Kmain, Kalias>, V> simpleEntry = null;
			try {
				Kalias alias = ma.get(entry.getKey());
				simpleEntry = new AbstractMap.SimpleEntry<Alias<Kmain, Kalias>, V>(//
						new Alias<Kmain, Kalias>(entry.getKey(), alias), //
						mv.get(entry.getKey()) //
				);
			} catch (Exception e) {
				e.printStackTrace();
			}
			/* Normalement null peut être dans un ensemble; */
			return simpleEntry;
		}).collect(Collectors.toSet());
	}

	// The symmetric entryKAliasSet is not needed: KMain should be present.
	public Set<Entry<Kmain, V>> entryKMainSet() {
		return mv.entrySet();
	}
}
