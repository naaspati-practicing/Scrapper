package sam.manga.scrapper;

import sam.myutils.MyUtilsException;

public class FailedChapter extends ScrappedChapter {
	
	private final  Throwable exception;
	private final  String _number;
	
	public FailedChapter(Throwable e, String number, String volume, String title, String url) {
		super(-1, volume, title, url);
		this.exception = e;
		this._number = number;
	}
	
	@Override
	public double getNumber() {
		throw new IllegalStateException("failed chapter");
	}
	
	public String getNumberString() {
		return _number;
	}
	public Throwable getException() {
		return exception;
	}

	@Override
	public String toString() {
		return "FailedChapter [number='" + _number + "', volume='" + volume + "', title='"
				+ (title == null ? "" : title) + "', url='" + url +"', exception='" + MyUtilsException.exceptionToString(exception) + "']";
	}
}
