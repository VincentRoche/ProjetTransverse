package eyjafjallajokull.projettransverse.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import eyjafjallajokull.projettransverse.model.Arc;
import eyjafjallajokull.projettransverse.model.Reseau;
import eyjafjallajokull.projettransverse.model.Station;

public class PanelReseau extends JPanel {

	private static final long serialVersionUID = -2312529314961684770L;

	//Attributs
	private final Reseau reseau;

	private static final int TAILLE_ROND = 30;
	private static final int EPAISSEUR_TRAIT = 3;

	//Constructeurs
	public PanelReseau(Reseau reseau) {
		this.reseau = reseau;
	}

	public void paintComponent(Graphics g) {
		// Affichage de chaque arc
		for (Arc a : reseau.getArcs())
		{
			Color Stationcolor = new Color(153, 0, 51);
			g.setColor(Stationcolor) ;
			int xb = a.getExtremite1().getCoordonneeX();
			int yb = a.getExtremite1().getCoordonneeY();
			int xf = a.getExtremite2().getCoordonneeX();
			int yf = a.getExtremite2().getCoordonneeY();
			Graphics2D g2 = (Graphics2D) g;
			g2.setStroke(new BasicStroke(EPAISSEUR_TRAIT));
			g2.setColor(Stationcolor);
			g2.drawLine(xb, yb, xf, yf);

		}
		// Affichage de chaque station
		for (Station s : reseau.getStations())
		{
			Color Stationcolor = new Color(153, 0, 51);
			g.setColor(Stationcolor) ;
			int xb = s.getCoordonneeX() - TAILLE_ROND / 2;
			int yb = s.getCoordonneeY() - TAILLE_ROND / 2;
			g.fillOval(xb, yb, TAILLE_ROND, TAILLE_ROND);
			g.setColor(Color.WHITE);
			int ecart = TAILLE_ROND / 6;
			g.fillOval(xb + ecart, yb + ecart, TAILLE_ROND - ecart * 2, TAILLE_ROND - ecart * 2);
		}
	}
}