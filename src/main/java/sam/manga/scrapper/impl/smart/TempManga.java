package sam.manga.scrapper.impl.smart;

import java.io.IOException;

import org.jsoup.select.Elements;

import sam.manga.scrapper.FailedChapter;
import sam.manga.scrapper.ScrappedChapter;
import sam.manga.scrapper.ScrapperException;
import sam.manga.scrapper.impl.mangahere.MangaHereManga;
import sam.manga.scrapper.jsoup.JsoupFactory;

public class TempManga extends MangaHereManga {

	public TempManga(JsoupFactory jsoupFactory, String url) throws ScrapperException, IOException {
		super(jsoupFactory, url);
	}
	@Override
	public ScrappedChapter[] getChapters() throws ScrapperException, IOException  {
		// "https://mangakakalot.com/"
		Elements els = jsoupFactory.getDocument(manga_urls.replace("http://www.mangahere.cc/", "https://manganelo.com/"))
				.getElementsByClass("chapter-list");
		if(els.isEmpty())
			return new ScrappedChapter[0];

		return els.get(0)
				.getElementsByTag("a")
				.stream()
				.map(e -> {
					String href = e.attr("href");
					int start = href.lastIndexOf('/');
					int end = href.length();
					if(start == href.length() - 1) {
						end--;
						start = href.lastIndexOf('/', end - 1);
					}

					String name = href.substring(start+1, end);
					String number = name.substring(name.indexOf('_')+1);

					try {
						return new TempChap(jsoupFactory, Double.parseDouble(number), null, e.attr("title"), href);
					} catch (NumberFormatException e2) {
						return new FailedChapter(e2, number, null, e.attr("title"), href);
					}
				})
				.toArray(ScrappedChapter[]::new);
	}

}
