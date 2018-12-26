package sam.manga.scrapper.impl.mangahere;

import org.jsoup.nodes.Element;

import sam.manga.samrock.urls.MangaUrlsMeta;
import sam.manga.scrapper.PageScrapListener;
import sam.manga.scrapper.ScrappedManga;
import sam.manga.scrapper.Scrapper;
import sam.manga.scrapper.UrlType;
import sam.manga.scrapper.jsoup.JsoupFactory;

public class MangaHereScrapper implements Scrapper {
	private JsoupFactory factory;
	@Override
	public void setJsoupFactory(JsoupFactory factory) {
		this.factory = factory;
	}

	@Override
	public ScrappedManga scrapManga(String mangaUrl) throws Exception {
		return new MangaHereManga(factory, mangaUrl);
	}
	@Override
	public void scrapPages(String chapterUrl, PageScrapListener listener) throws Exception {
		new MangaHereChapter(factory).getPages(chapterUrl, listener);
	}
	
	@Override
	public String getPageImageUrl(final String pageUrl) throws Exception {
		Element e = factory.getDocument(pageUrl, UrlType.PAGE)
				.getElementById("image");
		
		return e == null ? null : e.attr("src");
	}
	@Override
	public String urlColumn() {
		return MangaUrlsMeta.MANGAHERE;
	}
}
