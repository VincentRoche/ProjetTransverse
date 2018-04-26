package eyjafjallajokull.projettransverse.model;

/**
 * Représente une station de métro.
 */
public class Station {

	private final int coordonneeX;
	private final int coordonneeY;
                  private final String nom;

	Station(int coordonneeX, int coordonneeY, String nom) {
		this.coordonneeX = coordonneeX;
		this.coordonneeY = coordonneeY;
                                    this.nom = nom;
	}

	/**
	 * @return Coordonnée X de la station.
	 */
	public int getCoordonneeX() {
		return coordonneeX;
	}

	/**
	 * @return Coordonnée Y de la station.
	 */
	public int getCoordonneeY() {
		return coordonneeY;
	}
	
	/**
	 * Retourne la distance entre cette station et la station donnée.
	 * @param autreStation Station pour laquelle on veut la distance.
	 * @return Distance entre les deux stations.
	 */
	public double distance(Station autreStation)
	{
		return Math.hypot(this.getCoordonneeX() - autreStation.getCoordonneeX(), this.getCoordonneeY() - autreStation.getCoordonneeY());
	}

	@Override
	public String toString() {
		return "Station " + nom + " [X=" + coordonneeX + ", Y=" + coordonneeY + "]";
	}
}
