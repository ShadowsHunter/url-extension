package org.honton.chas.url.extension.urlhandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import lombok.SneakyThrows;

/**
 * Factory for TCP connection(s)
 */
public class TcpUrlStreamHandlerFactory implements URLStreamHandlerFactory {

  @Override
  public URLStreamHandler createURLStreamHandler(String protocol) {
    return protocol.equals("tcp") ? new TcpUrlStreamHandler() : null;
  }

  static private class TcpUrlStreamHandler extends URLStreamHandler {

    /**
     * Opens a tcp connection
     *
     * @param tcpUrl the Tcp URL.
     * @return a TcpUrlConnection
     */
    @Override
    protected URLConnection openConnection(URL tcpUrl) {
      return new TcpUrlConnection(tcpUrl);
    }

    @SneakyThrows
    @Override
    protected void parseURL(URL u, String spec, int start, int limit) {
      super.parseURL(u, spec, start, limit);
      if (u.getPort() <= 0) {
        throw new MalformedURLException("tcp port must be present");
      }
      if (!u.getPath().isEmpty()) {
        throw new MalformedURLException("path cannot be supplied with tcp");
      }
      if (u.getQuery() != null) {
        throw new MalformedURLException("query cannot be supplied with tcp");
      }
      parseLocal(u.getUserInfo());
    }

    private static InetSocketAddress parseLocal(String user) {
      if(user == null) {
        return null;
      }
      String localAddress;
      int localPort;
      int colon = user.indexOf(':');
      if(colon < 0) {
        localAddress = user;
        localPort = 0;
      }
      else {
        localAddress = user.substring(0, colon);
        localPort = Integer.parseInt(user.substring(colon + 1));
      }
      return new InetSocketAddress(localAddress, localPort);
    }

    /**
     * Simple Tcp connection
     */
    static private class TcpUrlConnection extends URLConnection {

      private Socket client;

      /**
       * Constructs a URL connection to the specified URL. A connection to
       * the object referenced by the URL is not created.
       *
       * @param url the specified URL.
       */
      private TcpUrlConnection(URL url) {
        super(url);
      }

      /**
       * Opens communications to the ZooKeeper referenced by this
       * URL, if such a connection has not already been established.
       */
      @Override
      public void connect() throws IOException {
        synchronized (this) {
          if (!connected) {
            InetSocketAddress isa = new InetSocketAddress(url.getHost(), url.getPort());
            client = new Socket();
            client.bind(parseLocal(url.getUserInfo()));
            client.connect(isa, getConnectTimeout());
            connected = true;
          }
        }
      }

      /**
       * Returns an input stream that reads from the tcp socket.
       *
       * @return an InputStream that reads from the tcp socket.
       * @throws IOException if an I/O error occurs while
       * creating the input stream.
       */
      @Override
      public InputStream getInputStream() throws IOException {
        connect();
        return client.getInputStream();
      }

      /**
       * Returns an output stream that writes to the tcp socket.
       *
       * @return an OutputStream that writes to the tcp socket.
       * @throws IOException if an I/O error occurs while
       * creating the output stream.
       */
      @Override
      public OutputStream getOutputStream() throws IOException {
        connect();
        return client.getOutputStream();
      }
    }

  }
}

