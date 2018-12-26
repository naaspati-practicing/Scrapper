package sam.manga.scrapper.scrappers.impl;

import sam.manga.scrapper.Scrapper;
import sam.manga.scrapper.impl.mangafox.MangaFoxScrapper;
import sam.manga.scrapper.impl.mangahere.MangaHereScrapper;

public enum ScrapperType {
	MANGAFOX(MangaFoxScrapper.class, "http://fanfox.net/"), MANGAHERE(MangaHereScrapper.class, "http://www.mangahere.cc/");
	
	final Class<? extends Scrapper> cls;
	final String base_url;
	
	private ScrapperType(Class<? extends Scrapper> cls, String base_url) {
		this.cls = cls;
		this.base_url = base_url;
	}
}
