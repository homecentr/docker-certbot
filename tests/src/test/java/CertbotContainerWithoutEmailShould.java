import helpers.CertbotDockerTagResolver;
import io.homecentr.testcontainers.containers.GenericContainerEx;
import io.homecentr.testcontainers.containers.wait.strategy.WaitEx;
import io.homecentr.testcontainers.images.PullPolicyEx;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.output.Slf4jLogConsumer;

import java.time.Duration;

import static io.homecentr.testcontainers.WaitLoop.waitFor;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CertbotContainerWithoutEmailShould {
    private static final Logger logger = LoggerFactory.getLogger(CertbotContainerWithoutEmailShould.class);

    private static GenericContainerEx _certbotContainer;

    @BeforeClass
    public static void before() {
        _certbotContainer = new GenericContainerEx<>(new CertbotDockerTagResolver())
                .withEnv("CRON_SCHEDULE", "* * * * *")
                .withEnv("CERTBOT_ARGS", "")
                .withFileSystemBind(TestConfiguration.cloudflareCredentialsHostPath, TestConfiguration.cloudflareCredentialsContainerPath)
                .withImagePullPolicy(PullPolicyEx.never())
                .waitingFor(WaitEx.forS6OverlayStart());

        _certbotContainer.start();
        _certbotContainer.followOutput(new Slf4jLogConsumer(logger));
    }

    @AfterClass
    public static void after() {
        _certbotContainer.close();
    }

    @Test
    public void printWarning() {
        assertTrue(_certbotContainer.getLogsAnalyzer().contains("The CERTBOT_ARGS variable must contain '--email'"));
    }

    @Test
    public void exitContainer() throws Exception {
        waitFor(Duration.ofSeconds(20), () -> _certbotContainer.isRunning());
    }
}
