package sam.manga.scrapper.jsoup;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import sam.manga.scrapper.ScrapperException;
import sam.manga.scrapper.UrlType;
import sam.myutils.System2;

public class DefaultJsoupFactory implements JsoupFactory {
	private final OkHttpClient client;
	
	public DefaultJsoupFactory() {
		client = new OkHttpClient.Builder()
				.connectTimeout(getConnectionTimeout(), TimeUnit.MILLISECONDS)
				.connectionPool(new ConnectionPool(5, 2, TimeUnit.SECONDS))
				.followRedirects(true)
				.followSslRedirects(true)
				.retryOnConnectionFailure(true)
				.build();
	}

	@Override
	public Document getDocument(String url, UrlType type) throws IOException, ScrapperException {
		return request(url, body -> Jsoup.parse(body.byteStream(), body.contentType().charset().toString(), url));
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
	public <E> E request(String url, ResponseConsumer<E> consumer) throws IOException, ScrapperException {
		Request r = new Request.Builder()
				.url(url)
				.header("Cookie", "isAdult=1")
				.build();
		
		try(Response rs = client.newCall(r).execute();
				ResponseBody body = rs.body();) {
			return consumer.consume(body);
		}
	}
}
