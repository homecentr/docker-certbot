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

import java.io.IOException;
import java.time.Duration;

import static io.homecentr.testcontainers.WaitLoop.waitFor;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CertbotContainerShould {
    private static final Logger logger = LoggerFactory.getLogger(CertbotContainerShould.class);

    private static GenericContainerEx _certbotContainer;
    private static TestConfiguration _testConfig;

    @BeforeClass
    public static void before() throws Exception {
        _testConfig = TestConfiguration.create();
        _testConfig.createCredentialsSecretFile();

        _certbotContainer = new GenericContainerEx<>(new CertbotDockerTagResolver())
                .withEnv("PUID", "9001")
                .withEnv("PGID", "9002")
                .withEnv("CRON_SCHEDULE", "* * * * *")
                .withEnv("CERTBOT_ARGS", _testConfig.getCertbotArgs())
                .withFileSystemBind(TestConfiguration.cloudflareCredentialsHostPath, TestConfiguration.cloudflareCredentialsContainerPath)
                .withImagePullPolicy(PullPolicyEx.never())
                .waitingFor(WaitEx.forS6OverlayStart());

        _certbotContainer.start();
        _certbotContainer.followOutput(new Slf4jLogConsumer(logger));

        waitFor(Duration.ofSeconds(80), () -> _certbotContainer.getLogsAnalyzer().contains("Execution finished"));
    }

    @AfterClass
    public static void after() {
        _certbotContainer.close();
    }

    @Test
    public void createCertificateFullChainFile() throws IOException, InterruptedException {
        assertTrue(fileExists("/certs/fullchain.pem"));

        assertEquals((Integer)9001, _certbotContainer.getFileOwnerUid("/certs/fullchain.pem"));
        assertEquals((Integer)9002, _certbotContainer.getFileOwningGid("/certs/fullchain.pem"));
    }

    @Test
    public void createCertificateChainFile() throws IOException, InterruptedException {
        assertTrue(fileExists("/certs/chain.pem"));

        assertEquals((Integer)9001, _certbotContainer.getFileOwnerUid("/certs/chain.pem"));
        assertEquals((Integer)9002, _certbotContainer.getFileOwningGid("/certs/chain.pem"));
    }

    @Test
    public void createPrivateKeyFile() throws IOException, InterruptedException {
        assertTrue(fileExists("/certs/privkey.pem"));

        assertEquals((Integer)9001, _certbotContainer.getFileOwnerUid("/certs/privkey.pem"));
        assertEquals((Integer)9002, _certbotContainer.getFileOwningGid("/certs/privkey.pem"));
    }

    @Test
    public void createPublicKeyFile() throws IOException, InterruptedException {
        assertTrue(fileExists("/certs/cert.pem"));

        assertEquals((Integer)9001, _certbotContainer.getFileOwnerUid("/certs/cert.pem"));
        assertEquals((Integer)9002, _certbotContainer.getFileOwningGid("/certs/cert.pem"));
    }

    private boolean fileExists(String fileNamePattern) throws IOException, InterruptedException {
        return _certbotContainer.execInContainer("ls", fileNamePattern).getExitCode() == 0;
    }
}
