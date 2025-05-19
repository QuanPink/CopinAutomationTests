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

        // Lấy cấu hình từ EnvironmentConfig
        EnvironmentConfig config = EnvironmentConfig.getInstance();
        int threadCount = config.getThreadCount();
        String parallelMode = config.getParallelMode();

        log.info("Applying thread configuration from .env: threadCount={}, parallelMode={}",
                threadCount, parallelMode);

        // Áp dụng cấu hình cho tất cả các suites
        for (XmlSuite suite : suites) {
            // Áp dụng số lượng thread
            suite.setThreadCount(threadCount);

            // Áp dụng parallel mode
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
