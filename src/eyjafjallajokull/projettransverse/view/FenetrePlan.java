package eyjafjallajokull.projettransverse.view;

import javax.swing.JFrame;

import eyjafjallajokull.projettransverse.model.Reseau;

public class FenetrePlan extends JFrame {

	private static final long serialVersionUID = 782320099087038591L;
	private Reseau reseau;
	private final double multiplicateur;

	private PanelReseau panelReseau;

	/**
	 * @param reseau
	 * @param multiplicateur Multiplicateur de la taille de l'affichage par rapport au réseau
	 */
	public FenetrePlan(Reseau reseau, double multiplicateur){
		super();
		this.reseau = reseau;
		this.multiplicateur = multiplicateur;
		build();
	}

	private void build() {
		setTitle("Ligne de métro"); 
		setSize((int) (reseau.getxMax() * multiplicateur), (int) (reseau.getyMax() * multiplicateur)); 
		setLocationRelativeTo(null); 
		setResizable(true); 
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		panelReseau = new PanelReseau(reseau, multiplicateur);
		add(panelReseau);
		setVisible(true);
	}

	/**
	 * Met à jour le plan affiché
	 */
	public void majReseau() {
		majReseau(null);
	}

	/**
	 * Met à jour le plan affiché
	 * @param reseauVoulu Réseau à afficher
	 */
	public void majReseau(Reseau reseauVoulu) {
		if (reseauVoulu != null)
		{
			reseau = reseauVoulu;
			panelReseau.setReseau(reseauVoulu);
		}

		panelReseau.revalidate();
		panelReseau.repaint();
	}
}
