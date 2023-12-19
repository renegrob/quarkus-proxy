package io.github.renegrob.quarkus.proxy;

import io.github.renegrob.quarkus.proxy.config.*;
import io.github.renegrob.quarkus.proxy.model.ErrorInfo;
import io.github.renegrob.quarkus.proxy.utils.IntegerRangeParser;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.FileSystemAccess;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.httpproxy.*;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Singleton;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

import static io.github.renegrob.quarkus.proxy.utils.UriUtils.isHttps;
import static io.github.renegrob.quarkus.proxy.utils.UriUtils.portOfUri;
import static io.vertx.core.http.impl.HttpUtils.normalizePath;

@Singleton
public class Server {

    private Vertx vertx;

    private ServerConfig serverConfig;

    private ErrorPageRenderer errorPageRenderer;

    private static final Logger LOG = LoggerFactory.getLogger(Server.class);

    public Server(Vertx vertx, ServerConfig serverConfig, ErrorPageRenderer errorPageRenderer) {
        this.vertx = vertx;
        this.serverConfig = serverConfig;
        this.errorPageRenderer = errorPageRenderer;
    }

    public void init(@Observes Router router) throws IOException {
        for (RouteConfig routeConfig : serverConfig.paths()) {
            routeConfig.staticHandler().ifPresent(config -> configure(router, routeConfig, config));
            routeConfig.proxy().ifPresent(config -> configure(router, routeConfig, config));
        }
        for (ErrorHandlerConfig  config : serverConfig.errors()) {
            configure(router, config);
        }
    }

    private void configure(Router router, ErrorHandlerConfig config) {
//        router.route("/*").handler(TimeoutHandler.create(10000));
        router.route("/*").failureHandler(routingContext -> {
            LOG.error("{} {}: {}", routingContext.response().getStatusCode(), routingContext.response().getStatusMessage(), routingContext.request().uri());
        });
        for (int statusCode : IntegerRangeParser.INSTANCE.parseRange(config.statusCode())) {
            router.errorHandler(statusCode, event -> {
                String reason = HttpResponseStatus.valueOf(statusCode).reasonPhrase();
                LOG.error("{} {}: {}", statusCode, reason, event.request().uri());
                ErrorInfo errorInfo = new ErrorInfo(statusCode, reason, URI.create(event.request().uri()));
                event.response().setStatusCode(statusCode);
                event.response().end(errorPageRenderer.renderError(config.template(), errorInfo));
            });
        }
    }

    private void configure(Router router, RouteConfig routeConfig, ProxyHandlerConfig proxyConfig) {
        // https://vertx.io/docs/4.2.0/vertx-http-proxy/java/#_proxy_server_with_httpproxy
        URI remoteUri = proxyConfig.remoteUri();
        final HttpClientOptions httpClientOptions = new HttpClientOptions();
        httpClientOptions.setSsl(isHttps(remoteUri));
        httpClientOptions.setForceSni(isHttps(remoteUri));
        HttpClient proxyClient = vertx.createHttpClient(httpClientOptions);
        HttpProxy proxy = HttpProxy.reverseProxy(proxyClient);
        try {
            proxy.origin(portOfUri(remoteUri), remoteUri.getHost());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        proxy.addInterceptor(rewritePath(routeConfig.path(), remoteUri));
        router.route(normalizePath (routeConfig.path()) + "*").handler(rc -> proxy.handle(rc.request()));
    }

    private void configure(Router router, RouteConfig routeConfig, StaticFileHandlerConfig filesConfig) {
        Path path = filesConfig.directory();
        if (Files.notExists(path)) {
            throw new RuntimeException(new FileNotFoundException(path.toAbsolutePath().toString()));
        }
        StaticHandler staticHandler = createStaticHandler(path);
        staticHandler.setIncludeHidden(false);
        staticHandler.setFilesReadOnly(true);
        staticHandler.setIndexPage(filesConfig.index());
        staticHandler.setDefaultContentEncoding(serverConfig.defaultCharset().toString());
        router.route(normalizePath (routeConfig.path()) + "*").handler(staticHandler);
    }

    private static StaticHandler createStaticHandler(Path path) {
        StaticHandler staticHandler;
        if (path.isAbsolute()) {
            String pathText;
            try {
                pathText = path.toRealPath().toString();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            staticHandler = StaticHandler.create(FileSystemAccess.ROOT, pathText);
        } else {
            staticHandler = StaticHandler.create(FileSystemAccess.RELATIVE, path.toString());
        }
        return staticHandler;
    }

    private ProxyInterceptor rewritePath(String path, URI target) {

        final String removePrefix = normalizePath(path);
        return new ProxyInterceptor() {
            @Override
            public Future<ProxyResponse> handleProxyRequest(ProxyContext context) {
                final ProxyRequest request = context.request();
                String postfix = StringUtils.removeStart(normalizePath(request.getURI()), removePrefix);
                request.setURI(normalizePath(target.getPath()) + postfix);
                request.headers().set("Host", target.getHost());
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Forwarding request {} {} to {}", request.getMethod(), request.absoluteURI(), target + postfix);
                }
                return ProxyInterceptor.super.handleProxyRequest(context);
            }
        };
    }
}
