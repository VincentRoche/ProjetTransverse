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
}
