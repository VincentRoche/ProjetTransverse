package eyjafjallajokull.projettransverse.model;

import eyjafjallajokull.projettransverse.view.FenetrePlan;

public abstract class IACreationLignes {

	protected int nbLignes;
	protected int longueurMax;
	protected Reseau reseau;
	protected FenetrePlan fenetre;
	
	/**
	 * @param reseau Réseau sur lequel placer des lignes (ne comporte que des stations et des voyageurs, pas d'arcs).
	 * @param nbLignes Nombre de lignes que l'IA soit placer.
	 * @param longueurMax Longueur maximale de rails à placer.
	 * @param fenetre Fenêtre affichant le plan (pour afficher les lignes pendant leur construction)
	 */
	public IACreationLignes(Reseau reseau, int nbLignes, int longueurMax, FenetrePlan fenetre)
	{
		this.nbLignes = nbLignes;
		this.longueurMax = longueurMax;
		this.reseau = reseau;
		this.fenetre = fenetre;
	}
	
	/**
	 * Fait fonctionner l'IA pour placer les lignes sur le réseau selon les contraintes.
	 * @return Réseau rempli de lignes.
	 */
	public abstract Reseau placerLignes();
}
