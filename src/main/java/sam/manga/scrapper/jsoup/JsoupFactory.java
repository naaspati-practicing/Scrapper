package sam.manga.scrapper.jsoup;

import org.jsoup.nodes.Document;

import sam.manga.scrapper.UrlType;

public interface JsoupFactory {
	Document getDocument(String url, UrlType owner) throws Exception;
	int getConnectionTimeout();
}
