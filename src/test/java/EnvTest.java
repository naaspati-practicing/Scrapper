import java.io.UnsupportedEncodingException;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sam.nopkg.Junk;

public class EnvTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(EnvTest.class);


	@Test
	void main_test() throws UnsupportedEncodingException {
		LOGGER.info("\n{}\n", Junk.systemInfo());
	}

}
