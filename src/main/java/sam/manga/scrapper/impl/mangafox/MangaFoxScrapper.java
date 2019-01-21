package sam.manga.scrapper.impl.mangafox;

import static sam.manga.scrapper.impl.mangafox.ChapInfo.END;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLConnection;
import java.util.stream.Collectors;

import sam.internetutils.InternetUtils;
import sam.manga.samrock.urls.MangaUrlsMeta;
import sam.manga.scrapper.ScrappedManga;
import sam.manga.scrapper.ScrappedPage;
import sam.manga.scrapper.Scrapper;
import sam.manga.scrapper.ScrapperException;
import sam.manga.scrapper.UrlType;
import sam.manga.scrapper.impl.mangafox.JsEngine.Result;
import sam.manga.scrapper.jsoup.JsoupFactory;

public class MangaFoxScrapper implements Scrapper {
	protected JsoupFactory factory;
	
	@Override
	public void setJsoupFactory(JsoupFactory factory) {
		this.factory = factory;
	}
	
	@Override
	public ScrappedManga scrapManga(String mangaUrl) throws ScrapperException, IOException {
		return new MangaFoxManga(factory, mangaUrl);
	}
	@Override
	public ScrappedPage[] scrapPages(String chapterUrl) throws ScrapperException, IOException {
		return new MangaFoxChapter(factory).getPages(chapterUrl);
	}
	
	@Override
	public String[] getPageImageUrl(final String pageUrl) throws ScrapperException, IOException{
		final int index = pageUrl.lastIndexOf(END);
		if(index < 0 )
			throw new IllegalArgumentException("pageUrl must end with\""+END+"\", pageUrl: "+pageUrl);
		
		String number = pageUrl.substring(index+END.length());
		if(!number.chars().allMatch(c -> c >= '0' && c <= '9'))
			throw new IllegalArgumentException("bad page_number: "+number);
		
		final String chUrl = pageUrl.substring(0, pageUrl.lastIndexOf('#'));
		ChapInfo info = MangaFoxChapter.chapInfo(chUrl, factory, UrlType.PAGE, null);
		
		int attept = 0;
		String url = info.chapterfun_ashx.concat(number);
		InputStream script = reader(url);
		
		while(attept++ < 10 && script == null) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				throw new ScrapperException(e1);
			}
			script = reader(url);
		}
		if(script == null)
			throw new ScrapperException("failed to load img-url-script: \nurl: "+url+"\npage_url: "+pageUrl);
			
		
		try(InputStream is = script;
				InputStreamReader reader = new InputStreamReader(is, "utf-8");
				BufferedReader breader = new BufferedReader(reader);
				) {
			Result e = JsEngine.parse(breader.lines().collect(Collectors.joining("\n")));
			
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
