package eyjafjallajokull.projettransverse.model;

import java.util.ArrayList;
import java.util.List;

public class Voyageur {

	private Station origine;
	private Station destination;
	/** Utilisé pour la simulation des déplacements : arcs restants à prendre pour arriver au bout du trajet, dans l'ordre. */
	private List<Arc> trajet;
	/** Utilisé pour la simulation des déplacements : arc sur lequel le voyageur se trouve (null au début). */
	private Arc arcActuel;
	/** Utilisé pour la simulation des déplacements : délai avant que le voyageur puisse prendre le prochain arc. */
	private int delaiProchainArc;
	/** Utilisé pour la simulation des déplacements : temps écoulé depuis le début du trajet. */
	private int tempsEcoule;
	/** Utilisé pour la simulation des déplacements : temps écoulé depuis le début du trajet. */
	private boolean estArrive;
	private int longueurTrajet;

	Voyageur(Station origine, Station destination) {
		this.origine = origine;
		this.destination = destination;
	}

	/**
	 * @return Station d'origine du voyageur.
	 */
	public Station getOrigine() {
		return origine;
	}

	/**
	 * @return Station où le voyageur veut aller.
	 */
	public Station getDestination() {
		return destination;
	}

	/**
	 * @return Temps que le voyageur a mis à arriver à destination (disponible après une simulation).
	 */
	public int getTempsTrajet() {
		return tempsEcoule;
	}
	
	/**
	 * @return Longueur du trajet que le voyageur emprunte.
	 */
	public int getLongueurTrajet() {
		return longueurTrajet;
	}

	/**
	 * @return true si le voyageur est arrivé à destination (lors d'une simulation).
	 */
	public boolean estArrive() {
		return estArrive;
	}

	@Override
	public String toString() {
		return "Voyageur [origine=" + origine + ", destination=" + destination + "]";
	}

	/**
	 * Initialise le trajet du voyageur et le place à son point de départ pour pouvoir le simuler ensuite.
	 */
	public void initialiserTrajet()
	{
		trajet = new ArrayList<Arc>(origine.getCheminCourt(destination));
		arcActuel = null;
		delaiProchainArc = 0;
		tempsEcoule = 0;
		estArrive = false;
		longueurTrajet = 0;
		for (Arc a : trajet)
		{
			longueurTrajet += a.getLongueur();
		}
	}

	/**
	 * Fait avancer le voyageur d'une unité de temps sur son trajet (s'il n'est pas encore arrivé).
	 */
	public void avancer()
	{
		if (!estArrive)
		{
			tempsEcoule++;
			if (delaiProchainArc > 0)
				delaiProchainArc--;
			else
			{
				if (trajet.isEmpty()) // S'il n'y a plus d'arcs, il est arrivé
				{
					estArrive = true;
				}
				else
				{
					Arc arcSuivant = trajet.get(0);
					boolean avancer = false;
					// Recherche de l'extrémité du prochain arc par lequel le voyageur va passer (en commun avec l'arc actuel)
					boolean entreeParExtrem1 = true; // false = extrémité 2
					if (arcActuel == null) // Si on est au départ
					{
						if (arcSuivant.getExtremite2().equals(origine) || arcSuivant.getExtremite2().equals(origine)) // Si c'est l'extrémité 2 de l'arc suivant
							entreeParExtrem1 = false;
					}
					else if (arcSuivant.getExtremite2().equals(arcActuel.getExtremite1()) || arcSuivant.getExtremite2().equals(arcActuel.getExtremite2())) // Si on va entrer par l'extrémité 2 de l'arc suivant
						entreeParExtrem1 = false;

					if (arcActuel != null && arcSuivant.getLigne().equals(arcActuel.getLigne())) // S'il ne change pas de ligne, il avance.
						avancer = true;
					else // Si c'est une correspondance ou qu'il est au point de départ, il faut qu'il y ait de la place pour monter (débit maximal)
					{
						if (entreeParExtrem1)
						{
							// S'il reste de la place pour monter, il y va
							if (arcSuivant.getEntreesExtrem1() < arcSuivant.getDebitEntreeMax())
								avancer = true;
						}
						else
						{
							// S'il reste de la place pour monter, il y va
							if (arcSuivant.getEntreesExtrem2() < arcSuivant.getDebitEntreeMax())
								avancer = true;
						}
					}

					if (avancer)
					{
						// Enregistrement de l'entrée du voyageur dans l'arc
						if (entreeParExtrem1)
							arcSuivant.ajouterEntreeExtrem1();
						else
							arcSuivant.ajouterEntreeExtrem2();

						// Passage à l'arc suivant dans le trajet
						arcActuel = arcSuivant;
						trajet.remove(0);

						// Calcul du temps avant d'entrée dans le prochain arc
						delaiProchainArc = (int) Math.round(arcActuel.getLongueur());
						if (!trajet.isEmpty()) // S'il n'est pas encore sur le dernier arc
						{
							arcSuivant = trajet.get(0);
							if (arcSuivant != null && arcActuel.getLigne().equals(arcSuivant.getLigne())) // Si ce sera une correspondance, délai supplémentaire
								delaiProchainArc += Reseau.TEMPS_CORRESPONDANCE;
						}
					}
				}
			}
		}
	}
}
