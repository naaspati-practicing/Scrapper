package sam.manga.scrapper.jsoup;

import java.io.IOException;

import org.jsoup.nodes.Document;

import sam.manga.scrapper.ScrapperException;
import sam.manga.scrapper.UrlType;

public interface JsoupFactory {
	Document getDocument(String url, UrlType owner) throws ScrapperException, IOException;
	int getConnectionTimeout();
}
