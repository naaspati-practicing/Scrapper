package sam.manga.scrapper;

public interface PageScrapListener {
	void onPageSuccess(String chapterUrl, int order, String pageUrl, String imgUrl);
}
