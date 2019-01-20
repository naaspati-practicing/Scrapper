import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import sam.manga.samrock.urls.MangaUrlsMeta;
import sam.manga.scrapper.ScrappedChapter;
import sam.manga.scrapper.ScrappedManga;
import sam.manga.scrapper.ScrappedPage;
import sam.manga.scrapper.Scrapper;
import sam.manga.scrapper.ScrapperException;
import sam.manga.scrapper.ScrapperType;
import sam.manga.scrapper.impl.mangafox.JsEngine;
import sam.manga.scrapper.impl.mangahere.MangaHereManga;
import sam.manga.scrapper.scrappers.impl.ScrapperCached;
import sam.myutils.Checker;
import sam.nopkg.Junk;

public class MangaTest {

	static ScrapperCached scrapper;
	static ScrappedManga manga;

	// http://www.mangahere.cc/manga/parallel_paradise/c067/chapterfun.ashx?cid=576498&key=&page=3
	String script_1_mangaere = "eval(function(p,a,c,k,e,d){e=function(c){return(c<a?\"\":e(parseInt(c/a)))+((c=c%a)>35?String.fromCharCode(c+29):c.toString(36))};if(!''.replace(/^/,String)){while(c--)d[e(c)]=k[c]||e(c);k=[function(e){return d[e]}];e=function(){return'\\\\w+'};c=1;};while(c--)if(k[c])p=p.replace(new RegExp('\\\\b'+e(c)+'\\\\b','g'),k[c]);return p;}('k e(){2 h=\"//a.7.b/f/3/6/4.0/c\";2 1=[\"/n.g?5=l&8=9\",\"/m.g?5=o&8=9\"];j(2 i=0;i<1.t;i++){u(i==0){1[i]=\"//a.7.b/f/3/6/4.0/c\"+1[i];q}1[i]=h+1[i]}r 1}2 d;d=e();p=s;',31,31,'|pvalue|var|manga|067|token|26739|mangahere|ttl|1548050400||cc|compressed||dm5imagefun|store|jpg|pix||for|function|b16539f06a792cbf40bc321c6e21b5c90c71ace6|g003|g002|d24ffbc545e8f9be20f236881f0c57600496117c|currentimageid|continue|return|14356281|length|if'.split('|'),0,{}))";


	String script_1_mangafox = "eval(function(p,a,c,k,e,d){e=function(c){return(c<a?\"\":e(parseInt(c/a)))+((c=c%a)>35?String.fromCharCode(c+29):c.toString(36))};if(!''.replace(/^/,String)){while(c--)d[e(c)]=k[c]||e(c);k=[function(e){return d[e]}];e=function(){return'\\\\w+'};c=1;};while(c--)if(k[c])p=p.replace(new RegExp('\\\\b'+e(c)+'\\\\b','g'),k[c]);return p;}('r e(){2 c=\"m://s.7.3/4/5/6/g.0/f\";2 1=[\"/l.9?8=n&b=a\",\"/k.9?8=j&b=a\"];t(2 i=0;i<1.o;i++){p(i==0){1[i]=\"//s.7.3/4/5/6/g.0/f\"+1[i];q}1[i]=c+1[i]}h 1}2 d;d=e();',30,30,'|pvalue|var|net|store|manga|24693|fanfox|token|jpg|1548000000|ttl|pix||dm5imagefun|compressed|067|return||3a5ded12ff12626baf6111280ea2e74cde2935b2|p001|p000|http|5db041f4cc6532140468d70006e351fae7f2459d|length|if|continue|function||for'.split('|'),0,{}))";


	@BeforeAll 
	static void init() throws ScrapperException, IOException {
		scrapper = new ScrapperCached("baseurl", ScrapperType.MANGAHERE);
		manga = scrapper.scrapManga("http://www.mangahere.cc/manga/parallel_paradise/");
	}

	@Test
	void toast() throws ScrapperException {
		JsEngine.parse("eval(function(p,a,c,k,e,d){e=function(c){return(c<a?\"\":e(parseInt(c/a)))+((c=c%a)>35?String.fromCharCode(c+29):c.toString(36))};if(!''.replace(/^/,String)){while(c--)d[e(c)]=k[c]||e(c);k=[function(e){return d[e]}];e=function(){return'\\\\w+'};c=1;};while(c--)if(k[c])p=p.replace(new RegExp('\\\\b'+e(c)+'\\\\b','g'),k[c]);return p;}('k e(){2 h=\"//a.7.b/f/3/6/4.0/c\";2 1=[\"/n.g?5=l&8=9\",\"/m.g?5=o&8=9\"];j(2 i=0;i<1.t;i++){u(i==0){1[i]=\"//a.7.b/f/3/6/4.0/c\"+1[i];q}1[i]=h+1[i]}r 1}2 d;d=e();p=s;',31,31,'|pvalue|var|manga|067|token|26739|mangahere|ttl|1548050400||cc|compressed||dm5imagefun|store|jpg|pix||for|function|b16539f06a792cbf40bc321c6e21b5c90c71ace6|g003|g002|d24ffbc545e8f9be20f236881f0c57600496117c|currentimageid|continue|return|14356281|length|if'.split('|'),0,{}))");
	}

	@Test
	void meta() throws ScrapperException, IOException {
		assumeTrue(MangaUrlsMeta.MANGAHERE.equals(scrapper.urlColumn()));

		StringBuilder sb = new StringBuilder();
		Junk.invokeGetters(manga, sb, e -> !e.getName().equals("getChapters"));

		System.out.println(sb);

		assertNotNull(manga.getThumb());
		assertTrue(manga.getThumb().contains("/cover.jpg?"));

		assertEquals("Parallel Paradise", manga.getTitle());
		assertEquals("OKAMOTO Lynn", manga.getAuthor());
		assertEquals("Action Adventure Comedy Fantasy Romance Drama Seinen Mature", String.join(" ", manga.getTags()));
		assertEquals("Ongoing", manga.getStatus());
		assertEquals("4.81", manga.getRank());

		String desc = "One day at school, Youta sees someone strange entering his school. Out of nowhere this being appears next to him and makes him fall from the 3rd floor. He lands in a strange world, where beings like dragons exist! Youta meets a young paladin, and discovers that he is the only man inhabiting that planet, and that its inhabitants are extremely ’weak’ to his presence. Could this be paradise?";

		assertEquals(desc.replaceAll("\\W+", ""), manga.getDescription().replaceAll("\\W+", ""));
	}

	@Test
	void chapters() throws IOException, ScrapperException {
		assumeTrue(MangaUrlsMeta.MANGAHERE.equals(scrapper.urlColumn()));

		ScrappedChapter[] chaps = manga.getChapters();

		Document doc = ((MangaHereManga)manga).getDoc();
		int count = Optional.of(doc.select("#tab-1.active span").get(0).text()).map(s -> s.substring(1, s.length() - 1)).map(s -> Integer.parseInt(s.trim())).orElse(0);

		assertEquals(count, chaps.length);

		for (ScrappedChapter s : chaps) 
			System.out.println(s);
	}

	@Test
	void pages() throws ScrapperException, IOException {
		assumeTrue(MangaUrlsMeta.MANGAHERE.equals(scrapper.urlColumn()));

		String[] urls = {
				"http://www.mangahere.cc/manga/parallel_paradise/c067/1.html",
				"http://www.mangahere.cc/manga/solo_leveling/c054/1.html"
		};

		StringBuilder sb = new StringBuilder();
		for (String s : urls) {
			ScrappedPage[] pages = scrapper.scrapPages(s);

			sb.append(s).append('\n');

			for (ScrappedPage p : pages) 
				sb.append(p).append('\n');

			sb.append('\n');
		}

		System.out.println(sb);
	}

	@Test
	void setImgUrlTest() throws ScrapperException, IOException {
		ScrappedPage[] ss = scrapper.scrapPages("http://www.mangahere.cc/manga/parallel_paradise/c067/1.html");

		StringBuilder sb = new StringBuilder();
		List<TempPage> pages = Arrays.stream(ss).map(TempPage::new).collect(Collectors.toList());
		int loop = 0;

		for (int i = 0; i < pages.size(); i++) {
			TempPage s = pages.get(i);
			sb.append(s).append("\n  ");
			
			if(s.getImgUrl() == null) {
				loop++;
				try {
					setImageUrl(scrapper, i, pages);
				} catch (Exception e) {
					sb.append(e);
					continue;
				}				
			}
			sb.append(s.getImgUrl()).append('\n');
		}
		System.out.println(sb.append("\n\nloop: ").append(loop).append(", page_count: ").append(pages.size()));
	}

	@Test
	void imgUrl() throws ScrapperException, IOException {
		String[] ss = {
				"http://www.mangahere.cc/manga/parallel_paradise/c067/1.html#ipg3",
				"http://www.mangahere.cc/manga/parallel_paradise/c067/1.html#ipg4",
		};

		StringBuilder sb = new StringBuilder();

		for (String s : ss) {
			String[] st = scrapper.getPageImageUrl(s);
			sb.append(s).append('\n');
			for (String p : st) 
				sb.append(p).append('\n');

			sb.append('\n');
		}

		System.out.println(sb);
	}

	private static class TempPage {
		final ScrappedPage page;
		String img_url;

		public TempPage(ScrappedPage page) {
			this.page = page;
			img_url = page.getImgUrl();
		}
		public String getChapterUrl() {
			return page.getChapterUrl();
		}
		public int getOrder() {
			return page.getOrder();
		}
		public String getPageUrl() {
			return page.getPageUrl();
		}
		public String getImgUrl() {
			return img_url;
		}
		public String toString() {
			return page.toString();
		}
		public int hashCode() {
			return page.hashCode();
		}
		public boolean equals(Object obj) {
			return page.equals(obj);
		}
		public void setImgUrl(String s) {
			img_url = s;
		}
	}

	private void setImageUrl(Scrapper scrapper, int index, List<TempPage> pages) throws ScrapperException, IOException {
		TempPage page = pages.get(index);

		String[] st = scrapper.getPageImageUrl(page.getPageUrl());
		if(Checker.isEmpty(st))
			return;
		
		int order = page.getOrder();
		for (String s : st) {
			int n = index++;
			int o = order++;
			
			if(n >= pages.size())
				return;
			
			if(pages.get(n).getOrder() == o)
				pages.get(n).setImgUrl(s);
		}
	}
}
