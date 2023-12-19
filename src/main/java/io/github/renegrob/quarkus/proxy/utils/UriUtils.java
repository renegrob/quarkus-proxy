package io.github.renegrob.quarkus.proxy.utils;

import java.net.MalformedURLException;
import java.net.URI;

public final class UriUtils {
    private UriUtils() {
    }

    public static boolean isHttps(URI uri) {
        return "https".equals(uri.getScheme());
    }

    public static int portOfUri(URI uri) throws MalformedURLException {
        if (uri.getPort() > 0) {
            return uri.getPort();
        }
        return uri.toURL().getDefaultPort();
    }
}