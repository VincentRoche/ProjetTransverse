package eyjafjallajokull.projettransverse.view;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

import eyjafjallajokull.projettransverse.model.Reseau;
import eyjafjallajokull.projettransverse.model.Station;

public class PanelStations extends JPanel {

	//Attributs
	private final Reseau reseau;

	private static final int TAILLE_ROND = 30;

	//Constructeurs
	public PanelStations(Reseau reseau) {
		this.reseau = reseau;
	}

	public void paintComponent(Graphics g) {
		// Affichage de chaque station
		for (Station s : reseau.getStations())
		{
			Color Stationcolor = new Color(153, 0, 51);
			g.setColor(Stationcolor) ;
			int xb = s.getCoordonneeX();
			int yb = s.getCoordonneeY();
			g.fillOval(xb, yb, TAILLE_ROND, TAILLE_ROND);
			g.setColor(Color.WHITE);
			int ecart = TAILLE_ROND / 6;
			g.fillOval(xb + ecart, yb + ecart, TAILLE_ROND - ecart * 2, TAILLE_ROND - ecart * 2);
		}
	}
}
