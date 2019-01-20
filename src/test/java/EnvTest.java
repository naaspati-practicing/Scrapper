import java.io.UnsupportedEncodingException;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

import sam.logging.MyLoggerFactory;
import sam.nopkg.Junk;

public class EnvTest {
	private static final Logger LOGGER = MyLoggerFactory.logger(EnvTest.class);

	@Test
	void main_test() throws UnsupportedEncodingException {
		LOGGER.config("\n"+Junk.systemInfo()+"\n");
	}

}
