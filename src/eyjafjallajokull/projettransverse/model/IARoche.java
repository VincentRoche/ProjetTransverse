package eyjafjallajokull.projettransverse.model;

import eyjafjallajokull.projettransverse.view.FenetrePlan;

public class IARoche extends IACreationLignes {

	/**
	 * Les virages formant un angle inférieur à celui-ci seront interdits.
	 */
	public final int ANGLE_MIN = 90;

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

		for (Station s1 : r.getStations())
		{
			// Tentative d'ajout d'un arc entre s1 et toutes les autres stations
			for (Station s2 : r.getStations())
			{
				if (!s1.equals(s2))
				{
					int x0 = s1.getCoordonneeX();
					int y0 = s1.getCoordonneeY();
					int x1 = s2.getCoordonneeX();
					int y1 = s2.getCoordonneeY();
					double longueur = s1.distance(s2);
					// Recherche dans les arcs déjà ajoutés d'arcs qui croisent celui qu'on veut ajouter
					boolean ajouterArc = true;
					for (Arc a : r.getArcs())
					{
						if (!a.getExtremite1().equals(s1) && !a.getExtremite1().equals(s2) && !a.getExtremite2().equals(s1) && !a.getExtremite2().equals(s2))
						{
							int x2 = a.getExtremite1().getCoordonneeX();
							int y2 = a.getExtremite1().getCoordonneeY();
							int x3 = a.getExtremite2().getCoordonneeX();
							int y3 = a.getExtremite2().getCoordonneeY();

							// Si ça se croise
							if (((x2-x0)*(y1-y0) - (y2-y0)*(x1-x0)) * ((x3-x0)*(y1-y0) - (y3-y0)*(x1-x0)) < 0 && ((x0-x2)*(y3-y2) - (y0-y2)*(x3-x2)) * ((x1-x2)*(y3-y2) - (y1-y2)*(x3-x2)) < 0)
							{
								// On abandonne le plus long
								if (a.getLongueur() >= longueur)
									r.supprimerArc(a);
								else // Si celui qu'on veut ajouter est plus long, bah on ne l'ajoute pas...
								{
									ajouterArc = false;
									break;
								}
							}
						}
					}
					if (ajouterArc)
					{
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


		////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Etape 2 : Calcul des chemins les plus courts et du flux de chaque arc (nombre de voyageurs qui l'empruntent).

		r.calculerCheminsCourts();


		///////////////////////////////////////////////////////////////////////////////////////////////////////
		// Etape 3 : Placement des lignes sur les arcs aux flux les plus élevés, sans faire de virages absurdes

		// Pour chaque ligne
		for (int i = 0; i < nbLignes; i++)
		{
			Ligne ligne = reseau.ajouterLigne();

			// Ajout du premier arc de flux maximal
			Arc premierArc = null;
			for (Arc a : r.getArcs())
			{
				if ((premierArc == null || premierArc.getFlux() < a.getFlux()) && !reseau.getArcs().contains(a))
					premierArc = a;
			}
			try {
				reseau.ajouterArc(premierArc.getExtremite1(), premierArc.getExtremite2(), ligne);
				//fenetre.majReseau();
			} catch (TropDArcsDeLaMemeLigneException | ArcDejaExistantException e) {
				e.printStackTrace();
				break;
			}

			// Ajout d'arcs à chaque bout du premier arc
			Station[] extremites = {premierArc.getExtremite1(), premierArc.getExtremite2()};
			for (Station extremite : extremites)
			{
				Arc arcFluxMax = premierArc;

				boolean stop = false;
				while (!stop) // Jusqu'à ce qu'on ne puisse plus continuer la ligne
				{
					Arc nouvelArcMax = null;
					Station autreExtremite = arcFluxMax.getAutreExtremite(extremite);
					// Sélection de l'arc voisin au plus grand flux et d'angle supérieur à ANGLE_MIN
					for (Arc arcVoisin : r.getArcsVoisins(extremite))
					{
						if (!arcVoisin.equals(arcFluxMax) && !reseau.getArcs().contains(arcVoisin)) // On ne veut pas remplacer un arc existant
						{
							Station extremiteVoisin = arcVoisin.getAutreExtremite(extremite);
							// Calcul de l'angle
							int xExtremite = extremite.getCoordonneeX();
							int yExtremite = extremite.getCoordonneeY();
							int xAutreExtremite = autreExtremite.getCoordonneeX();
							int yAutreExtremite = autreExtremite.getCoordonneeY();
							int xVoisin = extremiteVoisin.getCoordonneeX();
							int yVoisin = extremiteVoisin.getCoordonneeY();
							double angle1 = Math.atan2(yAutreExtremite - yExtremite, xAutreExtremite - xExtremite);
							double angle2 = Math.atan2(yVoisin - yExtremite, xVoisin - xExtremite);
							double angle = Math.abs(Math.toDegrees(angle1 - angle2));

							if (angle >= ANGLE_MIN && angle <= 360 - ANGLE_MIN)
							{
								if (nouvelArcMax == null || nouvelArcMax.getFlux() < arcVoisin.getFlux())
								{
									nouvelArcMax = arcVoisin;
								}
							}
						}
					}

					if (nouvelArcMax != null)
					{
						try {
							reseau.ajouterArc(nouvelArcMax.getExtremite1(), nouvelArcMax.getExtremite2(), ligne);
							//fenetre.majReseau();
							arcFluxMax = nouvelArcMax;
							extremite = nouvelArcMax.getAutreExtremite(extremite);
						} catch (TropDArcsDeLaMemeLigneException | ArcDejaExistantException e) {
							stop = true;
						}
					}
					else
					{
						stop = true;
					}
				}
			}
		}


		return reseau;
	}

}
