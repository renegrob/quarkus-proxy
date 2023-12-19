package io.github.renegrob.quarkus.proxy.config;

import io.smallrye.config.WithDefault;

import java.nio.file.Path;

public interface StaticFileHandlerConfig {
    Path directory();

    @WithDefault("index.html")
    String index();
}
