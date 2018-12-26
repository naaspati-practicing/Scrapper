package sam.manga.scrapper;

public class ScrapperException extends Exception {
	private static final long serialVersionUID = 800668726022739755L;

	public ScrapperException() {
		super();
	}

	public ScrapperException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ScrapperException(String message, Throwable cause) {
		super(message, cause);
	}

	public ScrapperException(String message) {
		super(message);
	}

	public ScrapperException(Throwable cause) {
		super(cause);
	}

}
