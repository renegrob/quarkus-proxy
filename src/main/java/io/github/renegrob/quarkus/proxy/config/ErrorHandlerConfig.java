package io.github.renegrob.quarkus.proxy.config;

import java.nio.file.Path;

public interface ErrorHandlerConfig {

   String statusCode();
   String template();
}
