package sam.manga.scrapper;

import sam.manga.scrapper.jsoup.JsoupFactory;

public interface Scrapper {
	ScrappedManga scrapManga(String mangaUrl) throws Exception;
	default  void scrapChapters(ScrappedManga manga, ChapterScrapListener listener) throws Exception {
		manga.getChapters(listener);
	}
	void scrapPages(String chapterUrl, PageScrapListener listener) throws Exception ;
	String getPageImageUrl(String pageUrls) throws Exception;
	String urlColumn();
	void setJsoupFactory(JsoupFactory factory);
}
