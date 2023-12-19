package io.github.renegrob.quarkus.proxy.model;

import io.quarkus.qute.TemplateData;

import java.net.URI;

@TemplateData
public record ErrorInfo(int statusCode, String reason, URI uri) {
}
