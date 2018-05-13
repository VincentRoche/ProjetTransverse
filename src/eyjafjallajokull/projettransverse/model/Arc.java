package eyjafjallajokull.projettransverse.model;

/**
 * Représente un arc entre deux stations, un morceau de ligne.
 */
public class Arc {

	private final Station extremite1;
	private final Station extremite2;
	private final Ligne ligne;

	/** Utilisé pour la simulation des déplacements : nombre de voyageurs entrés sur l'arc par l'extrémité 1 dans l'unité de temps actuelle. */
	private int entreesExtrem1;
	/** Utilisé pour la simulation des déplacements : nombre de voyageurs entrés sur l'arc par l'extrémité 2 dans l'unité de temps actuelle. */
	private int entreesExtrem2;
	/** Utilisé pour la simulation des déplacements : nombre maximal de voyageurs pouvant entrer dans une extrémité de l'arc en une unité de temps. */
	private int flux;
	private final static int DEBIT_ENTREE_MAX = 1; 
	
	Arc(Station extremite1, Station extremite2, Ligne ligne) {
		this.extremite1 = extremite1;
		this.extremite2 = extremite2;
		this.ligne = ligne;
	}

	/**
	 * @return Distance entre les deux extrémités de l'arc.
	 */
	public double getLongueur()
	{
		return extremite1.distance(extremite2);
	}
	
	/**
	 * @return L'emplacement source.
	 */
	public Station getExtremite1() {
		return extremite1;
	}
	
	/**
	 * @return L'emplacement cible.
	 */
	public Station getExtremite2() {
		return extremite2;
	}

	/**
	 * @return Ligne correspondante à l'arc.
	 */
	public Ligne getLigne() {
		return ligne;
	}

	/**
	 * @return Nombre de voyageurs entrés dans l'arc par l'extrémité 1 depuis la dernière remise à 0.
	 */
	public int getEntreesExtrem1() {
		return entreesExtrem1;
	}
	
	/**
	 * Ajoute un voyageur au comptage d'entrées dans l'arc par l'extrémité 1.
	 */
	public void ajouterEntreeExtrem1() {
		entreesExtrem1++;
	}

	/**
	 * @return Nombre de voyageurs entrés dans l'arc par l'extrémité 2 depuis la dernière remise à 0.
	 */
	public int getEntreesExtrem2() {
		return entreesExtrem2;
	}
	
	/**
	 * Ajoute un voyageur au comptage d'entrées dans l'arc par l'extrémité 2.
	 */
	public void ajouterEntreeExtrem2() {
		entreesExtrem2++;
	}
	
	/**
	 * Remet à 0 les compteurs de voyageurs entrés dans l'arc.
	 */
	public void reinitialiserEntrees() {
		entreesExtrem1 = 0;
		entreesExtrem2 = 0;
	}
	
	/**
	 * @return Nombre maximal de voyageurs pouvant entrer dans une extrémité de l'arc en une unité de temps.
	 */
	public int getDebitEntreeMax() {
		return DEBIT_ENTREE_MAX;
	}

	/**
	 * @return Nombre de voyageurs passant par l'arc (calculé en même temps que les plus courts chemins)
	 */
	public int getFlux() {
		return flux;
	}
	
	/**
	 * Ajoute un voyageur au flux de l'arc.
	 */
	public void incrementerFlux()
	{
		flux++;
	}
	
	/**
	 * Remet le flux de l'arc à 0.
	 */
	public void reinitialiserFlux()
	{
		flux = 0;
	}
	
	/**
	 * Retourne l'extrémité qui n'est pas la station donnée.
	 * @param s Une des extrémités de l'arc.
	 * @return L'autre extrémité, ou null si la station donnée n'appartient pas à l'arc.
	 */
	public Station getAutreExtremite(Station s)
	{
		if (s.equals(extremite1))
			return extremite2;
		else if (s.equals(extremite2))
			return extremite1;
		return null;
	}

	@Override
	public String toString() {
		return "Arc [extremite1=" + extremite1 + ", extremite2=" + extremite2 + ", ligne=" + ligne + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Arc))
			return false;
		Arc a2 = (Arc) obj;
		return (this.extremite1.equals(a2.extremite1) && this.extremite2.equals(a2.extremite2)) || this.extremite1.equals(a2.extremite2) && this.extremite2.equals(a2.extremite1);
	}
}