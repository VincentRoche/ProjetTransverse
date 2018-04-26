package eyjafjallajokull.projettransverse.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Représente un réseau de stations et de lignes.
 */
public class Reseau {
	private List<Station> stations;
	private List<Arc> arcs;
	private List<Voyageur> voyageurs;
	private List<Ligne> lignes;
	private int xMax, yMax;
	
	/**
	 * @param xMax Taille maximale en X (le plus loin où une station peut être placée)
	 * @param yMax Taille maximale en Y
	 */
	public Reseau(int xMax, int yMax) {
		this.xMax = xMax;
		this.yMax = yMax;
		this.stations = new ArrayList<Station>();
		this.arcs = new ArrayList<Arc>();
		this.voyageurs = new ArrayList<Voyageur>();
		this.lignes = new ArrayList<Ligne>();
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
	
	/**
	 * Ajoute un voyager avec son origine et sa destination.
	 * @param origine Station d'où part le voyageur.
	 * @param destination Station où il va.
	 */
	public void ajouterVoyageur(Station origine, Station destination)
	{
		Voyageur v = new Voyageur(origine, destination);
		voyageurs.add(v);
	}
	
	/**
	 * Ajoute une ligne (mais pas ses arcs)
	 * @param numero Numéro de la ligne
	 * @return Ligne créée.
	 */
	public Ligne ajouterLigne(int numero)
	{
		Ligne l = new Ligne(numero);
		lignes.add(l);
		return l;
	}

	/**
	 * @return Stations du réseau.
	 */
	public List<Station> getStations() {
		return new ArrayList<Station>(stations);
	}

	/**
	 * @return Arcs du réseau.
	 */
	public List<Arc> getArcs() {
		return new ArrayList<Arc>(arcs);
	}

	/**
	 * @return Voyageurs.
	 */
	public List<Voyageur> getVoyageurs() {
		return new ArrayList<Voyageur>(voyageurs);
	}
	
	/**
	 * @return Lignes du réseau.
	 */
	public List<Ligne> getLignes() {
		return new ArrayList<Ligne>(lignes);
	}

	/**
	 * @return Coordonnée X maximale du réseau.
	 */
	public int getxMax() {
		return xMax;
	}

	/**
	 * @return Coordonnée Y maximale du réseau.
	 */
	public int getyMax() {
		return yMax;
	}
	
	/**
	 * Evalue l'efficacité du réseau en simulant les trajets des voyageurs.
	 * @return Moyenne des temps de parcours des voyageurs
	 */
	public float evaluer() {
		return 0;
	}
}
