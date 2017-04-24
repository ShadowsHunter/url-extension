url-extension
=============

Extend a java process with multiple URLStreamHandlers. URLStreamHandler can be registered through
the service provider pattern or programmatically.  This artifact provides support for a 'tcp' scheme.
  
This jar can also be used as a maven extension to allow maven plugins to use URL schemes not handled by the JRE.

### Requirements
* Minimum of Java 7

## Maven Coordinates
To include url-extension as a dependency in your maven build, use the following fragment in your pom.
```xml
  <build>
    <dependencies>
      <dependency>
        <groupId>org.honton.chas.url</groupId>
        <artifactId>url-extension</artifactId>
        <version>0.0.1</version>
      </dependency>
    </dependencies>
  </build>
```

To include url-extension as an extension for plugins in your maven build, use the following fragment in your pom.
```xml
  <build>
    <extensions>
      <extension>
        <groupId>org.honton.chas.url</groupId>
        <artifactId>url-extension</artifactId>
        <version>0.0.1</version>
      </extension>
    </extensions>
  </build>
```

## UrlExtension
To register a URLStreamHandlerFactory:
```java
    UrlStreamHandlerRegistry registry;
    // ...
    registry.factory(new TcpUrlStreamHandlerFactory());
```

To add all URLStreamHandlerFactory(s) that implement the service provider pattern:

```java
   UrlStreamHandlerRegistry registry = UrlStreamHandlerRegistry.register();
```

To package your URLStreamHandlerFactory as a service provider, provide a file named
**java.net.URLStreamFactory** in the **META-INF/services** directory of the jar.  
Each line of this file should contain the name of a class implementing java.net.URLStreamFactory 
which should be added to the registry.  For example, the
```text
org.honton.chas.url.extension.urlhandler.TcpUrlStreamHandlerFactory
```

## Tcp scheme

The **TcpUrlStreamHandler** supports URLs with the following syntax: ```tcp://[[<localaddress>][:<localport>]@]<remoteaddress>:<remoteport>```.
The remote address and remote port values are required.  The local address and local port values are optional.
