package eyjafjallajokull.projettransverse.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

import eyjafjallajokull.projettransverse.model.Arc;
import eyjafjallajokull.projettransverse.model.Reseau;
import eyjafjallajokull.projettransverse.model.Station;

public class PanelReseau extends JPanel {

	private static final long serialVersionUID = -2312529314961684770L;

	//Attributs
	private final Reseau reseau;

	private static final int TAILLE_ROND = 15;
	private static final int EPAISSEUR_TRAIT = 3;

	//Constructeurs
	public PanelReseau(Reseau reseau) {
		this.reseau = reseau;
	}

	public void paintComponent(Graphics g) {
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // Anti crénelage
		
		// Affichage de chaque arc
		for (Arc a : reseau.getArcs())
		{
			Color Stationcolor = a.getLigne() != null ? a.getLigne().getCouleur() : new Color(0);
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
			Color Stationcolor = new Color(0, 0, 0);
			g.setColor(Stationcolor) ;
			int xb = s.getCoordonneeX() - TAILLE_ROND / 2;
			int yb = s.getCoordonneeY() - TAILLE_ROND / 2;
			g.fillOval(xb, yb, TAILLE_ROND, TAILLE_ROND);
			g.drawString(s.getNom(), xb + TAILLE_ROND, yb);
			g.setColor(Color.WHITE);
			int ecart = TAILLE_ROND / 6;
			g.fillOval(xb + ecart, yb + ecart, TAILLE_ROND - ecart * 2, TAILLE_ROND - ecart * 2);
		}
	}
}