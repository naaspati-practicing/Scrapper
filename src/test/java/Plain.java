import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.Test;

import okhttp3.OkHttpClient;
import sam.manga.scrapper.ScrapperException;
import sam.manga.scrapper.impl.mangafox.MangaFoxChapter;
import sam.manga.scrapper.impl.mangahere.ChapInfo;
import sam.manga.scrapper.jsoup.DefaultJsoupFactory;
import sam.manga.scrapper.jsoup.JsoupFactory;
import sam.nopkg.Junk;

public class Plain {
	@Test
	void jsparse() throws ScrapperException, IOException {
		/*
		 * String script = "eval(function(p,a,c,k,e,d){e=function(c){return(c<a?\"\":e(parseInt(c/a)))+((c=c%a)>35?String.fromCharCode(c+29):c.toString(36))};if(!''.replace(/^/,String)){while(c--)d[e(c)]=k[c]||e(c);k=[function(e){return d[e]}];e=function(){return'\\\\w+'};c=1;};while(c--)if(k[c])p=p.replace(new RegExp('\\\\b'+e(c)+'\\\\b','g'),k[c]);return p;}('2 0=\\'\\'+\\'c\\'+\\'3\\'+\\'7\\'+\\'a\\'+\\'9\\'+\\'e\\'+\\'c\\'+\\'3\\'+\\'e\\'+\\'4\\'+\\'5\\'+\\'7\\'+\\'1\\'+\\'4\\'+\\'b\\'+\\'6\\';$(\"#8\").d(0);',15,15,'guidkey||var||||||dm5_key|||||val|'.split('|'),0,{}))";
		Bindings bs =  new JsEngine().eval(script);

		System.out.println(Junk.toString(bs));
		 */
		JsoupFactory factory = new DefaultJsoupFactory();
		String url = "http://www.mangahere.cc/manga/gunota_ga_mahou_sekai_ni_tensei_shitara_gendai_heiki_de_guntai_harem_o_tsukucchaimashita/c025/1.html";
		ChapInfo info = MangaFoxChapter.chapInfo(url, factory, null, true);

		String purl = info.chapterfun_ashx.concat("1");
		System.out.println(purl);

		org.jsoup.Connection.Response r = factory.connection(purl)
				.header("Host", "www.mangahere.cc")
				.header("Pragma","no-cache")
				.header("Cookie", info.cookie)
				.header("X-Requested-With", "XMLHttpRequest")
				.header("Accept-Encoding","gzip, deflate")
				.method(Method.GET)
				.followRedirects(true)
				.execute();
		
		System.out.println(r.statusMessage());
		System.out.println(r.statusCode());
		System.out.println(Junk.toString(r.headers()));
		System.out.println();
		System.out.println(Jsoup.parse(new URL(purl), 60000));
		
	}

	public OkHttpClient client() {
		return new OkHttpClient.Builder()
				.connectTimeout(6000, TimeUnit.MILLISECONDS)
				.followRedirects(true)
				.followSslRedirects(true)
				.retryOnConnectionFailure(true)
				.build();
	}


}
