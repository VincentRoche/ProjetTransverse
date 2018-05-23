package eyjafjallajokull.projettransverse.view;

import javax.swing.JOptionPane;

public class UtilitairesAffichage {

	public static void messageErreur(String texte)
	{
		JOptionPane.showMessageDialog(null, texte, "Erreur", JOptionPane.ERROR_MESSAGE);
	}
}
