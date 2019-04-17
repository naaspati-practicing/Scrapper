package sam.manga.scrapper.impl.mangahere;

import static sam.manga.scrapper.impl.mangahere.ChapInfo.END;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.jsoup.Connection.Method;
import org.jsoup.helper.HttpConnection;
import org.jsoup.helper.HttpConnection.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sam.collection.Pair;
import sam.internetutils.ConnectionConfig;
import sam.manga.scrapper.ScrappedChapter;
import sam.manga.scrapper.ScrappedPage;
import sam.manga.scrapper.ScrapperException;
import sam.manga.scrapper.impl.mangafox.MangaFoxChapter;
import sam.manga.scrapper.impl.mangahere.JsEngine.Result;
import sam.manga.scrapper.jsoup.JsoupFactory;
import sam.myutils.Checker;
import sam.reference.ReferenceUtils;
import sam.reference.WeakMap;
import sam.string.StringUtils;

public class MangaHereChapter extends ScrappedChapter {
	private static final Logger LOGGER = LoggerFactory.getLogger(MangaHereChapter.class);

	private static final WeakMap<String, ChapInfo> chUrl_ajaxUrl = new WeakMap<>(new ConcurrentHashMap<>());
	private static final AtomicReference<WeakReference<Pair<String, ChapInfo>>> current = new AtomicReference<>();
	
	private final Supplier<JsoupFactory> jsoup;
	
	public MangaHereChapter(Supplier<JsoupFactory> jsoup, double number, String volume, String title, String url) {
		super(number, volume, title, url);
		this.jsoup = jsoup;
	}

	MangaHereChapter(String chapter_url, Supplier<JsoupFactory> jsoup) {
		this(jsoup, -1, null, null, chapter_url);
	}

	@Override
	public ScrappedPage[] getPages() throws ScrapperException, IOException {
		String chapter_url = this.url;
		
		if(!chapter_url.endsWith("/1.html"))
			throw new IllegalArgumentException("url must end with /1.html");

		JsoupFactory jsop = jsoup.get();
		ChapInfo info = chapInfo(chapter_url, jsop, LOGGER, true);
		chapter_url = chapter_url.concat("#ipg");

		LOGGER.debug("{}", info);

		String[] imgurls = info.imgUrls;
		ScrappedPage[] pages = new ScrappedPage[info.imagecount];

		for (int j = 0; j < info.imagecount; j++)
			pages[j] = new ScrappedPage(chapter_url, j, chapter_url.concat(Integer.toString(j+1)), imgurls == null ? null : imgurls[j]); 

		return pages;
	}
	
	@Override
	public String[] getPageImageUrl(final String pageUrl) throws ScrapperException, IOException{
		final int index = pageUrl.lastIndexOf(END);
		if(index < 0 )
			throw new IllegalArgumentException("pageUrl must end with\""+END+"\", pageUrl: "+pageUrl);

		String number = pageUrl.substring(index+END.length());
		if(!Checker.isInteger(number) || number.charAt(0) == '-' || (number.length() == 1 && number.charAt(0) == '0'))
			throw new IllegalArgumentException("bad page_number: "+number);
		
		/*
		int tries = 0;
		while(tries++ < 3) {
			String[] s = execute(info, url, false);
			if(s != null)
				return s;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		 */

		String[] s = null;
		try {
			 s = execute(number, false, false);
		} catch (IOException|ScrapperException e) {}
		
		if(s != null)
			return s;
		
		return execute(number, true, true);
	}
	private String[] execute(String number, boolean newinfo, boolean throwError) throws IOException, ScrapperException {
		ChapInfo info = MangaFoxChapter.chapInfo(getUrl(), jsoup.get(), null, newinfo);
		String url = info.chapterfun_ashx.concat(number);
		
		org.jsoup.Connection.Response res = jsoup.get().connection(url)
				.header("Cookie", info.cookie)
				.header("X-Requested-With", "XMLHttpRequest")
				.header("User-Agent", ConnectionConfig.DEFAULT_USER_AGENT)
				.method(Method.GET)
				.execute();
		
		if(Integer.parseInt(res.header("Content-Length")) == 0) {
			if(throwError)
				throw new ScrapperException("empty script: "+url);
			else
				return null;		
		}
		
		try(InputStream is = res.bodyStream();
				InputStreamReader reader = new InputStreamReader(is, "utf-8");
				BufferedReader breader = new BufferedReader(reader);
				) {
			String script = breader.lines().collect(Collectors.joining("\n"));

			if(Checker.isEmptyTrimmed(script)) {
				if(throwError)
					throw new ScrapperException("empty script: "+url);
				else
					return null;
			}

			Result e = JsEngine.parse(script);

			String[] urls = e.imgUrls;
			if(urls == null)
				return null;

			for (int i = 0; i < urls.length; i++) 
				urls[i] = info.appendProtocol(urls[i]);

			if(urls.length > 2)
				throw new ScrapperException("urls.length("+urls.length+") > 2\n"+String.join("\n", urls));
			return urls;
		}	
	}

	public static ChapInfo chapInfo(String chapter_url, JsoupFactory jsoup, Logger logger, boolean newinfo) throws ScrapperException, IOException {
		if(!chapter_url.endsWith("/1.html"))
			throw new IllegalArgumentException("url must end with /1.html");

		if(!newinfo) {
			Pair<String, ChapInfo> pair = ReferenceUtils.get(current.get());
			if(pair != null && pair.key.equals(chapter_url))
				return pair.value;

			ChapInfo info = chUrl_ajaxUrl.get(chapter_url);
			if(info != null) {
				current.set(new WeakReference<>(new Pair<>(chapter_url, info)));
				return info;	
			}
		}

		HttpConnection con = jsoup.connection(chapter_url);
		con.method(Method.GET);

		Response response = (Response) con.execute();
		String[] res = new String[2];

		response.parse()
		.getElementsByTag("script")
		.stream()
		.map(s -> s.html())
		.forEach(s -> {
			if(res[1] == null && s.startsWith("eval("))
				res[1] = s;
			if(s.startsWith("var csshost"))
				res[0] = s;
		});

		String[] chapterid = {null};
		int[] imagecount = {-1};

		StringUtils.splitStream(res[0], ';')
		.map(String::trim)
		.filter(s -> s.startsWith("var "))
		.map(s -> s.substring(4))
		.forEach(s -> {
			int n = s.indexOf('=');
			if(n < 0)
				return;
			String key = s.substring(0, n).trim();
			if(key.equals("chapterid"))
				chapterid[0] = s.substring(n+1).trim();
			else if(key.equals("imagecount"))
				imagecount[0] = Integer.parseInt(s.substring(n+1).trim());
		});

		if(chapterid[0] == null)
			throw new ScrapperException("chapterid not found");
		if(imagecount[0] == -1)
			throw new ScrapperException("imagecount not found");

		Result result = JsEngine.parse(res[1]);

		String[] imgurls = result.imgUrls;
		ChapInfo info = new ChapInfo(response.header("Set-Cookie"), imagecount[0], chapterid[0], result.val, chapter_url, imgurls);

		if(imgurls != null) {
			for (int i = 0; i < imgurls.length; i++)
				imgurls[i] = info.appendProtocol(imgurls[i]);
		}

		if(logger != null)
			logger.debug("chapterid: {}, imagecount: {}" , chapterid[0], imagecount[0]);

		chUrl_ajaxUrl.put(chapter_url, info);
		current.set(new WeakReference<>(new Pair<>(chapter_url, info)));
		return info;
	}

}
