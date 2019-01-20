package sam.manga.scrapper.impl.mangahere;

import java.io.IOException;

import org.jsoup.nodes.Element;

import sam.manga.samrock.urls.MangaUrlsMeta;
import sam.manga.scrapper.ScrappedManga;
import sam.manga.scrapper.ScrappedPage;
import sam.manga.scrapper.Scrapper;
import sam.manga.scrapper.ScrapperException;
import sam.manga.scrapper.UrlType;
import sam.manga.scrapper.impl.mangafox.MangaFoxManga;
import sam.manga.scrapper.impl.mangafox.MangaFoxScrapper;
import sam.manga.scrapper.jsoup.JsoupFactory;

public class MangaHereScrapper extends MangaFoxScrapper {
	
	@Override
	public String urlColumn() {
		return MangaUrlsMeta.MANGAHERE;
	}
	
	@Override
	public ScrappedManga scrapManga(String mangaUrl) throws ScrapperException, IOException {
		return new MangaHereManga(factory, mangaUrl);
	}
}
