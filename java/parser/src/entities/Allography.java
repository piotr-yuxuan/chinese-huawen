package entities;

public class Allography {
	private static int number = 0;

	public static int getNumber() {
		return number;
	}

	Sinogram sinogram;
	Integer alloclass;

	private Allography() {
	}

	public Allography(Sinogram sinogram, Integer alloclass) {
		this.sinogram = sinogram;
		Allography.number++;
	}
}
