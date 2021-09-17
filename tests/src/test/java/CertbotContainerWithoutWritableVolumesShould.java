import helpers.CertbotDockerTagResolver;
import io.homecentr.testcontainers.containers.GenericContainerEx;
import io.homecentr.testcontainers.containers.wait.strategy.WaitEx;
import io.homecentr.testcontainers.images.PullPolicyEx;
import org.junit.Test;
import org.testcontainers.containers.ContainerLaunchException;

import java.time.Duration;

import static io.homecentr.testcontainers.WaitLoop.waitFor;

public class CertbotContainerWithoutWritableVolumesShould {
    @Test
    public void failWhenCertsDirNotWritable() throws Exception {
        GenericContainerEx container = new GenericContainerEx<>(new CertbotDockerTagResolver())
                .withEnv("CRON_SCHEDULE", "* * * * *")
                .withEnv("CERTBOT_ARGS", "")
                .withEnv("PUID", "9001")
                .withEnv("PGID", "9002")
                .withTempDirectoryBind("/certs", 0)
                .withTempDirectoryBind("/logs", 9002)
                .withTempDirectoryBind("/state", 9002)
                .withImagePullPolicy(PullPolicyEx.never())
                .waitingFor(WaitEx.forS6OverlayStart());

        assertExits(container);
    }

    @Test
    public void failWhenLogsDirNotWritable() throws Exception {
        GenericContainerEx container = new GenericContainerEx<>(new CertbotDockerTagResolver())
                .withEnv("CRON_SCHEDULE", "* * * * *")
                .withEnv("CERTBOT_ARGS", "")
                .withEnv("PUID", "9001")
                .withEnv("PGID", "9002")
                .withTempDirectoryBind("/certs", 9002)
                .withTempDirectoryBind("/logs", 0)
                .withTempDirectoryBind("/state", 9002)
                .withImagePullPolicy(PullPolicyEx.never())
                .waitingFor(WaitEx.forS6OverlayStart());

        assertExits(container);
    }

    @Test
    public void failWhenStateDirNotWritable() throws Exception {
        GenericContainerEx container = new GenericContainerEx<>(new CertbotDockerTagResolver())
                .withEnv("CRON_SCHEDULE", "* * * * *")
                .withEnv("CERTBOT_ARGS", "")
                .withEnv("PUID", "9001")
                .withEnv("PGID", "9002")
                .withTempDirectoryBind("/certs", 9002)
                .withTempDirectoryBind("/logs", 9002)
                .withTempDirectoryBind("/state", 0)
                .withImagePullPolicy(PullPolicyEx.never())
                .waitingFor(WaitEx.forS6OverlayStart());

        assertExits(container);
    }

    private void assertExits(GenericContainerEx container) throws Exception {
        // start

        try {
            container.start();
        }
        catch (ContainerLaunchException ex) {
            // Expected
        }

        waitFor(Duration.ofSeconds(10), () -> !container.isRunning());
        waitFor(Duration.ofSeconds(10), () -> !container.getLogsAnalyzer().contains(".*not writable.*"));
    }
}
