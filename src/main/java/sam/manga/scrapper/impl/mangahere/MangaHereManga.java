package sam.manga.scrapper.impl.mangahere;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import sam.manga.samrock.urls.MangaUrlsUtils.MangaUrl;
import sam.manga.scrapper.ChapterScrapListener;
import sam.manga.scrapper.ScrappedManga;
import sam.manga.scrapper.UrlType;
import sam.manga.scrapper.jsoup.JsoupFactory;
import sam.myutils.MyUtilsException;

public class MangaHereManga implements ScrappedManga {
	private String title, author, description, thumb, status, rank;
	private List<String> tags;
	private Document doc;
	private final String manga_url;

	public MangaHereManga(JsoupFactory jsoupFactory, String url) throws Exception {
		this.doc = jsoupFactory.getDocument(url, UrlType.MANGA);
		this.manga_url = url;
	}

	private boolean loaded = false;
	private void load() {
		if(loaded)
			return;
		
		loaded = true;
		
		title = doc.getElementsByClass("title").stream().filter(e -> e.tagName().equals("h1")).findFirst().map(e -> e.text()).orElse(null);
		thumb = Optional.of(doc.select(".manga_detail_top img.img")).filter(e -> !e.isEmpty()).map(e -> e.get(0).attr("src")).orElse(null);
		
		doc.getElementsByClass("detail_topText")
		.get(0)
		.getElementsByTag("li")
		.forEach(e -> {
			Elements es = e.getElementsByTag("label");
			String key = es.isEmpty() ? null : es.get(0).text();
			if(key == null)
				return;
			
			switch (key) {
				case "Author(s):":
					author = e.getElementsByTag("a").first().text();
					break;
				case "Genre(s):":
					tags = Arrays.asList(e.ownText().split(", "));
					break;
				case "Status:":
					status = e.ownText();
					if(status != null) {
						int n = status.indexOf(' ');
						if(n > 0)
							status = status.substring(0, n);
					}
					break;
				case "Rank:":
					rank = e.ownText();
					break;
				default:
					if("Summary:".equals(es.get(0).ownText()))
						description = Optional.of(e.getElementById("show"))
						.map(s -> s.ownText())
						.orElse(null);
					break;
			}
		});
	}
	
	@Override
	public String getTitle() {
		load();
		return title;
	}

	@Override
	public String getAuthor() {
		load();
		return author;
	}
	@Override
	public List<String> getTags() {
		load();
		return tags;
	}

	@Override
	public String getDescription() {
		return description; 
	}
	@Override
	public String getThumb() {
		load();
		return thumb;
	}
	@Override
	public String getStatus() {
		load();
		return status;
	}

	@Override
	public  void getChapters(ChapterScrapListener listener) {
		String protocol = MyUtilsException.noError(() -> new URL(manga_url).getProtocol().concat(":"));
		
		doc.select(".detail_list ul li span.left")
		.forEach(elm -> {
			String url = elm.getElementsByTag("a").get(0).attr("href");
			String title = elm.ownText();
			int end = url.lastIndexOf('/');
			int start = end != url.length() - 1 ? end : url.lastIndexOf('/', end - 1);
			end = end == start ? url.length() : end;
			start++;
			
			if(url.charAt(start) == 'c' || url.charAt(start) < '0' || url.charAt(start) > '9')
				start++;
			
			String n = url.substring(start, end);
			String v = "UKNONWN";
			double number = 0 ;
			
			try {
				number = Double.parseDouble(n.charAt(0) == '#' ? n.substring(1) : n);
			} catch (NumberFormatException|NullPointerException e) {
				listener.onChapterFailed(String.format("Bad Number[number:%s, volume:%s, url:%s ]", n,v, url), e,n,v, title, url);
				return;
			}
			listener.onChapterSuccess(number, v, title, url.startsWith("//") ? protocol.concat(url) : url);
		});
	}

	@Override
	public String getRank() {
		return rank;
	}
}
