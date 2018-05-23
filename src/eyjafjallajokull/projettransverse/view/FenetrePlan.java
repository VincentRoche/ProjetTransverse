package eyjafjallajokull.projettransverse.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import eyjafjallajokull.projettransverse.controller.Main;
import eyjafjallajokull.projettransverse.model.IACreationLignes;
import eyjafjallajokull.projettransverse.model.IARoche;
import eyjafjallajokull.projettransverse.model.Reseau;
import eyjafjallajokull.projettransverse.model.Station;

public class FenetrePlan extends JFrame implements ActionListener {

	private static final long serialVersionUID = 782320099087038591L;
	private Reseau reseau;
	private IACreationLignes ia;
	private int debit;
	private final double multiplicateur;
	private final Image imageFond;
	private Thread threadIA;

	private PanelReseau panelPlan;

	private JButton boutonGenerer;
	private JButton boutonRetour;
	private JTextField fieldTemps;

	/**
	 * @param reseau Réseau à utiliser.
	 * @param multiplicateur Multiplicateur de la taille de l'affichage par rapport au réseau.
	 * @param imageFond Image de fond à afficher.
	 * @param nbLignes Nombre de lignes à placer (pour l'IA).
	 * @param longueurMax Longueur maximale du réseau (pour l'IA).
	 * @param debit Débit des voyageurs (pour l'IA).
	 */
	public FenetrePlan(Reseau reseau, double multiplicateur, Image imageFond, int nbLignes, int longueurMax, int debit){
		super();
		this.reseau = reseau;
		this.multiplicateur = multiplicateur;
		this.imageFond = imageFond;
		this.ia = new IARoche(reseau, nbLignes, longueurMax, this);
		this.debit = debit;
		build();
	}

	private void build() {
		setTitle("Carte du réseau"); 
		setSize((int) (reseau.getxMax() * multiplicateur), (int) (reseau.getyMax() * multiplicateur) + 40); 
		setLocationRelativeTo(null); 
		setResizable(true); 
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setContentPane(buildContentPane());
	}

	private JPanel buildContentPane() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());

		panelPlan = new PanelReseau(reseau, multiplicateur, imageFond);
		panel.add("Center", panelPlan);

		JPanel panelSouth = new JPanel();
		panelSouth.setLayout(new FlowLayout());
		boutonGenerer = new JButton("Générer le réseau");
		boutonGenerer.addActionListener(this);
		panelSouth.add(boutonGenerer);
		boutonRetour = new JButton("Retour à l'accueil");
		boutonRetour.addActionListener(this);
		panelSouth.add(boutonRetour);
		JLabel label = new JLabel("Moyenne des temps de trajet des voyageurs :");
		panelSouth.add(label);
		fieldTemps = new JTextField();
		fieldTemps.setEditable(false);
		fieldTemps.setColumns(20);
		panelSouth.add(fieldTemps);

		panel.add("South", panelSouth);

		return panel;
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
			panelPlan.setReseau(reseauVoulu);
		}

		panelPlan.revalidate();
		panelPlan.repaint();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == boutonGenerer)
		{
			boutonGenerer.setEnabled(false);

			// Lancement de l'IA de génération des lignes dans un autre Thread
			threadIA = new Thread() {
				public void run() {
					reseau = ia.placerLignes();
					majReseau();
					if (reseau != null)
					{
						List<Station> isolees = reseau.stationsIsolees();
						int nbTotal = reseau.getStations().size();
						int nbIsolees = isolees.size();
						for (Station s : isolees)
						{
							reseau.supprimerStation(s);
						}

						JOptionPane.showMessageDialog(null, "La génération du réseau est terminée. " + (nbTotal - nbIsolees) + " stations ont été reliées.\nLe calcul des temps de trajet des voyageurs va commencer.", "Génération terminée", JOptionPane.INFORMATION_MESSAGE);
						majReseau();

						double moyenne = reseau.evaluer(debit, isolees, fieldTemps) / 60;
						fieldTemps.setText((int) moyenne + " minutes");
					}
				}  
			};
			threadIA.start();

		}
		else if (source == boutonRetour)
		{
			if (threadIA != null)
				threadIA.stop();
			Main.retournerAccueil();
		}
	}
}
