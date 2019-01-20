package sam.manga.scrapper;

import sam.manga.scrapper.impl.mangafox.MangaFoxScrapper;
import sam.manga.scrapper.impl.mangahere.MangaHereScrapper;

public enum ScrapperType {
	MANGAFOX(MangaFoxScrapper.class, "http://fanfox.net/"), 
	MANGAHERE(MangaHereScrapper.class, "http://www.mangahere.cc/");
	
	public final Class<? extends Scrapper> cls;
	public final String base_url;
	
	private ScrapperType(Class<? extends Scrapper> cls, String base_url) {
		this.cls = cls;
		this.base_url = base_url;
	}
}
