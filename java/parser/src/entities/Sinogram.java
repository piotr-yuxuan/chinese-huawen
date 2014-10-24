package entities;

public class Sinogram {
	String cp;
	String semantics;
	Character consonants;
	Character rhyme;
	Integer tone;
	Integer stroke;
	Integer frequency;
	Boolean induced;

	private Sinogram() {
	}

	public Sinogram(String cp, Boolean induced) {
		this.cp = cp;
		this.induced = induced;
	}
}
