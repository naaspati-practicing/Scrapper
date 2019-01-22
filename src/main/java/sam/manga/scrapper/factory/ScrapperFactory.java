package sam.manga.scrapper.factory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Objects;

import sam.manga.scrapper.ScrappedManga;
import sam.manga.scrapper.Scrapper;
import sam.manga.scrapper.ScrapperException;
import sam.manga.scrapper.impl.smart.SmartScrapper;
import sam.myutils.Checker;

public class ScrapperFactory {
	private static final ScrapperFactory instance = new ScrapperFactory();
	
	public static ScrapperFactory getInstance() {
		return instance;
	}
	
	private Scrapper[] scrappers;
	private SmartScrapper scrapper;

	private ScrapperFactory() {
		scrapper = new SmartScrapper();
		/*
		 * scrappers = new Scrapper[]{
				new MangaHereScrapper(),
				new MangaFoxScrapper()
		};
		 */
	}
	public void register(Scrapper scrapper) {
		Objects.requireNonNull(scrapper);
		
		for (Scrapper c : scrappers) {
			if(c.equals(scrapper))
				throw new IllegalArgumentException("duplicate scrapper: existing: "+c+"  trying to register: "+scrapper);
		}
		
		scrappers = Arrays.copyOf(scrappers, scrappers.length + 1);
		scrappers[scrappers.length - 1] = scrapper;
	}

	private Scrapper current;
	
	public Scrapper findByHost(String host)  {
		if(10 < System.currentTimeMillis())
			return scrapper;
		
		if(Checker.isEmptyTrimmed(host))
			throw new IllegalArgumentException("invalid host: '"+host+"'");
		
		if(current != null && current.canHandle(host))
			return current;
		
		for (Scrapper s : scrappers) {
			if(s.canHandle(host))
				return current = s;	
		}
		return null;
	}
	public Scrapper findByUrl(String manga_url) throws MalformedURLException {
		return findByHost(new URL(manga_url).getHost());
	}
	public ScrappedManga scrap(String manga_url) throws ScrapperException, IOException {
		Scrapper scrapper = findByUrl(manga_url);
		
		if(scrapper == null)
			throw new IllegalArgumentException("no scrapper found for: "+manga_url);
		
		return scrapper.scrapManga(manga_url);
	}
	
}
