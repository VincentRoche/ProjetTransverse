package eyjafjallajokull.projettransverse.model;

import java.util.List;

/**
 * Représente un chemin d'arcs entre deux stations.
 */
public class Chemin {
	
	private final Station depart;
	private final Station arrivee;
	private final List<Arc> arcs;
	
	public Chemin(Station depart, Station arrivee, List<Arc> arcs) {
		this.depart = depart;
		this.arrivee = arrivee;
		this.arcs = arcs;
	}
	
	
}
