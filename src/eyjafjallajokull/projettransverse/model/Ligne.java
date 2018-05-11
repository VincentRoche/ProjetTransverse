package eyjafjallajokull.projettransverse.model;

import java.awt.Color;

/**
 * Représente une ligne de métro.
 */
public class Ligne {

	private static int nbLignes = 0;
	private final static Color[] couleursLignes = {
			new Color(255, 190, 0), 
			new Color(0, 85, 200), 
			new Color(110, 110, 0), 
			new Color(160, 0, 110), 
			new Color(255, 90, 0)};

	private final int numero;

	public Ligne() {
		this.numero = nbLignes++;
	}
	
	public Color getCouleur()
	{
		return couleursLignes[numero];
	}

	@Override
	public String toString() {
		return "Ligne [numero=" + numero + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Ligne))
			return false;
		return ((Ligne) obj).numero == numero;
	}

}
