package eyjafjallajokull.projettransverse.model;

public class ArcDejaExistantException extends Exception {

	private static final long serialVersionUID = -1639704074660847278L;

	public ArcDejaExistantException() {
	}

	public ArcDejaExistantException(String message) {
		super(message);
	}

	public ArcDejaExistantException(Throwable cause) {
		super(cause);
	}

	public ArcDejaExistantException(String message, Throwable cause) {
		super(message, cause);
	}

	public ArcDejaExistantException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
