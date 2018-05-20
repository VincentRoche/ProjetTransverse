package eyjafjallajokull.projettransverse.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

import eyjafjallajokull.projettransverse.model.Arc;
import eyjafjallajokull.projettransverse.model.Reseau;
import eyjafjallajokull.projettransverse.model.Station;

public class PanelReseau extends JPanel implements MouseMotionListener {

	private static final long serialVersionUID = -2312529314961684770L;

	//Attributs
	private Reseau reseau;
	private final double multiplicateur;

	private int sourisX;
	private int sourisY;

	private static final int TAILLE_ROND = 15;
	private static final int EPAISSEUR_TRAIT = 4;

	//Constructeurs
	public PanelReseau(Reseau reseau, double multiplicateur) {
		this.reseau = reseau;
		this.multiplicateur = multiplicateur;
		addMouseMotionListener(this);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // Anti crénelage

		// Affichage de chaque arc
		for (Arc a : reseau.getArcs())
		{
			Color stationcolor = a.getLigne() != null ? a.getLigne().getCouleur() : new Color(0, 0, 0);
			g.setColor(stationcolor) ;
			int xb = (int) (a.getExtremite1().getCoordonneeX() * multiplicateur);
			int yb = (int) (a.getExtremite1().getCoordonneeY() * multiplicateur);
			int xf = (int) (a.getExtremite2().getCoordonneeX() * multiplicateur);
			int yf = (int) (a.getExtremite2().getCoordonneeY() * multiplicateur);
			Graphics2D g2 = (Graphics2D) g;
			g2.setStroke(new BasicStroke((float) (EPAISSEUR_TRAIT * multiplicateur)));
			g2.setColor(stationcolor);
			g2.drawLine(xb, yb, xf, yf);

		}
		// Affichage de chaque station
		for (Station s : reseau.getStations())
		{
			Color stationcolor = new Color(0, 0, 0);
			g.setColor(stationcolor) ;
			int xb = (int) ((s.getCoordonneeX() - TAILLE_ROND / 2) * multiplicateur);
			int yb = (int) ((s.getCoordonneeY() - TAILLE_ROND / 2) * multiplicateur);
			g.fillOval(xb, yb, (int) (TAILLE_ROND * multiplicateur), (int) (TAILLE_ROND * multiplicateur));
			// Affichage du texte si la souris est sur le rond
			if (sourisX >= xb && sourisX <= xb + TAILLE_ROND * multiplicateur && sourisY >= yb && sourisY <= yb + TAILLE_ROND * multiplicateur)
				g.drawString(s.getNom(), (int) (xb + TAILLE_ROND * multiplicateur), yb);
			g.setColor(Color.WHITE);
			int ecart = (int) ((TAILLE_ROND / 6) * multiplicateur);
			g.fillOval(xb + ecart, yb + ecart, (int) ((TAILLE_ROND - ecart * 2) * multiplicateur), (int) ((TAILLE_ROND - ecart * 2) * multiplicateur));
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		sourisX = e.getX();
		sourisY = e.getY();

		revalidate();
		repaint();
	}
	
	/**
	 * @param reseau Redéfinit le réseauy à afficher
	 */
	public void setReseau(Reseau reseau) {
		this.reseau = reseau;
	}
}