import org.testcontainers.shaded.com.google.common.io.Files;
import org.testcontainers.shaded.org.apache.commons.lang.SystemUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.*;
import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

public class TestConfiguration {
    private final String _randomPrefix;

    private String _stateDirPath;
    private String _logsDirPath;
    private String _certsDirPath;
    private String _cloudflareCredentialFilePath;

    public static final String cloudflareCredentialsContainerPath = "/config/cloudflare.init.tmp";

    public static TestConfiguration create(int gid) throws IOException {
        String prefix = UUID.randomUUID().toString();

        TestConfiguration result = new TestConfiguration(prefix);
        result.createCredentialsSecretFile();
        result.createVolumeDirectories(gid);

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

    public String getCertsDirPath() {
        return _certsDirPath;
    }

    public String getStateDirPath() {
        return _stateDirPath;
    }

    public String getLogsDirPath() {
        return _logsDirPath;
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

    private String createDirectory(int gid) throws IOException {
        File dir = Files.createTempDir();

        if(SystemUtils.IS_OS_LINUX){
            // UserPrincipalLookupService groupLookupSvc = FileSystems.getDefault().getUserPrincipalLookupService();

            //GroupPrincipal group = groupLookupSvc.lookupPrincipalByGroupName("grp" + gid);

            //System.out.println("Dir group: " + group.getName());

            // PosixFileAttributeView attributeView = java.nio.file.Files.getFileAttributeView(dir.toPath(), PosixFileAttributeView.class, LinkOption.NOFOLLOW_LINKS);

            // attributeView.setPermissions(PosixFilePermissions.fromString("rwxrwxrwx"));
            //attributeView.setGroup(group);

            //java.nio.file.Files.setAttribute(dir.toPath(), "unix:gid", gid);
            // java.nio.file.Files.setPosixFilePermissions(dir.toPath(), PosixFilePermissions.fromString("rwxrwxrwx"));



            FileAttribute<Set<PosixFilePermission>> att = PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rwxrwx---"));

            // java.nio.file.Files.createDirectory(dir.toPath(), att);
            java.nio.file.Files.setAttribute(dir.toPath(), "unix:gid", gid);
            java.nio.file.Files.setPosixFilePermissions(dir.toPath(), PosixFilePermissions.fromString("rwxrwx---"));
        }

        return dir.getAbsolutePath();
    }

    private void createVolumeDirectories(int gid) throws IOException {
       _stateDirPath = createDirectory(gid);
       _logsDirPath = createDirectory(gid);
       _certsDirPath = createDirectory(gid);
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
