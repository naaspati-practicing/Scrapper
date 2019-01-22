package sam.manga.scrapper;

import java.io.IOException;

public abstract class ScrappedChapter {

	protected final  double number;
	protected final   String volume;
	protected final   String title;
	protected final   String url;
	
	public ScrappedChapter(double number, String volume, String title, String url) {
		this.number = number;
		this.volume = volume;
		this.title = title;
		this.url = url;
	}
	public double getNumber() {
		return number;
	}
	public String getVolume() {
		return volume;
	}
	public String getTitle() {
		return title;
	}
	public String getUrl() {
		return url;
	}
	@Override
	public String toString() {
		return getClass().getSimpleName()+" [number='" + number + "', volume='" + volume + "', title='" + (title == null ? "" : title) + "', url='" + url + "']";
	}
	
	public abstract String[] getPageImageUrl(String pageUrl) throws ScrapperException, IOException ;
	public abstract ScrappedPage[] getPages() throws ScrapperException, IOException ;
}
