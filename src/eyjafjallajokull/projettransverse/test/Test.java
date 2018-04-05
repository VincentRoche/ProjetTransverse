package eyjafjallajokull.projettransverse.test;

import java.util.List;
import java.util.Random;

import eyjafjallajokull.projettransverse.model.Reseau;
import eyjafjallajokull.projettransverse.model.Station;
import eyjafjallajokull.projettransverse.model.Voyageur;

public class Test {

	public static void main(String[] args) {
		int tailleMax = 500;
		Reseau reseau = new Reseau(tailleMax, tailleMax);
		// Stations aléatoires
		int nbStations = 10;
		Random rand = new Random();
		for (int i=0; i < nbStations; i++)
		{
			reseau.ajouterStation(rand.nextInt(tailleMax), rand.nextInt(tailleMax));
		}
		
		// Voyageurs aléatoires
		List<Station> stations = reseau.getStations();
		for (Station s : stations)
		{
			// Nombre aléatoire
			for (int i=0; i < rand.nextInt(10); i++)
			{
				reseau.ajouterVoyageur(s, stations.get(rand.nextInt(stations.size())));
			}
		}
		
		// Affichage
		for (Station s : reseau.getStations())
		{
			System.out.println(s);
		}
		for (Voyageur v : reseau.getVoyageurs())
		{
			System.out.println(v);
		}
	}

}
