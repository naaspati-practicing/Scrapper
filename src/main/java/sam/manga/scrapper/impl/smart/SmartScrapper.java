package sam.manga.scrapper.impl.smart;

import java.io.IOException;

import sam.manga.scrapper.ScrappedManga;
import sam.manga.scrapper.ScrappedPage;
import sam.manga.scrapper.ScrapperException;
import sam.manga.scrapper.ScrapperMore;
import sam.manga.scrapper.impl.mangahere.MangaHereScrapper;

public class SmartScrapper implements ScrapperMore {
	private final MangaHereScrapper here = new MangaHereScrapper();

	@Override
	public ScrappedManga scrapManga(String mangaUrl) throws ScrapperException, IOException {
		return new TempManga(here.getJsoupFactory(), mangaUrl);
	}

	@Override
	public boolean canHandle(String host) {
		return true;
	}

	@Override
	public String[] getPageImageUrl(String chapter_url, String page_url) throws ScrapperException, IOException {
		return null;
	}

	@Override
	public ScrappedPage[] getPages(String s) throws ScrapperException, IOException {
		// TODO Auto-generated method stub
		return null;
	} 

}
