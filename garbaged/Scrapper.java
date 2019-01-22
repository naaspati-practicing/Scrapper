package sam.manga.scrapper;

import java.io.IOException;

import sam.manga.scrapper.jsoup.JsoupFactory;

public interface Scrapper {
	ScrappedManga scrapManga(String mangaUrl) throws ScrapperException, IOException;
	default  ScrappedChapter[] scrapChapters(ScrappedManga manga) throws ScrapperException, IOException {
		return manga.getChapters();
	}
	ScrappedPage[] scrapPages(String chapterUrl) throws ScrapperException, IOException ;
	String[] getPageImageUrl(String pageUrls) throws ScrapperException, IOException;
	String urlColumn();
	void setJsoupFactory(JsoupFactory factory);
}
