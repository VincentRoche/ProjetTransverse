package eyjafjallajokull.projettransverse.model;

/**
 * Représente une station de métro.
 */
public class Station {

	private final int coordonneeX;
	private final int coordonneeY;

	Station(int coordonneeX, int coordonneeY) {
		this.coordonneeX = coordonneeX;
		this.coordonneeY = coordonneeY;
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
		return "Station [X=" + coordonneeX + ", Y=" + coordonneeY + "]";
	}
}
