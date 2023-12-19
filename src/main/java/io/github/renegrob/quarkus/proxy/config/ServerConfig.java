package io.github.renegrob.quarkus.proxy.config;

import io.smallrye.config.ConfigMapping;

import java.nio.charset.Charset;
import java.util.List;

@ConfigMapping(prefix = "server")
public interface ServerConfig {

    Charset defaultCharset();
    List<RouteConfig> paths();

    List<ErrorHandlerConfig> errors();
}
