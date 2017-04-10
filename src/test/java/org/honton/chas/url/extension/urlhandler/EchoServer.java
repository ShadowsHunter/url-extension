package org.honton.chas.url.extension.urlhandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Example echo server
 */
public class EchoServer {

  private final ServerSocket serverSocket;

  EchoServer() throws IOException {
    serverSocket = new ServerSocket(0);
  }

  void serveOnce() throws IOException {
    new Thread() {
      @Override
      public void run() {
        try {
          acceptAndRespond();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }.start();
  }

  private void acceptAndRespond() throws IOException {
    try(Socket connectionSocket = serverSocket.accept()) {
      String line = readLine(connectionSocket);
      writeLine(connectionSocket, line);
    }
  }

  private void writeLine(Socket connectionSocket, String line) throws IOException {
    Writer writer = new OutputStreamWriter(connectionSocket.getOutputStream(), StandardCharsets.UTF_8);
    writer.append(line).close();
  }

  private String readLine(Socket connectionSocket) throws IOException {
    InputStream inputStream = connectionSocket.getInputStream();
    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
    return reader.readLine();
  }

  public URL getUrl(boolean bindAddress, boolean bindPort) throws IOException {
    StringBuilder sb = new StringBuilder();
    sb.append("tcp://");
    if (bindAddress) {
      sb.append("127.0.0.1");
      if(bindPort) {
        sb.append(':')
          .append(getEphemeralPort());
      }
      sb.append('@');
    }
    sb.append(serverSocket.getInetAddress().getHostAddress())
      .append(':')
      .append(serverSocket.getLocalPort());
    return new URL(sb.toString());
  }

  private static int getEphemeralPort() throws IOException {
    try (ServerSocket socket = new ServerSocket(0)) {
      return socket.getLocalPort();
    }
  }
}
