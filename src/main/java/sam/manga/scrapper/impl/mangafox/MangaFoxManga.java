package sam.manga.scrapper.impl.mangafox;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import sam.manga.scrapper.ChapterScrapListener;
import sam.manga.scrapper.ScrappedManga;
import sam.manga.scrapper.UrlType;
import sam.manga.scrapper.jsoup.JsoupFactory;
import sam.string.StringUtils;

public class MangaFoxManga implements ScrappedManga {
	private String title, author, description, thumb;
	private List<String> tags;
	private Document doc;
	private Element details;

	public MangaFoxManga(JsoupFactory jsoupFactory, String url) throws Exception {
		this.doc = jsoupFactory.getDocument(url, UrlType.MANGA);
		this.details = doc.getElementsByClass("detail-info").get(0);
	}
	@Override
	public String getTitle() {
		if(title != null) return title;
		Element e = details.getElementsByClass("detail-info-right-title-font").first();
		return title = e == null ? null : e.text();
	}

	@Override
	public String getAuthor() {
		if(author != null) return author;
		Element e = details.getElementsByClass("detail-info-right-say").first();
		if(e != null)
			e = e.getElementsByTag("a").first();
		return author = e == null ? null : e.text();
	}
	@Override
	public List<String> getTags() {
		if(tags != null) return tags;
		Element e = details.getElementsByClass("detail-info-right-tag-list").first();
		return tags = e == null ? null : e.getElementsByTag("a").stream().map(Element::text).collect(Collectors.toList());
	}

	@Override
	public String getDescription() {
		if(description != null) return description;
		Element e = details.getElementsByClass("fullcontent").first();
		return description = e == null ? null : e.text();
	}
	@Override
	public String getThumb() {
		if(thumb != null) return thumb;
		Element e = details.getElementsByClass("detail-info-cover-img").first();
		return thumb = e == null ? null : e.attr("src");
	}
	private static final Pattern volumePattern = Pattern.compile("v\\d+.+");

	@Override
	public  void getChapters(ChapterScrapListener listener) {
		doc
		.getElementById("chapterlist")
		.getElementsByTag("li")
		.stream()
		.flatMap(t -> t.getElementsByTag("a").stream())
		.forEach(elm -> {
			String url = elm.absUrl("href");
			url = url.endsWith("html") ? url : url + (url.endsWith("/") ? "1.html" : "/1.html");
			String[] splits = StringUtils.split(url, '/');
			Element titles = elm.getElementsByClass("title1").first();
			String title = titles == null ? null : titles.text();
			
			String v = null, n = null;
			for (int i = splits.length - 1; i >= 1; i--) {
				if(v != null && n != null)
					break;
				String s = splits[i];
				if(s.isEmpty())
					continue;
				if(v == null && s.charAt(0) == 'v')
					v = s.substring(1); 
				if(n == null && s.charAt(0) == 'c')
					n = s.substring(1);  
			}

			String volume = v != null && volumePattern.matcher(v).matches() ? v : "vUnknown"; 
			double number = 0 ;
			try {
				number = Double.parseDouble(n);
			} catch (NumberFormatException|NullPointerException e) {
				listener.onChapterFailed(String.format("Bad Number[number:%s, volume:%s, url:%s ]", n,v, url), e,n,v, title, url);
				return;
			}
			listener.onChapterSuccess(number, volume, title, url);
		});
	}
	@Override
	public String getStatus() {
		return null;
	}
	@Override
	public String getRank() {
		return null;
	}
}
