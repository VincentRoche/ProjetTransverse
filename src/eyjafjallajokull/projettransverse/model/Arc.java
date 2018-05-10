package eyjafjallajokull.projettransverse.model;

/**
 * Représente un arc entre deux stations, un morceau de ligne.
 */
public class Arc {

	private final Station extremite1;
	private final Station extremite2;
	private final Ligne ligne;
	
	Arc(Station extremite1, Station extremite2, Ligne ligne) {
		this.extremite1 = extremite1;
		this.extremite2 = extremite2;
		this.ligne = ligne;
	}

	/**
	 * @return Distance entre les deux extrêmités de l'arc.
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

	@Override
	public String toString() {
		return "Arc [extremite1=" + extremite1 + ", extremite2=" + extremite2 + ", ligne=" + ligne + "]";
	}
}