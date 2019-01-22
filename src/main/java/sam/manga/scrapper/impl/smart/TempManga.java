package sam.manga.scrapper.impl.smart;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.jsoup.select.Elements;

import sam.manga.scrapper.FailedChapter;
import sam.manga.scrapper.ScrappedChapter;
import sam.manga.scrapper.ScrapperException;
import sam.manga.scrapper.impl.mangahere.MangaHereManga;
import sam.manga.scrapper.jsoup.JsoupFactory;
import sam.myutils.Checker;
import sam.myutils.System2;

public class TempManga extends MangaHereManga {
	
	private static final Map<String, String> replace;
	static {
		replace = Optional.ofNullable(System2.lookup("replace_urls"))
				.map(String::trim)
				.filter(Checker::isNotEmpty)
				.map(Paths::get)
				.filter(Files::exists)
				.map(path -> {
					Map<String, String> map = new HashMap<>();
					
					try {
						Files.lines(path)
								.forEach(s -> {
									int n = s.indexOf('\t');
									if(n < 0)
										return;
									map.put(s.substring(0, n).trim(), s.substring(n+1).trim());
								});
					} catch (IOException e) {
						e.printStackTrace();
					}
					return map;
				})
				.filter(map -> !map.isEmpty())
				.map(Collections::unmodifiableMap)
				.orElse(Collections.emptyMap());
	}

	public TempManga(JsoupFactory jsoupFactory, String url) throws ScrapperException, IOException {
		super(jsoupFactory, url);
	}
	@Override
	public ScrappedChapter[] getChapters() throws ScrapperException, IOException  {
		// "https://mangakakalot.com/"
		String url2 = replace.get(manga_urls);
		
		if(url2 == null)
			url2 = manga_urls.replace("http://www.mangahere.cc/", "https://manganelo.com/");
		Elements els = jsoupFactory.getDocument(url2)
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
