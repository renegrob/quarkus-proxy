package io.github.renegrob.quarkus.proxy.config;

import io.smallrye.config.WithName;

import java.nio.file.Path;

public interface ResourceFileHandlerConfig extends RouteConfig {

    @WithName("static")
    ResourceConfig staticFiles();

    interface ResourceConfig {
        Path resourcePath();
    }
}
