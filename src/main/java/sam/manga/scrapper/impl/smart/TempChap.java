package sam.manga.scrapper.impl.smart;

import java.io.IOException;

import sam.manga.scrapper.ScrappedChapter;
import sam.manga.scrapper.ScrappedPage;
import sam.manga.scrapper.ScrapperException;
import sam.manga.scrapper.jsoup.JsoupFactory;

public class TempChap extends ScrappedChapter {
	private final JsoupFactory fac;

	public TempChap(JsoupFactory fac, double number, String volume, String title, String url) {
		super(number, volume, title, url);
		this.fac = fac;
	}

	@Override
	public String[] getPageImageUrl(String pageUrl) throws ScrapperException, IOException {
		return null;
	}

	@Override
	public ScrappedPage[] getPages() throws ScrapperException, IOException {
		int[] order = {0};
		return fac.getDocument(url)
				.getElementById("vungdoc")
				.getElementsByTag("img")
				.stream()
				.map(e -> new ScrappedPage(url, order[0]++, String.valueOf(order[0]), e.attr("src")))
				.toArray(ScrappedPage[]::new);
	}

}
