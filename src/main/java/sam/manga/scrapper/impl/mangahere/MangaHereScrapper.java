package sam.manga.scrapper.impl.mangahere;

import java.io.IOException;

import sam.manga.scrapper.ScrappedManga;
import sam.manga.scrapper.ScrappedPage;
import sam.manga.scrapper.ScrapperException;
import sam.manga.scrapper.ScrapperMore;
import sam.manga.scrapper.jsoup.DefaultJsoupFactory;
import sam.manga.scrapper.jsoup.JsoupFactory;

public class MangaHereScrapper implements ScrapperMore {

	protected JsoupFactory _factory;

	public JsoupFactory getJsoupFactory() {
		if(_factory == null)
			_factory = new DefaultJsoupFactory();
		return _factory;
	}
	@Override
	public boolean canHandle(String host) {
		return host.endsWith("mangahere.cc");
	}
	@Override
	public ScrappedManga scrapManga(String mangaUrl) throws ScrapperException, IOException {
		return new MangaHereManga(getJsoupFactory(), mangaUrl);
	}
	
	@Override
	public ScrappedPage[] getPages(String chapter_url) throws ScrapperException, IOException {
		return new MangaHereChapter(chapter_url, this::getJsoupFactory).getPages();
	}
	@Override
	public String[] getPageImageUrl(String chapter_url, String page_url) throws ScrapperException, IOException {
		return new MangaHereChapter(chapter_url, this::getJsoupFactory).getPageImageUrl(page_url);
	}
}
