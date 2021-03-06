package eyjafjallajokull.projettransverse.model;

import java.util.ArrayList;
import java.util.List;

import eyjafjallajokull.projettransverse.view.FenetrePlan;
import eyjafjallajokull.projettransverse.view.UtilitairesAffichage;

public class IACriteresPonderes extends IACreationLignes {

	/**
	 * Réseau qui contient tous les arcs possibles.
	 */
	Reseau reseauComplet;

	/**
	 * Les virages formant un angle inférieur à celui-ci seront préférables sur une ligne.
	 */
	public static final int ANGLE_MIN_IDEAL = 125;
	/**
	 * Les virages formant un angle inférieur à celui-ci seront interdits sur une ligne.
	 */
	public static final int ANGLE_MIN_REQUIS = 90;
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
	public static final double IMPORTANCE_ANGLE = 3;
	/**
	 * Importance du nombre de correspondances dans le choix d'un arc pour prolonger une ligne.
	 */
	public static final double IMPORTANCE_CORRESPONDANCES = 1;
	/**
	 * Importance de la longueur dans le choix d'un arc pour prolonger une ligne.
	 */
	private static final double IMPORTANCE_LONGUEUR = 0.25;

	public IACriteresPonderes(Reseau reseau, int nbLignes, int longueurMax, FenetrePlan fenetre) {
		super(reseau, nbLignes, longueurMax, fenetre);
	}

	@Override
	public Reseau placerLignes() {
		Ligne.reinitialiserNb();

		////////////////////////////////////////////////////////////////////////////////////////
		// Etape 1 : création d'un réseau reliant toutes les stations, sans arcs qui se croisent

		try {
			reseauComplet = reseau.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}

		List<Station> stations = reseauComplet.getStations();
		for (int i=0; i < stations.size(); i++)
		{
			Station s1 = stations.get(i);
			List<Arc> voisinsS1 = reseauComplet.getArcsVoisins(s1);

			// Tentative d'ajout d'un arc entre s1 et toutes les autres stations
			for (int j = i + 1; j < stations.size(); j++)
			{
				Station s2 = stations.get(j);

				if (!s1.equals(s2))
				{
					List<Arc> voisinsS2 = reseauComplet.getArcsVoisins(s2);

					double longueur = s1.distance(s2);
					if (longueur < longueurMax / (reseauComplet.getStations().size() / 3))
					{
						// Recherche dans les arcs déjà ajoutés d'arcs qui croisent celui qu'on veut ajouter ou dont l'angle est trop faible
						boolean ajouterArc = true;
						List<Arc> arcsASupprimer = new ArrayList<Arc>();
						for (Arc a : reseauComplet.getArcs())
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
								reseauComplet.supprimerArc(a);
							}

							// Ajout du nouvel arc
							try {
								reseauComplet.ajouterArc(s1, s2, null);
							} catch (TropDArcsDeLaMemeLigneException e) {
								e.printStackTrace();
							} catch (ArcDejaExistantException e) {
								// Rien
							}
						}
					}
				}
			}
			fenetre.majReseau(reseauComplet);
		}

		// Erreur s'il y a des stations non reliées
		if (!reseauComplet.stationsIsolees().isEmpty())
		{
			UtilitairesAffichage.messageErreur("Il est impossible de relier les stations car la longueur maximale du réseau que vous avez entrée est trop faible. Veuillez modifier vos critères.");
			return null;
		}

		////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Etape 2 : Calcul des chemins les plus courts et du flux de chaque arc (nombre de voyageurs qui l'empruntent).

		reseauComplet.calculerCheminsCourts();


		///////////////////////////////////////////////////////////////////////////////////////////////////////
		// Etape 3 : Placement des lignes sur les arcs aux flux les plus élevés, sans faire de virages absurdes

		for (int i = 0; i < nbLignes; i++)
		{
			Ligne ligne = reseau.ajouterLigne();
			boolean lignePlacee = placerLigne(ligne);
			if (!lignePlacee)
			{
				UtilitairesAffichage.messageErreur("Toutes les lignes n'ont pas pu être placés, il y en a déjà suffisamment.");
				break;
			}
			else
				lierStationsProches();
		}

		///////////////////////////////////////////////////////////////////////////////////
		// Etape 4 : Fusion des lignes qui sont mises bout à bout pour économiser une ligne

		fusionnerLignes();

		///////////////////////////////////////////////////////////////////////////
		// Etape 5 : Poursuite des lignes proches de stations pas encore raccordées

		lierStationsIsolees();


		return reseau;
	}

	/**
	 * Place une nouvelle ligne à l'endroit idéal.
	 * @param ligne Ligne à placer.
	 * @return true si ça a fonctionné, false si elle n'a pas pu être placée.
	 */
	private boolean placerLigne(Ligne ligne)
	{
		// Ajout du premier arc de flux maximal
		Arc premierArc = null;
		for (Arc a : reseauComplet.getArcs())
		{
			if ((premierArc == null || premierArc.getFlux() < a.getFlux()) && !reseau.getArcs().contains(a)
					&& (reseau.getArcsVoisins(a.getExtremite1()).isEmpty() || reseau.getArcsVoisins(a.getExtremite2()).isEmpty()))
				premierArc = a;
		}

		if (premierArc == null)
		{
			return false;
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
			continuerLigne(ligne, extremite, premierArc);
		}
		return true;
	}

	private void continuerLigne(Ligne ligne, Station extremite, Arc arcPreced) {
		double longueurMoyenne = reseauComplet.getLongueur() / reseauComplet.getArcs().size();
		double longueurMaxLigne = (double) longueurMax / nbLignes;
		Arc arcFluxMax = arcPreced;

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
			for (Arc arcVoisin : reseauComplet.getArcsVoisins(extremite))
			{
				if (!arcVoisin.equals(arcFluxMax) && !reseau.getArcs().contains(arcVoisin)) // On ne veut pas remplacer un arc existant
				{
					fluxMax = Math.max(fluxMax, arcVoisin.getFlux());
					nbVoisinsMax = Math.max(nbVoisinsMax, reseau.getArcsVoisins(arcVoisin.getAutreExtremite(extremite)).size());
					longMax = Math.max(longMax, arcVoisin.getLongueur());
				}
			}
			// Sélection de l'arc voisin au plus grand flux et d'angle supérieur à ANGLE_MIN
			for (Arc arcVoisin : reseauComplet.getArcsVoisins(extremite))
			{
				if (!arcVoisin.equals(arcFluxMax) && !reseau.getArcs().contains(arcVoisin)) // On ne veut pas remplacer un arc existant
				{
					Station extremiteVoisin = arcVoisin.getAutreExtremite(extremite);

					double angle = calculerAngle(autreExtremite, extremite, extremiteVoisin);
					double scoreAngle = (angle - ANGLE_MIN_IDEAL) / ANGLE_MIN_IDEAL;

					double scoreFlux = (double) (arcVoisin.getFlux() - reseauComplet.getFluxMoyen() / 2) / fluxMax;

					double scoreNbVoisins = (nbVoisinsMax == 0) ? 1 : 1 - (double) reseau.getArcsVoisins(extremiteVoisin).size() / nbVoisinsMax;

					double scoreLongueur = (longueurMoyenne - arcVoisin.getLongueur()) / longMax;

					double scoreArc = IMPORTANCE_FLUX * scoreFlux + IMPORTANCE_ANGLE * scoreAngle + IMPORTANCE_CORRESPONDANCES * scoreNbVoisins + IMPORTANCE_LONGUEUR * scoreLongueur;
					if (scoreArc >= 0 && angle >= ANGLE_MIN_REQUIS && (nouvelArcMax == null || scoreMax < scoreArc))
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
					arcFluxMax = nouvelArcMax;
					extremite = nouvelArcMax.getAutreExtremite(extremite);

					// Enregistrement des correspondances
					List<Arc> voisins = reseau.getArcsVoisins(arc.getExtremite1());
					voisins.addAll(reseau.getArcsVoisins(arc.getExtremite2()));
				} catch (TropDArcsDeLaMemeLigneException | ArcDejaExistantException e) {
					stop = true;
				}
				if (reseau.longueurLigne(ligne) > longueurMaxLigne)
				{
					stop = true;
				}
			}
			else
			{
				stop = true;
				double longueurRestante = ((double) longueurMax / nbLignes) - reseau.longueurLigne(ligne);
				if (longueurRestante > 0)
					longueurMaxLigne += longueurRestante;
			}
		}
		fenetre.majReseau(reseau);
	}

	private void fusionnerLignes() {
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
					}
					placerLigne(ligneRetiree);
				}
			}
		}
		fenetre.majReseau(reseau);
	}

	private void lierStationsIsolees()
	{
		List<Station> stationsIsolees = reseau.stationsIsolees();
		int iterationsSansModif = 0;
		int nbStations = reseau.getStations().size();
		while (!stationsIsolees.isEmpty() && iterationsSansModif < nbStations)
		{
			iterationsSansModif++;

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
						if (calculerAngle(isolee, s, arcTerminus.getAutreExtremite(s)) >= ANGLE_MIN_REQUIS && distPlusProche > distance && reseauComplet.getArcs().contains(temp))
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
					continuerLigne(lignePlusProche, isoleePlusProche, nouvelArc);
					iterationsSansModif = 0;
				} catch (TropDArcsDeLaMemeLigneException | ArcDejaExistantException e) {
					// Rien
				}
			}
			else
			{
				lierStationsProches();
			}

			fusionnerLignes();

			stationsIsolees = reseau.stationsIsolees();
		}
	}

	private void lierStationsProches()
	{
		List<Station> stationsIsolees = reseau.stationsIsolees();

		// Recherche d'une station proche avec une ligne qui y passe
		Station isoleePlusProche = null;
		double distPlusProche = Double.MAX_VALUE;
		Arc arcPlusProche = null;
		for (Station isolee : stationsIsolees)
		{
			for (Station s : reseau.getStations())
			{
				List<Arc> voisins = reseau.getArcsVoisins(s);
				if (!voisins.isEmpty())
				{
					Arc temp = new Arc(isolee, s, null);
					double distance = temp.getLongueur();
					if (distPlusProche > distance)
					{
						// Sélection de l'arc le plus proche parmi les voisins
						Arc arcChoisi = null;
						double sommeDist = Double.MAX_VALUE;
						for (Arc a : reseau.getArcsVoisins(s))
						{
							double dist = a.getExtremite1().distance(isolee) + a.getExtremite2().distance(isolee);
							if (sommeDist > dist && calculerAngle(a.getExtremite1(), isolee, a.getExtremite2()) > ANGLE_MIN_REQUIS)
							{
								sommeDist = dist;
								arcChoisi = a;
							}
						}
						if (arcChoisi != null)
						{
							arcPlusProche = arcChoisi;
							isoleePlusProche = isolee;
							distPlusProche = distance;
						}
					}
				}
			}
		}
		if (arcPlusProche != null)
		{
			try {
				reseau.supprimerArc(arcPlusProche);
				reseau.ajouterArc(arcPlusProche.getExtremite1(), isoleePlusProche, arcPlusProche.getLigne());
				reseau.ajouterArc(arcPlusProche.getExtremite2(), isoleePlusProche, arcPlusProche.getLigne());
				fenetre.majReseau();
			} catch (TropDArcsDeLaMemeLigneException | ArcDejaExistantException e) {
				e.printStackTrace();
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
