package org.honton.chas.url.extension.urlhandler;

import java.net.URL;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * A URLStreamHandlerFactory that allows registration of multiple schemes and service provider pattern
 */
public class UrlStreamHandlerRegistry implements URLStreamHandlerFactory {

  private static UrlStreamHandlerRegistry SINGLETON;

  /**
   * Get the singleton instance of the registry/factory.  This will also invoke
   * URL.setURLStreamHandlerFactory and {@link #providers()} on first call to this method.
   *
   * @return The register UrlStreamHandlerRegistry
   */
  public static UrlStreamHandlerRegistry register() {
    synchronized (UrlStreamHandlerRegistry.class) {
      if(SINGLETON == null) {
        SINGLETON = new UrlStreamHandlerRegistry();
        URL.setURLStreamHandlerFactory(SINGLETON);
        SINGLETON.providers();
      }
      return SINGLETON;
    }
  }

  private final List<URLStreamHandlerFactory> factories = new ArrayList<>();

  /**
   * Register all URLStreamHandlerFactory found in META-INF/services/java.net.URLStreamHandlerFactory files.
   */
  public void providers() {
    // register providers found in jars
    for (URLStreamHandlerFactory factory : ServiceLoader.load(URLStreamHandlerFactory.class)) {
      factory(factory);
    }
  }

  /**
   * Register a URLStreamHandlerFactory
   *
   * @param urlStreamHandlerFactory A factory instance to register.  The factory must produce thread-safe URLStreamHandlers.
   */
  public UrlStreamHandlerRegistry factory(URLStreamHandlerFactory urlStreamHandlerFactory) {
    factories.add(urlStreamHandlerFactory);
    return this;
  }

  /**
   * Get or Create a URLStreamHandler for a scheme.  First check for a  URLStreamHandler
   * registered with the scheme, then query each registered for the schema.
   *
   * @param scheme The scheme for the URLStreamHandler
   * @return A thread-safe URLStreamHandler for the scheme or null
   */
  @Override
  public URLStreamHandler createURLStreamHandler(String scheme) {
    for (URLStreamHandlerFactory factory : factories) {
      URLStreamHandler urlStreamHandler = factory.createURLStreamHandler(scheme);
      if (urlStreamHandler != null) {
        return urlStreamHandler;
      }
    }
    return null;
  }
}
