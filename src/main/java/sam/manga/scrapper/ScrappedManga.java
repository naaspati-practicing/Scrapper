package sam.manga.scrapper;

import java.io.IOException;
import java.util.List;

public interface ScrappedManga {
	String getRank();
	String getTitle();
	String getAuthor();
	List<String> getTags();
	String getThumb();
	String getStatus();
	String getDescription();
	ScrappedChapter[] getChapters() throws IOException, ScrapperException;
}
