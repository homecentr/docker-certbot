import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

public class TestConfiguration {
    private final String _randomPrefix;

    public static final String cloudflareCredentialsContainerPath = "/config/cloudflare.init.tmp";
    public static final String cloudflareCredentialsHostPath = Paths.get(System.getProperty("user.dir"), "..", "cloudflare.init.tmp").normalize().toString();

    public static TestConfiguration create() {
        String prefix = UUID.randomUUID().toString();

        return new TestConfiguration(prefix);
    }


    private TestConfiguration(String randomPrefix) {
        _randomPrefix = randomPrefix;
    }

    public String getDomain() {
        return String.format("%s.%s", _randomPrefix, getRootDomain());
    }

    public String getCertbotArgs() {
        return String.format("--email %s --staging --dns-cloudflare --dns-cloudflare-credentials %s -d %s",
                getEmail(),
                cloudflareCredentialsContainerPath,
                getDomain());
    }

    public void createCredentialsSecretFile() throws IOException {
        File secretFile = new File(cloudflareCredentialsHostPath);

        if(secretFile.exists()) {
            secretFile.delete();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(secretFile))) {
            writer.write("dns_cloudflare_api_token = " + getCloudflareToken());
            writer.flush();
        }
    }

    private String getCloudflareToken() {
        return System.getProperty("cloudflare_token");
    }

    private String getEmail() {
        return System.getProperty("acme_email");
    }

    private String getRootDomain() {
        return System.getProperty("root_domain");
    }
}
