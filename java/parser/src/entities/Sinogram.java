package entities;

public class Sinogram {
	private int id; // ids hash
	String ids;
	String cp;
	String semantics;
	Character consonants;
	Character rhyme;
	Integer tone;
	Integer stroke;
	Integer frequency;
	Boolean induced;

	private static int idGenerator = 0;

	private Sinogram() {
	}

	public Sinogram(String cp, String ids, Boolean induced) {
		this.cp = cp;
		this.ids = ids;
		this.id = ids.hashCode();
		this.induced = induced;
	}
}
