package logging;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.perf4j.StopWatch;
import org.perf4j.log4j.Log4JStopWatch;

public class ATestLogging {

	final static Log logger = LogFactory.getLog("console");
	
	public static void main(String[] args) {
		logger.debug("debug message");
	    logger.info("Hello this is an info message");
	    
	    StopWatch watch = new Log4JStopWatch(Logger.getLogger("org.perf4j.TimingLogger"));
	    
	    watch.start("first");
	    for (int i = 0; i < 100000; i++) {
	    	System.out.println("bla");
	    }
	    watch.stop("first");
	    watch.start("second");
	    for (int i = 0; i < 100000; i++) {
	    	System.out.println("bla");
	    }
	    watch.stop("second");
	    
	    StopWatch watch1 = new Log4JStopWatch(Logger.getLogger("org.perf4j.TimingLogger"));
	    watch1.start("first");
	    for (int i = 0; i < 100000; i++) {
	    	//System.out.println("bla");
	    }
	    watch1.stop("first");
	    
	    logger.debug("done");
	    logger.info("over");
	}
}
