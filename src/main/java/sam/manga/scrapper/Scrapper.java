package sam.manga.scrapper;

import java.io.IOException;

public interface Scrapper {
	ScrappedManga scrapManga(String mangaUrl) throws ScrapperException, IOException;
	boolean canHandle(String host);
}
