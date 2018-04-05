package eyjafjallajokull.projettransverse.model;

import java.util.List;

/**
 * Représente un réseau de stations et de lignes.
 */
public class Reseau {
	private List<Station> stations;
	private List<Arc> arcs;
	private int xMax, yMax;
	
	/**
	 * @param xMax Taille maximale en X (le plus loin où une station peut être placée)
	 * @param yMax Taille maximale en Y
	 */
	public Reseau(int xMax, int yMax) {
		this.xMax = xMax;
		this.yMax = yMax;
	}
	
	/**
	 * Lie deux stations du réseau par un arc.
	 * @param extremite1 Station à un bout de l'arc.
	 * @param extremite2 Station à l'autre bout de l'arc.
	 * @param ligne Ligne correspondante à l'arc.
	 */
	public void ajouterArc(Station extremite1, Station extremite2, Ligne ligne)
	{
		// TODO Vérifier que chaque station n'a que un ou zéro arc pour la ligne donnée.
		
		Arc a = new Arc(extremite1, extremite2, ligne);
		if (!arcs.contains(a)) // TODO Implémenter la comparaison pour les différents objets.
			arcs.add(a);
	}
	
	/**
	 * Ajoute une station au réseau, pas liée aux autres.
	 * @param coordonneeX Coordonnée X de la station.
	 * @param coordonneeY Coordonnée Y de la station.
	 */
	public void ajouterStation(int coordonneeX, int coordonneeY)
	{
		Station s = new Station(coordonneeX, coordonneeY);
		if (!stations.contains(s)) // TODO Implémenter comparaison
			stations.add(s);
	}
}
