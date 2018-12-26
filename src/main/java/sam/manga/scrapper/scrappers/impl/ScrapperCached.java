package sam.manga.scrapper.scrappers.impl;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static sam.myutils.Checker.exists;
import static sam.myutils.MyUtilsPath.TEMP_DIR;
import static sam.myutils.System2.lookup;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiPredicate;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import sam.manga.scrapper.ChapterScrapListener;
import sam.manga.scrapper.PageScrapListener;
import sam.manga.scrapper.ScrappedManga;
import sam.manga.scrapper.Scrapper;
import sam.manga.scrapper.UrlType;
import sam.manga.scrapper.jsoup.DefaultJsoupFactory;
import sam.manga.scrapper.jsoup.JsoupFactory;
import sam.myutils.System2;

public class ScrapperCached implements JsoupFactory, Scrapper  {
	public final File cacheDir = Optional.ofNullable(lookup("SAMROCK_CACHE_DIR")).map(File::new).orElseGet(() -> new File(TEMP_DIR.toFile(), "samrock-cache"));
	public final File htmlCache = new File(cacheDir, "html");
	public final String baseUrl;
	private final DefaultJsoupFactory defaultJsoupFactory = new DefaultJsoupFactory();
	private boolean cacheIt = System2.lookupBoolean("CACHED_SCRAPPER", true);
	private BiPredicate<String, UrlType> cacheFilter = (url, type) -> type != UrlType.PAGE;
	private EnumMap<UrlType, File> paths = new EnumMap<>(UrlType.class);
	private HashMap<String, File> cacheMap;
	private final Scrapper scrapper;
	private final ScrapperType scrapperType;

	public static ScrapperCached createDefaultInstance() throws InstantiationException, IllegalAccessException, IOException {
		String type = System2.lookup("SCRAPPER_TYPE");
		if(type == null)
			return new ScrapperCached(ScrapperType.MANGAHERE.base_url, ScrapperType.MANGAHERE);
		ScrapperType st = ScrapperType.valueOf(type.toUpperCase());
		String base_url = System2.lookup("SCRAPPER_BASE_URL");
		if(base_url != null)
			return new ScrapperCached(base_url, st);
		
		return new ScrapperCached(st.base_url, st);
	}

	/**
	 * 
	 * @param cacheFilter should return true (default), if wanted to be cached; 
	 */
	public void setCacheFilter(BiPredicate<String, UrlType> cacheFilter) {
		this.cacheFilter = cacheFilter;
	}
	public ScrapperCached(String baseUrl, ScrapperType scrapperType) throws IOException, InstantiationException, IllegalAccessException {
		this.scrapperType = scrapperType;
		this.baseUrl = baseUrl;
		this.scrapper = scrapperType.cls.newInstance();
		this.scrapper.setJsoupFactory(this);
	}
	public String getBaseUrl() {  return baseUrl; }
	public File getCacheDir() { return cacheDir; }

	@Override
	public int getConnectionTimeout() {
		return defaultJsoupFactory.getConnectionTimeout();
	}
	private static Charset charset = StandardCharsets.UTF_8;
	@Override
	public synchronized Document getDocument(String url, UrlType type) throws MalformedURLException, IOException {
		Objects.requireNonNull(url);
		Objects.requireNonNull(type);

		if(!cacheIt || !cacheFilter.test(url, type))
			return defaultJsoupFactory.getDocument(url, type);

		if(cacheMap == null)  {
			File p = new File(cacheDir, "cacheMap.tsv");
			cacheMap = new HashMap<>();
			if(p.exists()) {
				Files.lines(p.toPath())
				.forEach(s -> {
					int n = s.indexOf('\t');
					if(n < 0) return;
					cacheMap.put(s.substring(0, n), new File(s.substring(n+1)));
				});
			}
		}

		File p = cacheMap.get(url);
		if(exists(p))
			return Jsoup.parse(p, "utf-8", baseUrl);

		File path = getHtmlCachePath(url, type);
		cacheMap.put(url, path);

		Document doc = defaultJsoupFactory.getDocument(url, type);
		Files.write(path.toPath(), doc.toString().getBytes(charset), CREATE, TRUNCATE_EXISTING);

		return doc;
	}

	public File getHtmlCachePath(String url, UrlType type) {
		File dir = paths.get(type);
		if(dir == null) {
			paths.put(type, dir = new File(cacheDir, type.toString()));
			dir.mkdirs();
		}
		return new File(dir, url.hashCode() + ".html");

	}
	@Override
	public ScrappedManga scrapManga(String mangaUrl) throws Exception {
		return scrapper.scrapManga(mangaUrl);
	}
	@Override
	public void scrapChapters(ScrappedManga manga, ChapterScrapListener listener) throws Exception {
		scrapper.scrapChapters(manga, listener);
	}
	@Override
	public void scrapPages(String chapterUrl, PageScrapListener listener) throws Exception {
		scrapper.scrapPages(chapterUrl, listener);
	}
	@Override
	public String getPageImageUrl(String pageUrls) throws Exception {
		return scrapper.getPageImageUrl(pageUrls);
	}
	@Override
	public String urlColumn() {
		return scrapper.urlColumn();
	}
	@Override
	public void setJsoupFactory(JsoupFactory factory) {
		scrapper.setJsoupFactory(factory);
	}
	public ScrapperType getScrapperType() {
		return scrapperType;
	}
}
