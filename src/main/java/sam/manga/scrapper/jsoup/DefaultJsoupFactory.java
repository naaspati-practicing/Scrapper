package sam.manga.scrapper.jsoup;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Optional;

import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;

import sam.internetutils.ConnectionConfig;
import sam.myutils.System2;

public class DefaultJsoupFactory implements JsoupFactory {
	public DefaultJsoupFactory() { }
	
	@Override
	public Document getDocument(String url) throws MalformedURLException, IOException {
		return connection(url).get();
	}

	private int connectionTimeout = Optional.ofNullable(System2.lookupAny("scrapper.connectionTimeout", "scrapper.connection_timeout", "SCRAPPER_CONNECTION_TIMEOUT")).map(Integer::parseInt).orElse(5_000);

	@Override
	public int getConnectionTimeout() {
		return connectionTimeout;
	}
	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}
	@Override
	public HttpConnection connection(String url) {
		return (HttpConnection) HttpConnection.connect(url)
		.cookie("isAdult", "1")
		.header("User-Agent", ConnectionConfig.DEFAULT_USER_AGENT);
	}
}
