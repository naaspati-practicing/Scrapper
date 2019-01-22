package sam.manga.scrapper.impl.mangafox;

import java.util.function.Supplier;

import sam.manga.scrapper.impl.mangahere.MangaHereChapter;
import sam.manga.scrapper.jsoup.JsoupFactory;

public class MangaFoxChapter extends MangaHereChapter {
	public MangaFoxChapter(Supplier<JsoupFactory> jsoup, double number, String volume, String title, String url) {
		super(jsoup, number, volume, title, url);
	}
}
