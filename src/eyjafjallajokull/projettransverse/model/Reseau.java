package eyjafjallajokull.projettransverse.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Représente un réseau de stations et de lignes.
 */
public class Reseau implements Cloneable {
	private List<Station> stations;
	private List<Arc> arcs;
	private List<Voyageur> voyageurs;
	private List<Ligne> lignes;
	private int xMax, yMax;
	private double fluxMoyen;

	/** Temps ajouté par un changement de ligne dans un trajet. */
	public final static int TEMPS_CORRESPONDANCE = 100;

	public Reseau() {
		this.xMax = 600;
		this.yMax = 500;
		this.stations = new ArrayList<Station>();
		this.arcs = new ArrayList<Arc>();
		this.voyageurs = new ArrayList<Voyageur>();
		this.lignes = new ArrayList<Ligne>();
	}

	/**
	 * Lie deux stations du réseau par un arc.
	 * @param extremite1 Station à un bout de l'arc.
	 * @param extremite2 Station à l'autre bout de l'arc.
	 * @param ligne Ligne correspondante à l'arc (peut être null dans certains algorithmes spécifiques).
	 * @return L'arc ajouté
	 * @throws TropDArcsDeLaMemeLigneException Si une des extrémités est déjà liée à 2 arcs de la ligne donnée.
	 * @throws ArcDejaExistantException S'il y a déjà un arc entre ces sommets.
	 */
	public Arc ajouterArc(Station extremite1, Station extremite2, Ligne ligne) throws TropDArcsDeLaMemeLigneException, ArcDejaExistantException
	{
		if (ligne != null)
		{
			// Vérification que chaque station n'a que un ou zéro arc pour la ligne donnée.
			int nbVoisins1 = 0, nbVoisins2 = 0;
			for (Arc a : getArcsVoisins(extremite1))
			{
				if (a.getLigne().equals(ligne))
					nbVoisins1++;
			}
			for (Arc a : getArcsVoisins(extremite2))
			{
				if (a.getLigne().equals(ligne))
					nbVoisins2++;
			}
			if (nbVoisins1 > 1 || nbVoisins2 > 1)
				throw new TropDArcsDeLaMemeLigneException();
		}

		Arc a = new Arc(extremite1, extremite2, ligne);
		if (!arcs.contains(a))
		{
			arcs.add(a);
			return a;
		}
		else
		{
			throw new ArcDejaExistantException(a.toString());
		}
	}

	/**
	 * Ajoute une station au réseau, pas liée aux autres.
	 * @param coordonneeX Coordonnée X de la station.
	 * @param coordonneeY Coordonnée Y de la station.
	 */
	public void ajouterStation(int coordonneeX, int coordonneeY, String nom)
	{
		Station s = new Station(coordonneeX, coordonneeY, nom);
		if (!stations.contains(s))
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
	 * @param nom Nom recherché
	 * @return Station portant le nom cherché.
	 */
	public Station getStation(String nom) {
		for (Station s : stations)
		{
			if (s.getNom().equalsIgnoreCase(nom))
				return s;
		}
		return null;
	}
	
	/**
	 * @return Liste des stations reliées à aucune ligne
	 */
	public List<Station> stationsIsolees()
	{
		List<Station> liste = new ArrayList<Station>();
		for (Station s : stations)
		{
			if (getArcsVoisins(s).isEmpty())
				liste.add(s);
		}
		return liste;
	}
	
	/**
	 * @param station
	 * @return Liste des arcs étant le dernier arc d'une ligne à la station donnée.
	 */
	public List<Arc> getArcsTerminus(Station station)
	{
		Map<Ligne, Integer> nbArcs = new HashMap<Ligne, Integer>();
		Map<Ligne, Arc> arcLigne = new HashMap<Ligne, Arc>();
		for (Arc a : getArcsVoisins(station))
		{
			Ligne l = a.getLigne();
			if (!nbArcs.containsKey(l))
				nbArcs.put(l, 1);
			else
				nbArcs.put(l, nbArcs.get(l) + 1);
			arcLigne.put(l, a);
		}
		List<Arc> terminus = new ArrayList<Arc>();
		for (Entry<Ligne, Integer> entry : nbArcs.entrySet())
		{
			if (entry.getValue() == 1)
				terminus.add(arcLigne.get(entry.getKey()));
		}
		return terminus;
	}

	/**
	 * @return Arcs du réseau.
	 */
	public List<Arc> getArcs() {
		return new ArrayList<Arc>(arcs);
	}

	/**
	 * @return Arcs d'une ligne.
	 */
	public List<Arc> getArcs(Ligne ligne) {
		List<Arc> liste = new ArrayList<Arc>();
		for (Arc a : arcs)
		{
			if (a.getLigne() != null && a.getLigne().equals(ligne))
				liste.add(a);
		}
		return liste;
	}

	/**
	 * Retourne les arcs qui ont une station donnée comme extrémité.
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
	 * Retourne un arc existant avec les deux extrémités données.
	 * @param extremite1
	 * @param extremite2
	 * @return Arc avec les bonnes extrémités (quel que soit leur ordre)
	 */
	public Arc getArc(Station extremite1, Station extremite2) {
		for (Arc a : arcs)
		{
			if ((a.getExtremite1().equals(extremite1) && a.getExtremite2().equals(extremite2)) || (a.getExtremite1().equals(extremite2) && a.getExtremite2().equals(extremite1)))
				return a;
		}
		return null;
	}

	/**
	 * Supprime un arc du graphe.
	 * @param a Arc à supprimer.
	 */
	public void supprimerArc(Arc a)
	{
		arcs.remove(a);
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
	 * @param xMax Coordonnée X maximale du plan.
	 */
	public void setxMax(int xMax) {
		this.xMax = xMax;
	}

	/**
	 * @param yMax Coordonnée Y maximale du plan.
	 */
	public void setyMax(int yMax) {
		this.yMax = yMax;
	}

	/**
	 * @return Longueur totale du réseau (tous les arcs).
	 */
	public long getLongueur() {
		long l = 0;
		for (Arc a : arcs)
		{
			l += a.getLongueur();
		}
		return l;
	}

	/**
	 * @return Flux moyen sur les arcs.
	 */
	public double getFluxMoyen() {
		return fluxMoyen;
	}

	/**
	 * Calcule les chemins les plus courts pour chaque station et voyageur (les résultats sont dans chaque station), et le flux de chaque arc.
	 */
	public void calculerCheminsCourts()
	{
		// Réinitialisation des chemins les plus courts
		for (Station s : stations)
		{
			s.viderCheminCourts();
		}

		// Réinitialisation des flux des arcs
		for (Arc a : arcs)
		{
			a.reinitialiserFlux();
		}

		// Dijkstra
		for (Voyageur voyageur : voyageurs)
		{
			if (voyageur.getOrigine().getCheminCourt(voyageur.getDestination()) == null) // Si le trajet le plus court n'a pas encore été calculé
			{
				new Dijkstra(voyageur.getOrigine()).calculerChemins();
			}
			// Incrémentation du flux de chaque arc du chemin
			for (Arc a : voyageur.getOrigine().getCheminCourt(voyageur.getDestination()))
			{
				a.incrementerFlux();
			}
		}
		
		// Calcul du flux moyen par arc
		long somme = 0;
		for (Arc a : arcs)
		{
			somme += a.getFlux();
		}
		fluxMoyen = (double) somme / arcs.size();
	}

	/**
	 * Evalue l'efficacité du réseau en simulant les trajets des voyageurs.
	 * @return Moyenne des temps de parcours des voyageurs
	 */
	public double evaluer() {
		calculerCheminsCourts();

		// Initialisation des trajets
		for (Voyageur v : voyageurs)
		{
			v.initialiserTrajet();
		}
		int nbArrives = 0;
		long sommeTemps = 0;
		while (nbArrives < voyageurs.size())
		{
			// Remise des compteurs de voyageurs des arcs à 0
			for (Arc a : arcs)
			{
				a.reinitialiserEntrees();
			}
			// Déplacement des voyageurs pas encore arrivés
			for (Voyageur v : voyageurs)
			{
				if (!v.estArrive())
				{
					v.avancer();
					if (v.estArrive())
					{
						nbArrives++;
						sommeTemps += v.getTempsTrajet();
					}
				}
			}
		}

		// Tout le monde est arrivé, calcul de la moyenne des temps de parcours
		double moyenne = (float) sommeTemps / nbArrives;
		return moyenne;
	}


	@Override
	protected Reseau clone() throws CloneNotSupportedException {
		Reseau r = (Reseau) super.clone();
		r.stations = new ArrayList<Station>(this.stations);
		r.arcs = new ArrayList<Arc>(this.arcs);
		r.voyageurs = new ArrayList<Voyageur>(this.voyageurs);
		r.lignes = new ArrayList<Ligne>(this.lignes);
		return r;
	}


	/**
	 * https://fr.wikipedia.org/wiki/Algorithme_de_Dijkstra#Impl%C3%A9mentation_de_l'algorithme
	 */
	private class Dijkstra
	{
		private Station depart;
		private Map<Station, Integer> distances;
		private Map<Station, Station> predecesseurs;

		private Dijkstra(Station depart)
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

		private Map<Station, List<Arc>> calculerChemins()
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
			Map<Station, List<Arc>> chemins = new HashMap<Station, List<Arc>>();
			for (Station station : stations) // Pour chaque station de fin
			{
				List<Arc> chemin = new ArrayList<Arc>();
				Station s = station;
				while (!s.equals(depart))
				{
					Station s2 = predecesseurs.get(s);
					chemin.add(getArc(s2, s));
					s = s2;
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
			int longueurA = (int) Math.round(a.getLongueur());

			// Si passer par ce nouvel arc fait changer de ligne, malus
			if (a.getLigne() != null)
			{
				Arc arcPrecedent = getArc(predecesseurs.get(s1), s1);
				if (!s1.equals(depart) && !a.getLigne().equals(arcPrecedent.getLigne()))
				{
					longueurA += Reseau.TEMPS_CORRESPONDANCE;
				}
			}

			if (distances.get(s2) > distances.get(s1) + longueurA)
			{
				distances.put(s2, distances.get(s1) + longueurA);
				predecesseurs.put(s2, s1);
			}
		}
	}
}
