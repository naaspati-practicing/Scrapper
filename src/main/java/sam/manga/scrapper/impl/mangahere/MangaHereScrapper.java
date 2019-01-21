package sam.manga.scrapper.impl.mangahere;

import java.io.IOException;

import sam.manga.samrock.urls.MangaUrlsMeta;
import sam.manga.scrapper.ScrappedManga;
import sam.manga.scrapper.ScrapperException;
import sam.manga.scrapper.impl.mangafox.MangaFoxScrapper;

public class MangaHereScrapper extends MangaFoxScrapper {
	
	@Override
	public String urlColumn() {
		return MangaUrlsMeta.MANGAHERE;
	}
	
	@Override
	public ScrappedManga scrapManga(String mangaUrl) throws ScrapperException, IOException {
		return new MangaHereManga(factory, mangaUrl);
	}
	
	@Override
	public String[] getPageImageUrl(String pageUrl) throws ScrapperException, IOException {
		ScrapperException e ;
		try {
			return super.getPageImageUrl(pageUrl);
		} catch (ScrapperException e1) {
			e = e1;
		}
		try {
			return super.getPageImageUrl(pageUrl.replace("mangahere.cc", "fanfox.net"));
		} catch (ScrapperException e1) {}
		
		throw e;
	}
}
