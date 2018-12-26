package sam.manga.scrapper.impl.mangafox;

import java.util.concurrent.ConcurrentHashMap;

import sam.manga.scrapper.PageScrapListener;
import sam.manga.scrapper.ScrapperException;
import sam.manga.scrapper.UrlType;
import sam.manga.scrapper.jsoup.JsoupFactory;
import sam.reference.WeakMap;
import sam.string.StringUtils;

public class MangaFoxChapter {
	private final JsoupFactory jsoupFactory;
	public MangaFoxChapter(JsoupFactory jsoupFactory) {
		this.jsoupFactory = jsoupFactory;
	}
	
	private static final WeakMap<String, ChapInfo> chUrl_ajaxUrl = new WeakMap<>(new ConcurrentHashMap<>());

	public void getPages(String chapter_url, PageScrapListener listener) throws Exception {
		if(!chapter_url.endsWith("/1.html"))
			throw new IllegalArgumentException("url must end with /1.html");
	
		ChapInfo info = chapInfo(chapter_url, jsoupFactory, UrlType.CHAPTER);
		chapter_url = chapter_url.concat("#ipg");
		
		String[] imgurls = info.imgUrls;
		
		for (int j = 0; j < info.imagecount; j++) 
			listener.onPageSuccess(chapter_url, j, chapter_url.concat(Integer.toString(j+1)), imgurls == null ? null : imgurls[j]);
	}

	static ChapInfo chapInfo(String chapter_url, JsoupFactory jsoup, UrlType owner) throws Exception {
		if(!chapter_url.endsWith("/1.html"))
			throw new IllegalArgumentException("url must end with /1.html");

		ChapInfo info = chUrl_ajaxUrl.get(chapter_url);
		if(info != null)
			return info;

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
		
		try(JsEngine js = JsEngine.get();) {
			js.eval(res[1]);
			String[] imgurls = js.imgUrls;
			info = new ChapInfo(imagecount[0], chapterid[0], js.val, chapter_url, imgurls);
			
			if(imgurls != null) {
				for (int i = 0; i < imgurls.length; i++)
					imgurls[i] = info.appendProtocol(imgurls[i]);
			}
		}
		
		chUrl_ajaxUrl.put(chapter_url, info);
		return info;
	}
}
