package sam.manga.scrapper.impl.mangahere;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import sam.logging.MyLoggerFactory;
import sam.manga.scrapper.FailedChapter;
import sam.manga.scrapper.ScrappedChapter;
import sam.manga.scrapper.ScrappedManga;
import sam.manga.scrapper.ScrapperException;
import sam.manga.scrapper.UrlType;
import sam.manga.scrapper.jsoup.JsoupFactory;
import sam.string.StringUtils;

public class MangaHereManga implements ScrappedManga {
	private final Logger LOGGER = MyLoggerFactory.logger(getClass());

	protected String title, author, description, thumb, status, rank;
	protected List<String> tags;
	protected final Document doc;
	protected final String manga_urls;

	public MangaHereManga(JsoupFactory jsoupFactory, String url) throws ScrapperException, IOException {
		this.doc = jsoupFactory.getDocument(url, UrlType.MANGA);
		this.manga_urls = url;
	}
	private boolean loaded = false;
	private void load() {
		if(loaded)
			return;

		LOGGER.fine(() -> "meta loaded: "+manga_urls);

		loaded = true;

		Function<String, Elements> cls = doc::getElementsByClass;
		BiFunction<String, String, Stream<Element>> cls2 = (classname, tagname) -> cls.apply(classname).stream().flatMap(e -> e.getElementsByTag("a").stream());

		title = cls.apply("detail-info-right-title-font").get(0).text();
		thumb = Optional.of(cls.apply("detail-info-cover-img")).filter(e -> !e.isEmpty()).map(e -> e.get(0).attr("src")).orElse(null);
		status = Optional.of(cls.apply("detail-info-right-title-tip")).filter(e -> !e.isEmpty()).map(e -> e.get(0).text()).orElse(null);
		author = cls2.apply("detail-info-right-say", "a")
				.filter(e -> e.attr("href").startsWith("/search/author/"))
				.findFirst()
				.map(e -> e.attr("title"))
				.orElse(null);

		tags = cls2.apply("detail-info-right-tag-list", "a")
				.filter(e -> e.attr("href").startsWith("/directory/"))
				.map(e -> e.attr("title"))
				.collect(Collectors.toList());

		description = cls.apply("fullcontent").stream()
				.map(p -> p.text())
				.max(Comparator.comparingInt(String::length))
				.orElse(null);

		rank = cls.apply("item-score").get(0).text();
	}

	@Override
	public String getTitle() {
		load();
		return title;
	}

	@Override
	public String getAuthor() {
		load();
		return author;
	}
	@Override
	public List<String> getTags() {
		load();
		return tags;
	}

	@Override
	public String getDescription() {
		load();
		return description; 
	}
	@Override
	public String getThumb() {
		load();
		return thumb;
	}
	@Override
	public String getStatus() {
		load();
		return status;
	}
	@Override
	public String getRank() {
		load();
		return rank;
	}
	private static final Pattern volumePattern = Pattern.compile("v\\d+.+");

	@Override
	public  ScrappedChapter[] getChapters() {
		return doc
				.getElementById("chapterlist")
				.getElementsByTag("li")
				.stream()
				.flatMap(t -> t.getElementsByTag("a").stream())
				.map(elm -> {
					String url = elm.absUrl("href");
					url = url.endsWith("html") ? url : url + (url.endsWith("/") ? "1.html" : "/1.html");
					String[] splits = StringUtils.split(url, '/');
					Element titles = elm.getElementsByClass("title1").first();
					String title = titles == null ? null : titles.text();

					String v = null, n = null;
					for (int i = splits.length - 1; i >= 1; i--) {
						if(v != null && n != null)
							break;
						String s = splits[i];
						if(s.isEmpty())
							continue;
						if(v == null && s.charAt(0) == 'v')
							v = s.substring(1); 
						if(n == null && s.charAt(0) == 'c')
							n = s.substring(1);  
					}

					String volume = v != null && volumePattern.matcher(v).matches() ? v : "vUnknown"; 
					double number = 0 ;
					try {
						number = Double.parseDouble(n);
					} catch (NumberFormatException|NullPointerException e) {
						return new FailedChapter(e, n, v, title, url);
					}
					return new ScrappedChapter(number, volume, title, url);
				})
				.toArray(ScrappedChapter[]::new);
	}

	public Document getDoc() {
		return doc;
	}
}
