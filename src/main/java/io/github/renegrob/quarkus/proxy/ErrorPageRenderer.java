package io.github.renegrob.quarkus.proxy;

import io.github.renegrob.quarkus.proxy.model.ErrorInfo;
import io.quarkus.qute.Engine;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class ErrorPageRenderer {

    @Inject
    Engine engine;

    public String renderError(String template, ErrorInfo errorInfo) {
        return engine.getTemplate(template).data(errorInfo).render();
    }
}
