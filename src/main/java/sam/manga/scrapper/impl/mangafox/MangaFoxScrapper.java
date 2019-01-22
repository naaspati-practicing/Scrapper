package sam.manga.scrapper.impl.mangafox;

import java.io.IOException;

import sam.manga.scrapper.ScrappedManga;
import sam.manga.scrapper.ScrapperException;
import sam.manga.scrapper.impl.mangahere.MangaHereScrapper;

public class MangaFoxScrapper extends MangaHereScrapper {
	
	@Override
	public boolean canHandle(String host) {
		return host.endsWith("fanfox.net");
	}
	@Override
	public ScrappedManga scrapManga(String mangaUrl) throws ScrapperException, IOException {
		return new MangaFoxManga(getJsoupFactory(), mangaUrl);
	}
}
