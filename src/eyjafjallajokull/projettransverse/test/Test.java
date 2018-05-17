package eyjafjallajokull.projettransverse.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import eyjafjallajokull.projettransverse.model.Arc;
import eyjafjallajokull.projettransverse.model.Reseau;
import eyjafjallajokull.projettransverse.view.FenetrePlan;

public class Test {

	public static void main(String[] args) {
		Reseau reseau = new Reseau();

		// Lecture fichier Station
		//String pathFichier = "station.txt";
		String pathFichier = "StationsParis.txt";
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
		catch(IOException exc) {
			exc.printStackTrace();
		}
		finally{
			try{
				if(fluxEntree!=null){
					/* Fermeture du flux vers le fichier */
					fluxEntree.close();
				}
			}
			catch(IOException e){
				e.printStackTrace();
			}
		}
		
		reseau.setxMax(xMax + 200);
		reseau.setyMax(yMax);
		
		// Lecture fichier trajet
		/*pathFichier="trajet.txt";

		fluxEntree=null;
		try {
			fluxEntree = new BufferedReader(new FileReader(pathFichier));

			String ligneLue = null;
			String trajet [] = null;

			while((ligneLue = fluxEntree.readLine())!=null) {
				trajet = ligneLue.split(" ");

				for (int i=0; i < Integer.parseInt(trajet[2]); i++)
				{
					reseau.ajouterVoyageur(reseau.getStation(trajet[0]), reseau.getStation(trajet[1]));
				}                  
			}
		}
		catch(IOException exc){
			exc.printStackTrace();
		}
		finally{
			try{
				if(fluxEntree!=null){
					// Fermeture du flux vers le fichier
					fluxEntree.close();
				}
			}
			catch(IOException e){
				e.printStackTrace();
			}
		}

		
		// Placement des lignes avec l'IA
		reseau = new IARoche(reseau, 3, 10000, null).placerLignes();*/
		// Fenêtre
		FenetrePlan f = new FenetrePlan(reseau, 0.6);
		f.majReseau();

		// Affichage des données
		/*for (Station s : reseau.getStations())
		{
			System.out.println(s);
		}*/
		for (Arc a : reseau.getArcs())
		{
			System.out.println(a);
		}
		/*for (Voyageur v : reseau.getVoyageurs())
		{
			System.out.println(v);
		}*/


		//System.out.println("Moyenne des temps de parcours = " + reseau.evaluer());
	}

}
