package sam.manga.scrapper.jsoup;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Optional;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import sam.manga.scrapper.UrlType;
import sam.myutils.System2;

public class DefaultJsoupFactory implements JsoupFactory {

	@Override
	public Document getDocument(String url, UrlType type) throws MalformedURLException, IOException {
		Connection c = Jsoup.connect(url)
				.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.29 Safari/537.36")
				.timeout(getConnectionTimeout());

		c.cookie("isAdult", "1");
		return c.get();
	}

	private int connectionTimeout = Optional.ofNullable(System2.lookupAny("scrapper.connectionTimeout", "scrapper.connection_timeout", "SCRAPPER_CONNECTION_TIMEOUT")).map(Integer::parseInt).orElse(5_000);

	@Override
	public int getConnectionTimeout() {
		return connectionTimeout;
	}
	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}
}
