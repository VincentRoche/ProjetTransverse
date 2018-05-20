package eyjafjallajokull.projettransverse.model;

import java.util.ArrayList;
import java.util.List;

import eyjafjallajokull.projettransverse.view.FenetrePlan;

public class IARoche extends IACreationLignes {

	/**
	 * Les virages formant un angle inférieur à celui-ci seront interdits sur une ligne.
	 */
	public final int ANGLE_MIN_LIGNE = 30;
	/**
	 * Il ne peut y avoir aucun angle inférieur à celui-là, même si les lignes sont différentes.
	 */
	public final int ANGLE_MAX = 20;
	/**
	 * Importance du flux dans le choix d'un arc pour prolonger une ligne.
	 */
	private static final double IMPORTANCE_FLUX = 1;
	/**
	 * Importance de l'angle avec le dernier arc dans le choix d'un arc pour prolonger une ligne.
	 */
	private static final double IMPORTANCE_ANGLE = 1;
	/**
	 * Importance du nombre de correspondances dans le choix d'un arc pour prolonger une ligne.
	 */
	private static final double IMPORTANCE_CORRESPONDANCES = 0.5;

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
					if (longueur < longueurMax / r.getStations().size())
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
							if (voisin && (Math.abs(angle) < ANGLE_MAX/* || Math.abs(angle) > 360 - ANGLE_MAX*/))
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
					double scoreMax = 0;
					Station autreExtremite = arcFluxMax.getAutreExtremite(extremite);
					
					// Calcul des valeurs maximale parmi les angles voisins
					int fluxMax = 0;
					int nbVoisinsMax = 0;
					for (Arc arcVoisin : r.getArcsVoisins(extremite))
					{
						if (!arcVoisin.equals(arcFluxMax) && !reseau.getArcs().contains(arcVoisin)) // On ne veut pas remplacer un arc existant
						{
							fluxMax = Math.max(fluxMax, arcVoisin.getFlux());
							nbVoisinsMax = Math.max(nbVoisinsMax, reseau.getArcsVoisins(arcVoisin.getAutreExtremite(extremite)).size());
						}
					}
					// Sélection de l'arc voisin au plus grand flux et d'angle supérieur à ANGLE_MIN
					for (Arc arcVoisin : r.getArcsVoisins(extremite))
					{
						if (!arcVoisin.equals(arcFluxMax) && !reseau.getArcs().contains(arcVoisin)) // On ne veut pas remplacer un arc existant
						{
							Station extremiteVoisin = arcVoisin.getAutreExtremite(extremite);

							double angle = calculerAngle(autreExtremite, extremite, extremiteVoisin);
							double scoreAngle = (angle - ANGLE_MIN_LIGNE) / ANGLE_MIN_LIGNE;

							double scoreFlux = (double) arcVoisin.getFlux() / fluxMax;

							double scoreNbVoisins = (nbVoisinsMax == 0) ? 1 : 1 - (double) reseau.getArcsVoisins(extremiteVoisin).size() / nbVoisinsMax;
							
							double scoreArc = IMPORTANCE_FLUX * scoreFlux + IMPORTANCE_ANGLE * scoreAngle + IMPORTANCE_CORRESPONDANCES * scoreNbVoisins;
							if (angle >= ANGLE_MIN_LIGNE && (nouvelArcMax == null || scoreMax < scoreArc))
							{
								nouvelArcMax = arcVoisin;
								scoreMax = scoreArc;
							}
						}
					}

					if (nouvelArcMax != null)
					{
						try {
							reseau.ajouterArc(nouvelArcMax.getExtremite1(), nouvelArcMax.getExtremite2(), ligne);
							fenetre.majReseau(reseau);
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

	/**
	 * Calcule l'angle entre deux arcs ayant le sommet milieu en commun.
	 * @param s1 Sommet 1
	 * @param milieu Sommet en commun entre les deux arcs ("pointe" de l'angle)
	 * @param s2 Sommet 2
	 * @return Angle Valeur absolue de l'angle.
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
