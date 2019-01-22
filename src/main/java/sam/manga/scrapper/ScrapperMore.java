package sam.manga.scrapper;

import java.io.IOException;

public interface ScrapperMore extends Scrapper {
	String[] getPageImageUrl(String chapter_url, String page_url) throws ScrapperException, IOException ;
	ScrappedPage[] getPages(String s) throws ScrapperException, IOException ;
}
