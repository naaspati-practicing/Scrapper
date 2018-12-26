package sam.manga.scrapper.impl.mangafox;

import static sam.manga.scrapper.impl.mangafox.ChapInfo.END;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLConnection;
import java.util.concurrent.ConcurrentHashMap;

import sam.internetutils.InternetUtils;
import sam.manga.samrock.urls.MangaUrlsMeta;
import sam.manga.scrapper.PageScrapListener;
import sam.manga.scrapper.ScrappedManga;
import sam.manga.scrapper.Scrapper;
import sam.manga.scrapper.ScrapperException;
import sam.manga.scrapper.UrlType;
import sam.manga.scrapper.jsoup.JsoupFactory;
import sam.reference.WeakMap;

public class MangaFoxScrapper implements Scrapper {
	private JsoupFactory factory;
	
	@Override
	public void setJsoupFactory(JsoupFactory factory) {
		this.factory = factory;
	}
	
	@Override
	public ScrappedManga scrapManga(String mangaUrl) throws Exception {
		return new MangaFoxManga(factory, mangaUrl);
	}
	@Override
	public void scrapPages(String chapterUrl, PageScrapListener listener) throws Exception {
		new MangaFoxChapter(factory).getPages(chapterUrl, listener);
	}
	
	private static final WeakMap<String, String> pageUrl_imgUrl = new WeakMap<>(new ConcurrentHashMap<>());
	
	@Override
	public String getPageImageUrl(final String pageUrl) throws Exception {
		final int index = pageUrl.lastIndexOf(END);
		if(index < 0 )
			throw new IllegalArgumentException("pageUrl must end with\""+END+"\", pageUrl: "+pageUrl);
		
		String number = pageUrl.substring(index+END.length());
		if(!number.chars().allMatch(c -> c > '0' && c <= '9'))
			throw new IllegalArgumentException("bad page_number: "+number);
		
		String result = pageUrl_imgUrl.get(pageUrl);
		if(result != null)
			return result;
		
		final String chUrl = pageUrl.substring(0, pageUrl.lastIndexOf('#'));
		ChapInfo info = MangaFoxChapter.chapInfo(chUrl, factory, UrlType.PAGE);
		
		int attept = 0;
		String url = info.chapterfun_ashx.concat(number);
		InputStream script = reader(url);
		
		while(attept++ < 10 && script == null) {
			Thread.sleep(1000);
			script = reader(url);
		}
		if(script == null)
			throw new ScrapperException("failed to load img-url-script: \nurl: "+url+"\npage_url: "+pageUrl);
			
		
		try(JsEngine e = JsEngine.get();
				InputStream is = script;
				InputStreamReader reader = new InputStreamReader(is, "utf-8");
				BufferedReader breader = new BufferedReader(reader);
				) {
			e.eval(breader);
			String[] urls = e.urls;
			if(urls == null)
				return null;
			
			if(urls.length > 2)
				throw new ScrapperException("urls.length("+urls.length+") > 2");
			
			if(urls.length == 0)
				return null;
			if(urls.length == 2)
				pageUrl_imgUrl.put(pageUrl.substring(0, pageUrl.length() - number.length()), info.appendProtocol(urls[1]));
				
			return info.appendProtocol(urls[0]);
			
		}
	}

	private InputStream reader(String url) throws IOException {
		URLConnection c = InternetUtils.connection(url);
		c.connect();
		if(c.getContentLength() == 0)
			return null;
		
		return c.getInputStream();
	}
	@Override
	public String urlColumn() {
		return MangaUrlsMeta.MANGAFOX;
	}
}
