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
	private final double multiplicateur;

	private static final int TAILLE_ROND = 15;
	private static final int EPAISSEUR_TRAIT = 3;

	//Constructeurs
	public PanelReseau(Reseau reseau, double multiplicateur) {
		this.reseau = reseau;
		this.multiplicateur = multiplicateur;
	}

	public void paintComponent(Graphics g) {
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // Anti crénelage
		
		// Affichage de chaque arc
		for (Arc a : reseau.getArcs())
		{
			Color Stationcolor = a.getLigne() != null ? a.getLigne().getCouleur() : new Color(0);
			g.setColor(Stationcolor) ;
			int xb = (int) (a.getExtremite1().getCoordonneeX() * multiplicateur);
			int yb = (int) (a.getExtremite1().getCoordonneeY() * multiplicateur);
			int xf = (int) (a.getExtremite2().getCoordonneeX() * multiplicateur);
			int yf = (int) (a.getExtremite2().getCoordonneeY() * multiplicateur);
			Graphics2D g2 = (Graphics2D) g;
			g2.setStroke(new BasicStroke((float) (EPAISSEUR_TRAIT * multiplicateur)));
			g2.setColor(Stationcolor);
			g2.drawLine(xb, yb, xf, yf);

		}
		// Affichage de chaque station
		for (Station s : reseau.getStations())
		{
			Color Stationcolor = new Color(0, 0, 0);
			g.setColor(Stationcolor) ;
			int xb = (int) ((s.getCoordonneeX() - TAILLE_ROND / 2) * multiplicateur);
			int yb = (int) ((s.getCoordonneeY() - TAILLE_ROND / 2) * multiplicateur);
			g.fillOval(xb, yb, (int) (TAILLE_ROND * multiplicateur), (int) (TAILLE_ROND * multiplicateur));
			g.drawString(s.getNom(), (int) (xb + TAILLE_ROND * multiplicateur), yb);
			g.setColor(Color.WHITE);
			int ecart = (int) ((TAILLE_ROND / 6) * multiplicateur);
			g.fillOval(xb + ecart, yb + ecart, (int) ((TAILLE_ROND - ecart * 2) * multiplicateur), (int) ((TAILLE_ROND - ecart * 2) * multiplicateur));
		}
	}
}