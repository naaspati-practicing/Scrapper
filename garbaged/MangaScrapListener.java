package sam.manga.scrapper.scrappers;

public interface MangaScrapListener {
    void badChapterNumber(String number, String volume, String url, RuntimeException e);
}
