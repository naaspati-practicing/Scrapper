package sam.manga.scrapper.impl.mangafox;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

import sam.collection.Pair;
import sam.logging.MyLoggerFactory;
import sam.manga.scrapper.ScrappedPage;
import sam.manga.scrapper.ScrapperException;
import sam.manga.scrapper.UrlType;
import sam.manga.scrapper.impl.mangafox.JsEngine.Result;
import sam.manga.scrapper.jsoup.JsoupFactory;
import sam.reference.ReferenceUtils;
import sam.reference.WeakMap;
import sam.string.StringUtils;

public class MangaFoxChapter {
	private final Logger LOGGER = MyLoggerFactory.logger(getClass());
	
	private final JsoupFactory jsoupFactory;
	public MangaFoxChapter(JsoupFactory jsoupFactory) {
		this.jsoupFactory = jsoupFactory;
	}
	
	private static final WeakMap<String, ChapInfo> chUrl_ajaxUrl = new WeakMap<>(new ConcurrentHashMap<>());
	private static final AtomicReference<WeakReference<Pair<String, ChapInfo>>> current = new AtomicReference<>();

	public ScrappedPage[] getPages(String chapter_url) throws ScrapperException, IOException {
		if(!chapter_url.endsWith("/1.html"))
			throw new IllegalArgumentException("url must end with /1.html");
	
		ChapInfo info = chapInfo(chapter_url, jsoupFactory, UrlType.CHAPTER, LOGGER, true);
		chapter_url = chapter_url.concat("#ipg");
		
		LOGGER.fine(() -> info.toString());
		
		String[] imgurls = info.imgUrls;
		ScrappedPage[] pages = new ScrappedPage[info.imagecount];
		
		for (int j = 0; j < info.imagecount; j++)
			pages[j] = new ScrappedPage(chapter_url, j, chapter_url.concat(Integer.toString(j+1)), imgurls == null ? null : imgurls[j]); 
			
		return pages;
	}

	static ChapInfo chapInfo(String chapter_url, JsoupFactory jsoup, UrlType owner, Logger logger, boolean newinfo) throws ScrapperException, IOException {
		if(!chapter_url.endsWith("/1.html"))
			throw new IllegalArgumentException("url must end with /1.html");

		if(!newinfo) {
			Pair<String, ChapInfo> pair = ReferenceUtils.get(current.get());
			if(pair.key.equals(chapter_url))
				return pair.value;
			
			ChapInfo info = chUrl_ajaxUrl.get(chapter_url);
			if(info != null) {
				current.set(new WeakReference<>(new Pair<>(chapter_url, info)));
				return info;	
			}
		}

		String[] res = {null, null};

		jsoup.getDocument(chapter_url, owner)
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
		ChapInfo info = new ChapInfo(imagecount[0], chapterid[0], result.val, chapter_url, imgurls);
		
		if(imgurls != null) {
			for (int i = 0; i < imgurls.length; i++)
				imgurls[i] = info.appendProtocol(imgurls[i]);
		}
		
		if(logger != null)
			logger.fine(() -> String.format("chapterid: %s, imagecount: %s" , chapterid[0], imagecount[0]));
		
		chUrl_ajaxUrl.put(chapter_url, info);
		current.set(new WeakReference<>(new Pair<>(chapter_url, info)));
		return info;
	}
}
