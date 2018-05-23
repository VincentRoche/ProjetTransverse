package eyjafjallajokull.projettransverse.controller;

import java.awt.Image;

import eyjafjallajokull.projettransverse.model.Reseau;
import eyjafjallajokull.projettransverse.view.FenetreAccueil;
import eyjafjallajokull.projettransverse.view.FenetrePlan;

public class Main {

	private static FenetreAccueil fenetreAccueil;
	private static FenetrePlan fenetrePlan;
	
	public static void main(String[] args) {
		fenetreAccueil = new FenetreAccueil();
		fenetreAccueil.setVisible(true);
	}

	public static void afficherFenetreReseau(Reseau reseau, int nbLignes, int longueurMax, int debit, Image imageFond)
	{
		fenetreAccueil.setVisible(false);
		fenetrePlan = new FenetrePlan(reseau, 0.5, imageFond, nbLignes, longueurMax, debit);
		fenetrePlan.setVisible(true);
	}

	public static void retournerAccueil()
	{
		fenetrePlan.setVisible(false);
		fenetrePlan.dispose();
		fenetreAccueil.setVisible(true);
	}
}
