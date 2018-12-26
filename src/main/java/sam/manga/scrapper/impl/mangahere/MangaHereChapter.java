package sam.manga.scrapper.impl.mangahere;

import sam.manga.scrapper.PageScrapListener;
import sam.manga.scrapper.ScrapperException;
import sam.manga.scrapper.UrlType;
import sam.manga.scrapper.jsoup.JsoupFactory;

public class MangaHereChapter {
	private final JsoupFactory jsoupFactory;
	public MangaHereChapter(JsoupFactory jsoupFactory) {
		this.jsoupFactory = jsoupFactory;
	}

	public void getPages(String chapter_url, PageScrapListener listener) throws Exception {
		int max = jsoupFactory.getDocument(chapter_url, UrlType.CHAPTER)
		.getElementsByTag("select")
		.stream()
		.flatMap(e -> e.getElementsByTag("option").stream())
		.map(e -> e.text())
		.mapToInt(e -> {
			try {
				return Integer.parseInt(e);
			} catch (NumberFormatException e2) {
				return -10;
			}
		})
		.filter(e -> e != -10)
		.max()
		.orElseThrow(() -> new ScrapperException("no images found"));
		
		chapter_url = chapter_url.charAt(chapter_url.length() - 1) == '/' ? chapter_url : chapter_url.concat("/");  
		listener.onPageSuccess(chapter_url, 0, chapter_url, null);
		
		for (int i = 2; i <= max; i++) 
			listener.onPageSuccess(chapter_url, i - 1, chapter_url.concat(Integer.toString(i)).concat(".html"), null);
	}
}
