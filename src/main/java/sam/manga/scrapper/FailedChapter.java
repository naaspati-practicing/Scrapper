package sam.manga.scrapper;

import java.io.IOException;

import sam.myutils.MyUtilsException;

public class FailedChapter extends ScrappedChapter {
	
	private final  Throwable exception;
	private final  String _number;
	
	public FailedChapter(Throwable e, String number, String volume, String title, String url) {
		super(-1, volume, title, url);
		this.exception = e;
		this._number = number;
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
				+ (title == null ? "" : title) + "', url='" + url +"', exception='" + MyUtilsException.toString(exception) + "']";
	}

	private <E> E failed() {
		throw new IllegalStateException("failed chapter");
	}
	@Override
	public double getNumber() {
		return failed();
	}
	@Override
	public ScrappedPage[] getPages() throws ScrapperException, IOException {
		return failed();
	}
	@Override
	public String[] getPageImageUrl(String pageUrl) throws ScrapperException, IOException {
		return failed();
	}
}
