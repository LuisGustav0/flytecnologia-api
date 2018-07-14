package com.flytecnologia.core.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("flyapp")
public class FlyAppProperty {

    private final Security security = new Security();
    private final App app = new App();
    private final Email email = new Email();

    public Security getSecurity() {
        return security;
    }

    public App getApp() {
        return app;
    }

    public Email getEmail() {
        return email;
    }

    public static class Security {
        private boolean enableHttps;
        private String allowOrigin;
        private String frontEndServerOrigin;

        public boolean isEnableHttps() {
            return enableHttps;
        }

        public void setEnableHttps(boolean enableHttps) {
            this.enableHttps = enableHttps;
        }

        public String getAllowOrigin() {
            return allowOrigin;
        }

        public void setAllowOrigin(String allowOrigin) {
            this.allowOrigin = allowOrigin;
        }

        public String getFrontEndServerOrigin() {
            return frontEndServerOrigin;
        }

        public void setFrontEndServerOrigin(String frontEndServerOrigin) {
            this.frontEndServerOrigin = frontEndServerOrigin;
        }
    }

    public static class App {
        private boolean debug;
        private boolean validatePermissions;

        public boolean isDebug() {
            return debug;
        }

        public void setDebug(boolean debug) {
            this.debug = debug;
        }

        public boolean isValidatePermissions() {
            return validatePermissions;
        }

        public void setValidatePermissions(boolean validatePermissions) {
            this.validatePermissions = validatePermissions;
        }
    }

    public static class Email {
        private String emailEncaminhamento;

        public String getEmailEncaminhamento() {
            return emailEncaminhamento;
        }

        public void setEmailEncaminhamento(String emailEncaminhamento) {
            this.emailEncaminhamento = emailEncaminhamento;
        }
    }
}
