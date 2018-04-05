package eyjafjallajokull.projettransverse.model;

public class Voyageur {

	private Station origine;
	private Station destination;
	
	Voyageur(Station origine, Station destination) {
		this.origine = origine;
		this.destination = destination;
	}

	/**
	 * @return Station d'origine du voyageur.
	 */
	public Station getOrigine() {
		return origine;
	}

	/**
	 * @return Station où le voyageur veut aller.
	 */
	public Station getDestination() {
		return destination;
	}

	@Override
	public String toString() {
		return "Voyageur [origine=" + origine + ", destination=" + destination + "]";
	}
	
	
}
