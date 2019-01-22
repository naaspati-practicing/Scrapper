package sam.manga.scrapper.impl.mangafox;

import static sam.manga.scrapper.impl.mangafox.ChapInfo.END;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import sam.manga.samrock.urls.MangaUrlsMeta;
import sam.manga.scrapper.ScrappedManga;
import sam.manga.scrapper.ScrappedPage;
import sam.manga.scrapper.Scrapper;
import sam.manga.scrapper.ScrapperException;
import sam.manga.scrapper.UrlType;
import sam.manga.scrapper.impl.mangafox.JsEngine.Result;
import sam.manga.scrapper.jsoup.JsoupFactory;
import sam.myutils.Checker;

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
		if(!Checker.isInteger(number) || number.charAt(0) == '-' || (number.length() == 1 && number.charAt(0) == '0'))
			throw new IllegalArgumentException("bad page_number: "+number);

		final String chUrl = pageUrl.substring(0, pageUrl.lastIndexOf('#'));
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
			 s = execute(chUrl, number, false, false);
		} catch (IOException|ScrapperException e) {}
		
		if(s != null)
			return s;
		
		return execute(chUrl, number, true, true);
	}
	private String[] execute(String chUrl, String number, boolean newinfo, boolean throwError) throws IOException, ScrapperException {
		ChapInfo info = MangaFoxChapter.chapInfo(chUrl, factory, UrlType.PAGE, null, newinfo);
		String url = info.chapterfun_ashx.concat(number);


		return factory.request(url, body -> {
			try(InputStream is = body.byteStream();
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
		});
	}

	@Override
	public String urlColumn() {
		return MangaUrlsMeta.MANGAFOX;
	}
}
