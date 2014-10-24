package entities;

import parser.IDC;

public class Structure {
	Sinogram father;
	Sinogram son;
	IDC idc;
	Integer ordinal;

	private Structure() {
	}

	public Structure(Sinogram father, Sinogram Son, IDC idc, Integer ordinal) {
		this.father = father;
		this.son = son;
		this.idc = idc;
		this.ordinal = ordinal;
	}
}
