package tb;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Run {
	private static final Logger log = LoggerFactory.getLogger(Run.class);

	public void start() throws Exception {
		log.info("SERVER STARTED");
	}

	public void stop() throws IOException {
		log.info("SERVER SHUT DOWN\r\n");
	}
}
