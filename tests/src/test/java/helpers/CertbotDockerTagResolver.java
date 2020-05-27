package helpers;

import io.homecentr.testcontainers.images.EnvironmentImageTagResolver;

public class CertbotDockerTagResolver extends EnvironmentImageTagResolver {
    public CertbotDockerTagResolver() {
        super("homecentr/certbot:local");
    }
}
