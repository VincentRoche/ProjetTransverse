package eyjafjallajokull.projettransverse.model;

/**
 * Représente un arc entre deux stations, un morceau de ligne.
 */
public class Arc {

	private final Station extremite1;
	private final Station extremite2;
	private Ligne ligne;

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
	 * @param ligne Ligne à définir.
	 */
	public void setLigne(Ligne ligne) {
		this.ligne = ligne;
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

	/**
	 * Regarde si cet arc croise l'arc donné ou pas.
	 * @param a Arc à vérifier
	 * @return true si les 2 arcs se croisent.
	 */
	public boolean croise(Arc a)
	{
		int p0_x = extremite1.getCoordonneeX(), p0_y = extremite1.getCoordonneeY();
		int p1_x = extremite2.getCoordonneeX(), p1_y = extremite2.getCoordonneeY();
		int p2_x = a.extremite1.getCoordonneeX(), p2_y = a.extremite1.getCoordonneeY();
		int p3_x = a.extremite2.getCoordonneeX(), p3_y = a.extremite2.getCoordonneeY();

		float s02_x, s02_y, s10_x, s10_y, s32_x, s32_y, s_numer, t_numer, denom;
	    s10_x = p1_x - p0_x;
	    s10_y = p1_y - p0_y;
	    s32_x = p3_x - p2_x;
	    s32_y = p3_y - p2_y;

	    denom = s10_x * s32_y - s32_x * s10_y;
	    if (denom == 0)
	        return false; // Collinear
	    boolean denomPositive = denom > 0;

	    s02_x = p0_x - p2_x;
	    s02_y = p0_y - p2_y;
	    s_numer = s10_x * s02_y - s10_y * s02_x;
	    if ((s_numer < 0) == denomPositive)
	        return false; // No collision

	    t_numer = s32_x * s02_y - s32_y * s02_x;
	    if ((t_numer < 0) == denomPositive)
	        return false; // No collision

	    if (((s_numer > denom) == denomPositive) || ((t_numer > denom) == denomPositive))
	        return false; // No collision
	   
	    return true;
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