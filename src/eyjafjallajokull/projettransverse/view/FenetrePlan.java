package eyjafjallajokull.projettransverse.view;

import javax.swing.JFrame;

import eyjafjallajokull.projettransverse.model.Reseau;

public class FenetrePlan extends JFrame {
	
	private static final long serialVersionUID = 782320099087038591L;
	private final Reseau reseau;
	
	public FenetrePlan(Reseau reseau){
        super();
		this.reseau = reseau;
        build();
    }
	
    private void build() {
        setTitle("Ligne de métro"); 
        setSize(reseau.getxMax(), reseau.getyMax()); 
        setLocationRelativeTo(null); 
        setResizable(false); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        majReseau();
  }

	/**
	 * Met à jour le plan affiché
	 */
	public void majReseau() {
        setContentPane(new PanelReseau(reseau));
	}
}
