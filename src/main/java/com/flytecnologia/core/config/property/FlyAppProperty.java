package com.flytecnologia.core.config.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties("flyapp")
public class FlyAppProperty {
    private final Security security = new Security();
    private final App app = new App();

    @Getter
    @Setter
    public static class Security {
        private boolean enableHttps;
        private String allowOrigin;
        private String frontEndServerOrigin;
        private String accessDeniedErrorPage;

        public boolean isEnableHttps() {
            return enableHttps;
        }
    }

    @Getter
    @Setter
    public static class App {
        private boolean debug;
        private boolean validatePermissions;
        private String startSchemas;

        public boolean isDebug() {
            return debug;
        }

        public boolean isValidatePermissions() {
            return validatePermissions;
        }
    }
}
