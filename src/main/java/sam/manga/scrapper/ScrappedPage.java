package sam.manga.scrapper;

public class ScrappedPage {
	protected final  String chapterUrl;
	protected final  int order;
	protected final  String pageUrl;
	protected final String imgUrl;
	
	public ScrappedPage(String chapterUrl, int order, String pageUrl, String imgUrl) {
		this.chapterUrl = chapterUrl;
		this.order = order;
		this.pageUrl = pageUrl;
		this.imgUrl = imgUrl;
	}
	public String getChapterUrl() {
		return chapterUrl;
	}
	public int getOrder() {
		return order;
	}
	public String getPageUrl() {
		return pageUrl;
	}
	public String getImgUrl() {
		return imgUrl;
	}
	
	@Override
	public String toString() {
		return "ScrappedPage [chapterUrl='" + chapterUrl + "', order=" + order + ", pageUrl='" + (pageUrl == null ? "" : pageUrl) + "', imgUrl='"
				+ (imgUrl == null ? "" : imgUrl) + "']";
	}
}
