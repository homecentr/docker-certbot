import helpers.CertbotDockerTagResolver;
import io.homecentr.testcontainers.containers.GenericContainerEx;
import io.homecentr.testcontainers.containers.wait.strategy.WaitEx;
import io.homecentr.testcontainers.images.PullPolicyEx;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.ContainerLaunchException;

import java.io.IOException;
import java.time.Duration;

import static io.homecentr.testcontainers.WaitLoop.waitFor;

public class CertbotContainerWithoutEmailShould {
    private static final Logger logger = LoggerFactory.getLogger(CertbotContainerWithoutEmailShould.class);

    private static GenericContainerEx _certbotContainer;
    private static TestConfiguration _testConfig;

    @BeforeClass
    public static void before() throws IOException {
        _testConfig = TestConfiguration.create();

        _certbotContainer = new GenericContainerEx<>(new CertbotDockerTagResolver())
                .withEnv("CRON_SCHEDULE", "* * * * *")
                .withEnv("CERTBOT_ARGS", "")
                .withEnv("PUID", "0")
                .withEnv("PGID", "0")
                .withFileSystemBind(_testConfig.getCloudflareCredentialFilePath(), TestConfiguration.cloudflareCredentialsContainerPath)
                .withTempDirectoryBind("/certs", 9002)
                .withTempDirectoryBind("/logs", 9002)
                .withTempDirectoryBind("/state", 9002)
                .withImagePullPolicy(PullPolicyEx.never())
                .waitingFor(WaitEx.forS6OverlayStart());

        try {
            _certbotContainer.start();
        }
        catch (ContainerLaunchException ex) {
            // Expected
        }
    }

    @AfterClass
    public static void after() {
        _certbotContainer.close();
    }

    @Test
    public void printWarning() throws Exception {
        waitFor(Duration.ofSeconds(20), () -> _certbotContainer.getLogsAnalyzer().contains("The CERTBOT_ARGS variable must contain '--email'"));
    }

    @Test
    public void exitContainer() throws Exception {
        waitFor(Duration.ofSeconds(20), () -> !_certbotContainer.isRunning());
    }
}
