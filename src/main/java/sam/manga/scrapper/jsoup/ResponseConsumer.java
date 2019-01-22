package sam.manga.scrapper.jsoup;

import java.io.IOException;

import okhttp3.ResponseBody;
import sam.manga.scrapper.ScrapperException;

@FunctionalInterface
public interface ResponseConsumer<E> {
	public E consume(ResponseBody response) throws IOException, ScrapperException;
}
