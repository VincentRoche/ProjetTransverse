package eyjafjallajokull.projettransverse.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	public void ajouterStation(int coordonneeX, int coordonneeY, String nom)
	{
		Station s = new Station(coordonneeX, coordonneeY, nom);
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
	 * @return Ligne créée.
	 */
	public Ligne ajouterLigne()
	{
		Ligne l = new Ligne();
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
	 * Retourne les arcs qui ont une station donnée comme extrêmité.
	 * @param s Station cherchée.
	 * @return Arcs touchant cette station.
	 */
	public List<Arc> getArcsVoisins(Station s) {
		List<Arc> arcsVoisins = new ArrayList<Arc>();
		for (Arc a : arcs)
		{
			if (a.getExtremite1().equals(s) || a.getExtremite2().equals(s))
				arcsVoisins.add(a);
		}
		return arcsVoisins;
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
		for (Voyageur voyageur : voyageurs)
		{
			if (voyageur.getOrigine().getCheminCourt(voyageur.getDestination()) == null) // Si le trajet le plus court n'a pas encore été calculé
			{
				new Dijkstra(voyageur.getOrigine()).calculerChemins();
				System.out.print("Chemin de " + voyageur.getOrigine().getNom() + " à " + voyageur.getDestination().getNom() + " :");
				for (Station s : voyageur.getOrigine().getCheminCourt(voyageur.getDestination()))
				{
					System.out.print(" " + s.getNom());
				}
				System.out.println("");
			}
		}
		return 0;
	}


	/**
	 * https://fr.wikipedia.org/wiki/Algorithme_de_Dijkstra#Impl%C3%A9mentation_de_l'algorithme
	 */
	private class Dijkstra
	{
		private Station depart;
		private Map<Station, Integer> distances;
		private Map<Station, Station> predecesseurs;

		Dijkstra(Station depart)
		{
			this.depart = depart;
			this.distances = new HashMap<Station, Integer>();
			this.predecesseurs = new HashMap<Station, Station>();

			// Initialisation des distances à l'infini
			for (Station s : stations)
			{
				distances.put(s, Integer.MAX_VALUE);
			}
			distances.put(depart, 0); // Sauf la station de départ qui est initialisée à 0
		}

		Map<Station, List<Station>> calculerChemins()
		{
			List<Station> q = new ArrayList<Station>(stations); //Ensemble de tous les noeuds
			while (!q.isEmpty())
			{
				Station s1 = trouve_min(q);
				q.remove(s1);
				for (Arc a : getArcsVoisins(s1))
				{
					Station s2 = a.getExtremite1().equals(s1) ? a.getExtremite2() : a.getExtremite1();
					maj_distances(s1, s2, a);
				}
			}

			// Récupération des chemins les plus courts pour chaque station
			Map<Station, List<Station>> chemins = new HashMap<Station, List<Station>>();
			for (Station station : stations) // Pour chaque station de fin
			{
				List<Station> chemin = new ArrayList<Station>();
				Station s = station;
				while (!s.equals(depart))
				{
					chemin.add(s);
					s = predecesseurs.get(s);
				}
				Collections.reverse(chemin);
				chemins.put(station, chemin);
			}
			depart.setCheminsCourts(chemins);
			return chemins;
		}

		private Station trouve_min(List<Station> q)
		{
			int mini = Integer.MAX_VALUE;
			Station sommet = null;
			for (Station s : q)
			{
				if (distances.get(s) <= mini)
				{
					mini = distances.get(s);
					sommet = s;
				}
			}
			return sommet;
		}

		private void maj_distances(Station s1, Station s2, Arc a)
		{
			if (distances.get(s2) > distances.get(s1) + a.getLongueur())
			{
				distances.put(s2, distances.get(s1) + a.getLongueur());
				predecesseurs.put(s2, s1);
			}
		}
	}
}
