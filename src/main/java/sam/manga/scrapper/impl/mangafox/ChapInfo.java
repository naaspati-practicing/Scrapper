package sam.manga.scrapper.impl.mangafox;

import java.net.MalformedURLException;
import java.net.URL;

class ChapInfo {
	final int imagecount;
	final String chapterid;
	final String key;
	final String[] imgUrls;
	final String protocol;
	
	public final String chapterfun_ashx;
	
	static final String END = "/1.html#ipg";
	
	public ChapInfo(int imagecount, String chapterid, String key, String chapterUrl, String[] imgUrls) throws MalformedURLException {
		this.imagecount = imagecount;
		this.chapterid = chapterid;
		this.key = key;
		this.protocol = new URL(chapterUrl).getProtocol().concat(":");
		this.imgUrls = imgUrls;
		this.chapterfun_ashx = chapurl(chapterUrl);
		
		if(imgUrls != null && imgUrls.length != imagecount)
			throw new IllegalStateException(String.format("imgUrls.length(%s) != imagecount(%s)", imgUrls.length, imagecount));
	}

	String chapurl(String c) {
		if(!c.endsWith("/1.html"))
			throw new IllegalArgumentException("url must end with /1.html");
		// with key
		// c = c.substring(0, c.lastIndexOf('/')).concat("/chapterfun.ashx?cid="+chapterid+(key == null || key.trim().isEmpty() ? "" : "&key="+key)+"&page=");
		c = c.substring(0, c.lastIndexOf('/')).concat("/chapterfun.ashx?cid="+chapterid+"&key=&page=");
		return c;
	}
	
	public String appendProtocol(String s) {
		return s.startsWith("http") ? s : protocol.concat(s);
	}

	@Override
	public String toString() {
		return "ChapInfo [imagecount=" + imagecount + ", chapterid=" + chapterid + ", key=" + key + ", imgUrls="
				+ (imgUrls == null ? "null":"String["+imgUrls.length+"]") + ", protocol=" + protocol + ", chapterfun_ashx=" + chapterfun_ashx + "]";
	}
	
	
}
