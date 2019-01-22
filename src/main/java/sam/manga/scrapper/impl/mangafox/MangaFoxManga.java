package sam.manga.scrapper.impl.mangafox;

import java.io.IOException;

import sam.manga.scrapper.ScrappedChapter;
import sam.manga.scrapper.ScrapperException;
import sam.manga.scrapper.impl.mangahere.MangaHereManga;
import sam.manga.scrapper.jsoup.JsoupFactory;

public class MangaFoxManga extends MangaHereManga {

	public MangaFoxManga(JsoupFactory jsoupFactory, String url) throws ScrapperException, IOException {
		super(jsoupFactory, url);
	}
	@Override
	protected ScrappedChapter newInstance(double number, String volume, String title, String url) {
		return new MangaFoxChapter(this::getJsoupFactory, number, volume, title, url);
	}
}
