package com.flytecnologia.core.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("flyapp")
public class FlyAppProperty {

    private final Security security = new Security();
    private final App app = new App();

    public Security getSecurity() {
        return security;
    }

    public App getApp() {
        return app;
    }

    public static class Security {
        private boolean enableHttps;

        public boolean isEnableHttps() {
            return enableHttps;
        }

        public void setEnableHttps(boolean enableHttps) {
            this.enableHttps = enableHttps;
        }
    }

    public static class App {
        private boolean debug;

        public boolean isDebug() {
            return debug;
        }

        public void setDebug(boolean debug) {
            this.debug = debug;
        }
    }
}
