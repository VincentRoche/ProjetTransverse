package eyjafjallajokull.projettransverse.view;

import javax.swing.JFrame;

import eyjafjallajokull.projettransverse.model.Reseau;

public class FenetrePlan extends JFrame {
	
	private final Reseau reseau;
	
	public FenetrePlan(Reseau reseau){
        super();
		this.reseau = reseau;
        build();
    }
	
    private void build(){
        setTitle("Ligne de métro"); 
        setSize(reseau.getxMax(), reseau.getyMax()); 
        setLocationRelativeTo(null); 
        setResizable(false); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(new PanelStations(reseau));
        setVisible(true);
  }
}
