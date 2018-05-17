package eyjafjallajokull.projettransverse.view;

import javax.swing.JFrame;

import eyjafjallajokull.projettransverse.model.Reseau;

public class FenetrePlan extends JFrame {
	
	private static final long serialVersionUID = 782320099087038591L;
	private final Reseau reseau;
	private final double multiplicateur;
	
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
        majReseau();
        setVisible(true);
  }

	/**
	 * Met à jour le plan affiché
	 */
	public void majReseau() {
        add(new PanelReseau(reseau, multiplicateur));
	}
}
