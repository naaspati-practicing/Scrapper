package sam.manga.scrapper.jsoup;

import java.io.IOException;
import java.io.InputStream;

// import okhttp3.ResponseBody;
import sam.manga.scrapper.ScrapperException;

@FunctionalInterface
public interface ResponseConsumer<E> {
	public E consume(InputStream response) throws IOException, ScrapperException;
}
