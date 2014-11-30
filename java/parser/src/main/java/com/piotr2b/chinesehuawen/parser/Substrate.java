package com.piotr2b.chinesehuawen.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Substrate {

	private ConcurrentHashMap<Integer, Node> in;
	private ConcurrentHashMap<String, Integer> si;
	private ConcurrentHashMap<Integer, String> is;

	public Substrate() {
		in = new ConcurrentHashMap<>();
		si = new ConcurrentHashMap<>();
		is = new ConcurrentHashMap<>();
	}

	public boolean flatten(Node node) {
		if (node.character != null && node.character.equals("敹")) {
			System.out.print("");
		}
		Node flattened = new Node();
		flattened.idc = node.idc;
		flattened.character = node.character;
		for (int i = 0; i < node.leaves.size(); i++) {
			Node n = node.leaves.get(i);
			flatten(n);
			if (in.get(n.getId()) == null) {
				System.out.print("");
			} else {
				flattened.leaves.add(in.get(n.getId()));
			}
		}
		// On est sûr maintenant que les feuilles ont été intégrées.
		if (in.containsKey(node.getId())) {
			// Ce nœud est déjà connu.
			if (in.get(node.getId()).getCharacter() == null && flattened.getCharacter() != null) {
				// Le substrat contient un caractère anonyme, on le remplace
				// sans état d'âme.
				in.put(flattened.getId(), flattened);
				si.put(flattened.getCharacter(), flattened.getId());
				is.put(flattened.getId(), flattened.getCharacter());
			} else if (!(node.getCharacter() == null || in.get(node.getId()).getCharacter().equals(node.getCharacter()))) {
				// Ce sinograme se fait redéfinir, il s'agit d'une incohérence.
				System.out.println("Incohérence");
			}
			return true;
		} else {
			// C'est un nouveau nœud.
			in.put(flattened.getId(), flattened);
			if (!(flattened.getCharacter() == null || flattened.getCharacter().equals(""))) {
				si.put(flattened.getCharacter(), flattened.getId());
				is.put(flattened.getId(), flattened.getCharacter());
			}
			return false;
		}
	}

	public Set<Node> export(Node.TreeType type) {
		return in.values().stream().map(x -> type.export(x)).collect(Collectors.toCollection(HashSet<Node>::new));
	}

	public List<Node> getCompounds(Node node) {
		if (in.containsKey(node.getId())) {
			return in.values().stream().filter(x -> x.contains(node)).collect(Collectors.toList());
		} else {
			// Sinogramme inconnu, renvoie 0.
			return new ArrayList<Node>();
		}
	}

	public Collection<Node> values() {
		return in.values();
	}

	public int size() {
		return in.size();
	}

	public boolean isEmpty() {
		return in.isEmpty();
	}

	public Set<Entry<Integer, Node>> entrySet() {
		return in.entrySet();
	}
}
