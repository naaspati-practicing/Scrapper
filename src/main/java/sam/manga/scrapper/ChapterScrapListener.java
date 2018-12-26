package sam.manga.scrapper;

public interface ChapterScrapListener {
	void onChapterSuccess(double number, String volume, String title, String url);
	void onChapterFailed(String msg, Throwable e, String number, String volume, String title, String url);
}
