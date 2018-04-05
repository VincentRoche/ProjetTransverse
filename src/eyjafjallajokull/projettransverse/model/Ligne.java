package eyjafjallajokull.projettransverse.model;

/**
 * Représente une ligne de métro.
 */
public class Ligne {

	private final int numero;

	public Ligne(int numero) {
		this.numero = numero;
	}

	@Override
	public String toString() {
		return "Ligne [numero=" + numero + "]";
	}

}
