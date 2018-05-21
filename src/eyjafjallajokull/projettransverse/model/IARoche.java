package eyjafjallajokull.projettransverse.model;

import java.util.ArrayList;
import java.util.List;

import eyjafjallajokull.projettransverse.view.FenetrePlan;

public class IARoche extends IACreationLignes {

	/**
	 * Les virages formant un angle inférieur à celui-ci seront préférables sur une ligne.
	 */
	public static final int ANGLE_MIN_IDEAL = 125;
	/**
	 * Les virages formant un angle inférieur à celui-ci seront interdits sur une ligne.
	 */
	public static final int ANGLE_MIN_REQUIS = 80;
	/**
	 * Il ne peut y avoir aucun angle inférieur à celui-là, même si les lignes sont différentes.
	 */
	public static final int ANGLE_MAX = 20;
	/**
	 * Importance du flux dans le choix d'un arc pour prolonger une ligne.
	 */
	public static final double IMPORTANCE_FLUX = 1;
	/**
	 * Importance de l'angle avec le dernier arc dans le choix d'un arc pour prolonger une ligne.
	 */
	public static final double IMPORTANCE_ANGLE = 2.25;
	/**
	 * Importance du nombre de correspondances dans le choix d'un arc pour prolonger une ligne.
	 */
	public static final double IMPORTANCE_CORRESPONDANCES = 1;
	/**
	 * Importance de la longueur dans le choix d'un arc pour prolonger une ligne.
	 */
	private static final double IMPORTANCE_LONGUEUR = 2;

	public IARoche(Reseau reseau, int nbLignes, int longueurMax, FenetrePlan fenetre) {
		super(reseau, nbLignes, longueurMax, fenetre);
	}

	@Override
	public Reseau placerLignes() {
		////////////////////////////////////////////////////////////////////////////////////////
		// Etape 1 : création d'un réseau reliant toutes les stations, sans arcs qui se croisent

		Reseau r; // Réseau à remplir
		try {
			r = reseau.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}

		List<Station> stations = r.getStations();
		for (int i=0; i < stations.size(); i++)
		{
			Station s1 = stations.get(i);
			List<Arc> voisinsS1 = r.getArcsVoisins(s1);

			// Tentative d'ajout d'un arc entre s1 et toutes les autres stations
			for (int j = i + 1; j < stations.size(); j++)
			{
				Station s2 = stations.get(j);

				if (!s1.equals(s2))
				{
					List<Arc> voisinsS2 = r.getArcsVoisins(s2);

					double longueur = s1.distance(s2);
					if (longueur < longueurMax / (r.getStations().size() / 3))
					{
						// Recherche dans les arcs déjà ajoutés d'arcs qui croisent celui qu'on veut ajouter ou dont l'angle est trop faible
						boolean ajouterArc = true;
						List<Arc> arcsASupprimer = new ArrayList<Arc>();
						for (Arc a : r.getArcs())
						{
							// Si ça se croise
							if (!a.getExtremite1().equals(s1) && !a.getExtremite1().equals(s2) && !a.getExtremite2().equals(s1) && !a.getExtremite2().equals(s2))
							{
								if (new Arc(s1, s2, null).croise(a))
								{
									// On abandonne le plus long
									if (a.getLongueur() >= longueur)
									{
										arcsASupprimer.add(a);
									}
									else // Si celui qu'on veut ajouter est plus long, bah on ne l'ajoute pas...
									{
										ajouterArc = false;
										break;
									}
								}
							}

							// Recherche de si les arcs se touchent et calcul de l'angle entre eux
							double angle = 0;
							boolean voisin = false;
							if (voisinsS1.contains(a))
							{
								voisin = true;
								angle = calculerAngle(s2, s1, a.getAutreExtremite(s1));
							}
							else if (voisinsS2.contains(a))
							{
								voisin = true;
								angle = calculerAngle(s1, s2, a.getAutreExtremite(s2));
							}
							if (voisin && (angle < ANGLE_MAX))
							{
								// On abandonne le plus long
								if (a.getLongueur() >= longueur)
								{
									arcsASupprimer.add(a);
								}
								else // Si celui qu'on veut ajouter est plus long, bah on ne l'ajoute pas...
								{
									ajouterArc = false;
									break;
								}
							}
						}
						if (ajouterArc)
						{
							// Suppression des arcs qu'il croise
							for (Arc a : arcsASupprimer)
							{
								r.supprimerArc(a);
							}

							// Ajout du nouvel arc
							try {
								r.ajouterArc(s1, s2, null);
							} catch (TropDArcsDeLaMemeLigneException e) {
								e.printStackTrace();
							} catch (ArcDejaExistantException e) {
								// Rien
							}
						}
					}
				}
			}
			fenetre.majReseau(r);
		}

		////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Etape 2 : Calcul des chemins les plus courts et du flux de chaque arc (nombre de voyageurs qui l'empruntent).

		r.calculerCheminsCourts();


		///////////////////////////////////////////////////////////////////////////////////////////////////////
		// Etape 3 : Placement des lignes sur les arcs aux flux les plus élevés, sans faire de virages absurdes

		// Valeurs utiles aux calculs

		// Pour chaque ligne
		for (int i = 0; i < nbLignes; i++)
		{
			Ligne ligne = reseau.ajouterLigne();
			placerLigne(ligne, r);
		}

		///////////////////////////////////////////////////////////////////////////////////
		// Etape 4 : Fusion des lignes qui sont mises bout à bout pour économiser une ligne

		// Recherche de stations qui ont 2 terminus
		for (Station s : reseau.getStations())
		{
			// Comptage des arcs
			List<Arc> terminus = reseau.getArcsTerminus(s);
			if (terminus.size() >= 2)
			{
				// Fusion des 2 lignes
				Arc arcR = terminus.get(1);
				Arc arcC = terminus.get(0);
				Ligne ligneRetiree = arcR.getLigne();
				Ligne ligneConservee = arcC.getLigne();
				if (calculerAngle(arcR.getAutreExtremite(s), s, arcC.getAutreExtremite(s)) >= ANGLE_MIN_IDEAL)
				{
					for (Arc a : reseau.getArcs(ligneRetiree))
					{
						a.setLigne(ligneConservee);
						fenetre.majReseau(reseau);
					}
					placerLigne(ligneRetiree, r);
				}
			}
		}

		///////////////////////////////////////////////////////////////////////////
		// Etape 5 : Poursuite des lignes proches de stations pas encore raccordées

		List<Station> stationsIsolees = reseau.stationsIsolees();
		while (!stationsIsolees.isEmpty())
		{
			// Recherche de la station isolée la plus proche d'un terminus
			Station isoleePlusProche = null;
			Station plusProche = null;
			Ligne lignePlusProche = null;
			double distPlusProche = Integer.MAX_VALUE;
			for (Station isolee : stationsIsolees)
			{
				// Recherche de terminus
				for (Station s : reseau.getStations())
				{
					List<Arc> terminus = reseau.getArcsTerminus(s);
					if (!terminus.isEmpty())
					{
						Arc arcTerminus = terminus.get(0);
						Arc temp = new Arc(isolee, s, null);
						double distance = temp.getLongueur();
						if (calculerAngle(isolee, s, arcTerminus.getAutreExtremite(s)) >= ANGLE_MIN_REQUIS && distPlusProche > distance && r.getArcs().contains(temp))
						{
							isoleePlusProche = isolee;
							plusProche = s;
							distPlusProche = distance;
							lignePlusProche = arcTerminus.getLigne();
						}
					}
				}
			}

			if (isoleePlusProche != null)
			{
				try {
					Arc nouvelArc = reseau.ajouterArc(plusProche, isoleePlusProche, lignePlusProche);
					fenetre.majReseau(reseau);
					continuerLigne(lignePlusProche, isoleePlusProche, nouvelArc, r);
				} catch (TropDArcsDeLaMemeLigneException | ArcDejaExistantException e) {
					// Rien
				}
			}
			else
			{
				// Suppression de la ligne la plus courte pour essayer de la replacer
				Ligne plusCourte = null;
				double longueur = 0;
				for (Ligne l : reseau.getLignes())
				{
					double longueurLigne = reseau.longueurLigne(l);
					if (plusCourte == null || longueur > longueurLigne)
					{
						plusCourte = l;
						longueur = longueurLigne;
					}
				}
				for (Arc a : reseau.getArcs(plusCourte))
				{
					reseau.supprimerArc(a);
				}
				fenetre.majReseau(reseau);
				placerLigne(plusCourte, r);
			}

			stationsIsolees = reseau.stationsIsolees();
		}


		return reseau;
	}

	void placerLigne(Ligne ligne, Reseau reseauComplet)
	{
		Reseau r = reseauComplet;

		// Ajout du premier arc de flux maximal
		Arc premierArc = null;
		for (Arc a : r.getArcs())
		{
			if ((premierArc == null || premierArc.getFlux() < a.getFlux()) && !reseau.getArcs().contains(a)
					&& reseau.getArcsVoisins(a.getExtremite1()).isEmpty() && reseau.getArcsVoisins(a.getExtremite2()).isEmpty())
				premierArc = a;
		}
		try {
			reseau.ajouterArc(premierArc.getExtremite1(), premierArc.getExtremite2(), ligne);
		} catch (TropDArcsDeLaMemeLigneException | ArcDejaExistantException e) {
			e.printStackTrace();
		}

		// Ajout d'arcs à chaque bout du premier arc
		Station[] extremites = {premierArc.getExtremite1(), premierArc.getExtremite2()};
		for (Station extremite : extremites)
		{
			continuerLigne(ligne, extremite, premierArc, r);
		}
	}

	private void continuerLigne(Ligne ligne, Station extremite, Arc arcPreced, Reseau reseauComplet) {
		double longueurMoyenne = reseauComplet.getLongueur() / reseauComplet.getArcs().size();
		Arc arcFluxMax = arcPreced;
		Reseau r = reseauComplet;

		boolean stop = false;
		while (!stop) // Jusqu'à ce qu'on ne puisse plus continuer la ligne
		{
			Arc nouvelArcMax = null;
			double scoreMax = 0;
			Station autreExtremite = arcFluxMax.getAutreExtremite(extremite);

			// Calcul des valeurs maximale parmi les angles voisins
			int fluxMax = 0;
			int nbVoisinsMax = 0;
			double longMax = 0;
			for (Arc arcVoisin : r.getArcsVoisins(extremite))
			{
				if (!arcVoisin.equals(arcFluxMax) && !reseau.getArcs().contains(arcVoisin)) // On ne veut pas remplacer un arc existant
				{
					fluxMax = Math.max(fluxMax, arcVoisin.getFlux());
					nbVoisinsMax = Math.max(nbVoisinsMax, reseau.getArcsVoisins(arcVoisin.getAutreExtremite(extremite)).size());
					longMax = Math.max(longMax, arcVoisin.getLongueur());
				}
			}
			// Sélection de l'arc voisin au plus grand flux et d'angle supérieur à ANGLE_MIN
			for (Arc arcVoisin : r.getArcsVoisins(extremite))
			{
				if (!arcVoisin.equals(arcFluxMax) && !reseau.getArcs().contains(arcVoisin)) // On ne veut pas remplacer un arc existant
				{
					Station extremiteVoisin = arcVoisin.getAutreExtremite(extremite);

					// Recherche de si cette station a une correspondance avec une ligne déjà rencontrée
					boolean correspondanceDejaVue = false;
					for (Arc a : reseau.getArcsVoisins(extremiteVoisin))
					{
						if (ligne.getCorrespondances().contains(a.getLigne()))
							correspondanceDejaVue = true;
					}

					double angle = calculerAngle(autreExtremite, extremite, extremiteVoisin);
					double scoreAngle = (angle - ANGLE_MIN_IDEAL) / ANGLE_MIN_IDEAL;

					double scoreFlux = (double) (arcVoisin.getFlux() - r.getFluxMoyen() / 2) / fluxMax;

					double scoreNbVoisins = (nbVoisinsMax == 0) ? 1 : 1 - (double) reseau.getArcsVoisins(extremiteVoisin).size() / nbVoisinsMax;

					double scoreLongueur = (longueurMoyenne - arcVoisin.getLongueur()) / longMax;

					double scoreArc = IMPORTANCE_FLUX * scoreFlux + IMPORTANCE_ANGLE * scoreAngle + IMPORTANCE_CORRESPONDANCES * scoreNbVoisins + IMPORTANCE_LONGUEUR * scoreLongueur;
					if (!correspondanceDejaVue && scoreArc > 0 && angle >= ANGLE_MIN_REQUIS && (nouvelArcMax == null || scoreMax < scoreArc))
					{
						nouvelArcMax = arcVoisin;
						scoreMax = scoreArc;
					}
				}
			}

			if (nouvelArcMax != null)
			{
				try {
					Arc arc = reseau.ajouterArc(nouvelArcMax.getExtremite1(), nouvelArcMax.getExtremite2(), ligne);
					fenetre.majReseau(reseau);
					arcFluxMax = nouvelArcMax;
					extremite = nouvelArcMax.getAutreExtremite(extremite);

					// Enregistrement des correspondances
					List<Arc> voisins = reseau.getArcsVoisins(arc.getExtremite1());
					voisins.addAll(reseau.getArcsVoisins(arc.getExtremite2()));
					for (Arc v : voisins)
					{
						ligne.ajouterCorrespondance(v.getLigne());
					}
				} catch (TropDArcsDeLaMemeLigneException | ArcDejaExistantException e) {
					stop = true;
				}
				if (reseau.longueurLigne(ligne) > (double) longueurMax / nbLignes)
				{
					stop = true;
				}
			}
			else
			{
				stop = true;
			}
		}
	}

	/**
	 * Calcule l'angle entre deux arcs ayant le sommet milieu en commun.
	 * @param s1 Sommet 1
	 * @param milieu Sommet en commun entre les deux arcs ("pointe" de l'angle)
	 * @param s2 Sommet 2
	 * @return Valeur absolue de l'angle.
	 */
	private double calculerAngle(Station s1, Station milieu, Station s2)
	{
		int xMilieu = milieu.getCoordonneeX();
		int yMilieu = milieu.getCoordonneeY();
		int x1 = s1.getCoordonneeX();
		int y1 = s1.getCoordonneeY();
		int x2 = s2.getCoordonneeX();
		int y2 = s2.getCoordonneeY();
		double angle1 = Math.atan2(y1 - yMilieu, x1 - xMilieu);
		double angle2 = Math.atan2(y2 - yMilieu, x2 - xMilieu);
		double angle = Math.abs(Math.toDegrees(angle1 - angle2));
		if (angle > 180)
			angle = 360 - angle;
		return angle;
	}

}
