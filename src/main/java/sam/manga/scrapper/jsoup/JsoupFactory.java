package sam.manga.scrapper.jsoup;

import java.io.IOException;

import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;

import sam.manga.scrapper.ScrapperException;

public interface JsoupFactory {
	Document getDocument(String url) throws ScrapperException, IOException;
	int getConnectionTimeout();
	HttpConnection connection(String url);
}
