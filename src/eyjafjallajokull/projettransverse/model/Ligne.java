package eyjafjallajokull.projettransverse.model;

import java.awt.Color;
import java.util.Random;

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
			new Color(255, 90, 0), 
			new Color(130, 220, 115), 
			new Color(255, 130, 180), 
			new Color(210, 130, 190), 
			new Color(210, 210, 0), 
			new Color(220, 150, 0), 
			new Color(90, 35, 10), 
			new Color(0, 100, 60), 
			new Color(130, 200, 230), 
			new Color(100, 0, 130)};

	private final int numero;
	private final Color couleur;

	public Ligne() {
		this.numero = nbLignes++;
		if (couleursLignes.length > numero)
			this.couleur = couleursLignes[numero];
		else
		{
			// Couleur aléatoire
			Random rand = new Random();
			this.couleur = new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
		}
	}
	
	public Color getCouleur()
	{
		return couleur;
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
