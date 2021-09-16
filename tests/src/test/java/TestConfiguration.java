import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

public class TestConfiguration {
    private final String _randomPrefix;

    private String _cloudflareCredentialFilePath;

    public static final String cloudflareCredentialsContainerPath = "/config/cloudflare.init.tmp";

    public static TestConfiguration create() throws IOException {
        String prefix = UUID.randomUUID().toString();

        TestConfiguration result = new TestConfiguration(prefix);
        result.createCredentialsSecretFile();

        return result;
    }

    private TestConfiguration(String randomPrefix) {
        _randomPrefix = randomPrefix;
    }

    public String getDomain() {
        return String.format("%s.%s", _randomPrefix, getRootDomain());
    }

    public String getCertbotArgs() {
        return String.format("--email %s --staging --dns-cloudflare --dns-cloudflare-credentials %s -d %s -v",
                getEmail(),
                cloudflareCredentialsContainerPath,
                getDomain());
    }

    public String getCloudflareCredentialFilePath() {
        return _cloudflareCredentialFilePath;
    }

    private void createCredentialsSecretFile() throws IOException {
        File credentialsFile = File.createTempFile("cloudflare", "creds");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(credentialsFile))) {
            writer.write("dns_cloudflare_api_token = " + getCloudflareToken());
            writer.flush();
        }

        _cloudflareCredentialFilePath = credentialsFile.getAbsolutePath();
    }

    private String getCloudflareToken() {
        return System.getenv("CLOUDFLARE_TOKEN");
    }

    private String getEmail() {
        return System.getenv("ACME_EMAIL");
    }

    private String getRootDomain() {
        return System.getenv("ROOT_DOMAIN");
    }
}
