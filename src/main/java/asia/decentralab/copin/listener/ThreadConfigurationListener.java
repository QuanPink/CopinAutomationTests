package asia.decentralab.copin.listener;

import asia.decentralab.copin.config.EnvironmentConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IAlterSuiteListener;
import org.testng.xml.XmlSuite;

import java.util.List;

public class ThreadConfigurationListener implements IAlterSuiteListener {
    private static final Logger log = LoggerFactory.getLogger(ThreadConfigurationListener.class);

    @Override
    public void alter(List<XmlSuite> suites) {
        if (suites == null || suites.isEmpty()) {
            log.warn("No TestNG suites found to configure");
            return;
        }

        // Get configuration from EnvironmentConfig
        EnvironmentConfig config = EnvironmentConfig.getInstance();
        int threadCount = config.getThreadCount();
        String parallelMode = config.getParallelMode();

        log.info("Applying thread configuration from .env: threadCount={}, parallelMode={}",
                threadCount, parallelMode);

        // Use the configuration across all suites
        for (XmlSuite suite : suites) {
            // Set thread count
            suite.setThreadCount(threadCount);

            // Set parallel mode
            try {
                XmlSuite.ParallelMode mode = XmlSuite.ParallelMode.getValidParallel(parallelMode);
                suite.setParallel(mode);
                log.info("Applied thread configuration to suite '{}': threadCount={}, parallelMode={}",
                        suite.getName(), threadCount, mode);
            } catch (Exception e) {
                log.warn("Invalid parallel mode: {}. Using 'methods' as default.", parallelMode);
                suite.setParallel(XmlSuite.ParallelMode.METHODS);
            }
        }
    }
}
