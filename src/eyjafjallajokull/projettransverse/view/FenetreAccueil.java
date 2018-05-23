package eyjafjallajokull.projettransverse.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import eyjafjallajokull.projettransverse.controller.Main;
import eyjafjallajokull.projettransverse.model.Reseau;
import eyjafjallajokull.projettransverse.model.Station;

public class FenetreAccueil extends JFrame implements ActionListener {

	private static final long serialVersionUID = -8690899871701887818L;

	private JTextField nbLignes, longueur, debit;

	private JFileChooser selectionStations;
	private JButton ouvrirStations;
	private JTextField fieldStations;

	private JFileChooser selectionTrajets;
	private JButton ouvrirTrajets;
	private JTextField fieldTrajets;

	private JFileChooser selectionImage;
	private JButton ouvrirImage;
	private JTextField fieldImage;

	private JButton validation;

	public FenetreAccueil(){
		super();
		build();
	}

	private void build(){
		setTitle("Projet transverse"); 
		setSize(550, 280); 
		setLocationRelativeTo(null); 
		setResizable(false); 
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
		setContentPane(buildContentPane());
	}

	private JPanel buildContentPane(){
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		JPanel panel1 = new JPanel();
		JLabel label = new JLabel("Nombre de lignes à placer :");
		panel1.add(label);
		panel1.setLayout(new FlowLayout());
		nbLignes = new JTextField("18");
		nbLignes.setColumns(3);
		panel1.add(nbLignes);
		panel.add(panel1);

		JPanel panel2 = new JPanel();
		JLabel label2 = new JLabel("Longueur maximale du réseau :");
		panel2.add(label2);
		longueur = new JTextField("25000");
		longueur.setColumns(6);
		panel2.add(longueur);
		panel.add(panel2);

		JPanel panel3 = new JPanel();
		JLabel label3 = new JLabel("Nb de voyageurs transportés par seconde :");
		panel3.add(label3);
		debit = new JTextField();
		debit.setColumns(2);
		debit.setText("4");
		panel3.add(debit);
		panel.add(panel3);

		JPanel panel4 = new JPanel();
		selectionStations = new JFileChooser();
		ouvrirStations = new JButton("Sélectionner un fichier stations");
		ouvrirStations.addActionListener(this);
		panel4.add(ouvrirStations);
		fieldStations = new JTextField("StationsParis.txt");
		fieldStations.setColumns(20);
		panel4.add(fieldStations);
		panel.add(panel4);

		JPanel panel5 = new JPanel();
		selectionTrajets = new JFileChooser();
		ouvrirTrajets = new JButton("Sélectionner un fichier voyageurs");
		ouvrirTrajets.addActionListener(this);
		panel5.add(ouvrirTrajets);
		fieldTrajets = new JTextField("TrajetsParis.txt");
		fieldTrajets.setColumns(20);
		panel5.add(fieldTrajets);
		panel.add(panel5);

		JPanel panel6 = new JPanel();
		selectionImage = new JFileChooser();
		ouvrirImage = new JButton("Sélectionner une image de fond (facultatif)");
		ouvrirImage.addActionListener(this);
		panel6.add(ouvrirImage);
		fieldImage = new JTextField("Paris.jpg");
		fieldImage.setColumns(20);
		panel6.add(fieldImage);
		panel.add(panel6);


		validation = new JButton("Valider");
		validation.addActionListener(this);

		JPanel borderPanel = new JPanel();
		borderPanel.setLayout(new BorderLayout());
		borderPanel.add("Center", panel);
		borderPanel.add("South", validation);
		
		return borderPanel;
	}


	class StateListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			System.out.println("source : " + ((JRadioButton)e.getSource()).getText() + " - état : " + ((JRadioButton)e.getSource()).isSelected());
		}
	}

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == validation)
		{
			if (verifierValeurs())
			{
				chargerDonnees();
			}
		}
		else if (e.getSource() == ouvrirStations)
		{
			int returnVal = selectionStations.showOpenDialog(this);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = selectionStations.getSelectedFile();
				fieldStations.setText(file.getAbsolutePath());
			}
		}
		else if (e.getSource() == ouvrirTrajets)
		{
			int returnVal = selectionTrajets.showOpenDialog(this);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = selectionTrajets.getSelectedFile();
				fieldTrajets.setText(file.getAbsolutePath());
			}
		}
		else if (e.getSource() == ouvrirImage)
		{
			int returnVal = selectionImage.showOpenDialog(this);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = selectionImage.getSelectedFile();
				fieldImage.setText(file.getAbsolutePath());
			}
		}
	}

	private boolean verifierValeurs()
	{
		if (!estNombreEntre(nbLignes, 1, 100))
		{
			UtilitairesAffichage.messageErreur("Le nombre de lignes est invalide.");
			return false;
		}
		if (!estNombreEntre(longueur, 1, 999999999))
		{
			UtilitairesAffichage.messageErreur("La longueur est invalide.");
			return false;
		}
		if (!estNombreEntre(debit, 1, 1000000))
		{
			UtilitairesAffichage.messageErreur("Le débit de voyageurs est invalide.");
			return false;
		}
		if (fieldStations.getText().isEmpty() || !fieldStations.getText().toLowerCase().endsWith(".txt"))
		{
			UtilitairesAffichage.messageErreur("Veuillez choisir un fichier de stations à utiliser (format txt).");
			return false;
		}
		if (fieldTrajets.getText().isEmpty() || !fieldTrajets.getText().toLowerCase().endsWith(".txt"))
		{
			UtilitairesAffichage.messageErreur("Veuillez choisir un fichier de voyageurs à utiliser (format txt).");
			return false;
		}
		return true;
	}

	private boolean estNombreEntre(JTextField field, int min, int max)
	{
		try {
			int nb = Integer.valueOf(field.getText());
			return nb >= min && nb <= max;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	private void chargerDonnees()
	{
		Reseau reseau = new Reseau();

		// Lecture fichier Station
		String pathFichier = fieldStations.getText();
		int xMax = 0;
		int yMax = 0;

		BufferedReader fluxEntree=null;
		try {
			fluxEntree = new BufferedReader(new FileReader(pathFichier));

			String ligneLue = null;
			String station [] = null;

			while((ligneLue = fluxEntree.readLine())!=null){
				station = ligneLue.split(" ");

				int x = Integer.parseInt(station[1]);
				int y = Integer.parseInt(station[2]);
				reseau.ajouterStation(x, y, station[0]);    
				xMax = Math.max(x, xMax);
				yMax = Math.max(y, yMax);
			}
		}
		catch(Exception exc) {
			UtilitairesAffichage.messageErreur("Impossible de lire le fichier " + pathFichier + ".\nVérifiez qu'il existe et que son contenu est conforme.");
			return;
		}
		finally{
			try{
				if (fluxEntree != null) {
					/* Fermeture du flux vers le fichier */
					fluxEntree.close();
				}
			}
			catch(IOException e){
				e.printStackTrace();
			}
		}

		reseau.setxMax(xMax + 200);
		reseau.setyMax(yMax + 100);

		// Lecture fichier trajet
		pathFichier = fieldTrajets.getText();

		fluxEntree=null;
		try {
			fluxEntree = new BufferedReader(new FileReader(pathFichier));

			String ligneLue = null;
			String trajet [] = null;

			while((ligneLue = fluxEntree.readLine())!=null) {
				trajet = ligneLue.split(" ");

				Station s1 = reseau.getStation(trajet[0]);
				Station s2 = reseau.getStation(trajet[1]);

				if (s1 == null)
				{
					UtilitairesAffichage.messageErreur("La station " + trajet[0] + " du fichiers voyageurs n'est pas définie dans le fichier stations.");
					return;
				}
				else if (s2 == null)
				{
					UtilitairesAffichage.messageErreur("La station " + trajet[1] + " du fichiers voyageurs n'est pas définie dans le fichier stations.");
					return;
				}
				else
				{
					for (int i=0; i < Integer.parseInt(trajet[2]); i++)
					{
						reseau.ajouterVoyageur(s1, s2);
					}               
				}
			}
		}
		catch(Exception exc) {
			UtilitairesAffichage.messageErreur("Impossible de lire le fichier " + pathFichier + ".\nVérifiez qu'il existe et que son contenu est conforme.");
			return;
		}
		finally {
			try {
				if (fluxEntree!=null) {
					// Fermeture du flux vers le fichier
					fluxEntree.close();
				}
			}
			catch(IOException e){
				e.printStackTrace();
			}
		}

		
		// Ouverture de l'image si elle est donnée
		Image img = null;
		if (!fieldImage.getText().isEmpty())
		{
			try {
				img = ImageIO.read(new File(fieldImage.getText()));
			} catch (IOException e) {
				UtilitairesAffichage.messageErreur("Impossible d'ouvrir l'image " + pathFichier + ".\nVérifiez qu'elle existe.");
				return;
			}
		}
		
		
		Main.afficherFenetreReseau(reseau, Integer.valueOf(nbLignes.getText()), Integer.valueOf(longueur.getText()), Integer.valueOf(debit.getText()), img);
	}
}
