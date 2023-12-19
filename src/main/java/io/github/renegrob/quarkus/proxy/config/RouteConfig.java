package io.github.renegrob.quarkus.proxy.config;

import io.smallrye.config.WithName;

import java.util.Optional;

public interface RouteConfig {

    String path();

    Optional<ProxyHandlerConfig> proxy();

    @WithName("static")
    Optional<StaticFileHandlerConfig> staticHandler();
}
