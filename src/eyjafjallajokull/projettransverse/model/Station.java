package eyjafjallajokull.projettransverse.model;

import java.util.List;
import java.util.Map;

/**
 * Représente une station de métro.
 */
public class Station {

	private final int coordonneeX;
	private final int coordonneeY;
	private final String nom;
	private Map<Station, List<Arc>> cheminsCourts; // Chemins les plus courts pour aller de cette station à chaque station de la Map

	Station(int coordonneeX, int coordonneeY, String nom) {
		this.coordonneeX = coordonneeX;
		this.coordonneeY = coordonneeY;
		this.nom = nom;
	}

	/**
	 * @return Coordonnée X de la station.
	 */
	public int getCoordonneeX()
	{
		return coordonneeX;
	}

	/**
	 * @return Coordonnée Y de la station.
	 */
	public int getCoordonneeY()
	{
		return coordonneeY;
	}

	public String getNom()
	{
		return nom;
	}

	/**
	 * Retourne le chemin le plus court pour aller à une station donnée.
	 * @param arrivee Station de destination
	 * @return Chemin allant à la station d'arrivée ou null s'il n'a pas été calculé.
	 */
	public List<Arc> getCheminCourt(Station arrivee) {
		return cheminsCourts != null ? cheminsCourts.get(arrivee) : null;
	}

	/**
	 * @param chemins Chemins associés à chaque station d'arrivée
	 */
	public void setCheminsCourts(Map<Station, List<Arc>> chemins) {
		this.cheminsCourts = chemins;
	}
	
	/**
	 * Supprime les chemins les plus courts enregistrés.
	 */
	public void viderCheminCourts()
	{
		this.cheminsCourts = null;
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

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Station))
			return false;
		Station s2 = (Station) obj;
		return s2.coordonneeX == this.coordonneeX && s2.coordonneeY == this.coordonneeY;
	}
}
