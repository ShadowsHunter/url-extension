package org.honton.chas.url.extension.urlhandler;

import com.google.common.io.CharStreams;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test URL registrations
 */
public class UrlStreamHandlerRegistryTest {

  @Before
  public void initializeRegistry() {
    UrlStreamHandlerRegistry.register();
  }

  @Test(timeout = 5000)
  public void testTcp() throws IOException {
    testTcpConnection(false, false);
  }

  @Test(timeout = 5000)
  public void testTcpWithAddressBind() throws IOException {
    testTcpConnection(true, false);
  }

  @Test(timeout = 5000)
  public void testTcpWithAddressAndPortBind() throws IOException {
    testTcpConnection(true, true);
  }

  private void testTcpConnection(boolean bindAddress, boolean bindPort) throws IOException {
    EchoServer es = new EchoServer();
    es.serveOnce();

    URLConnection connection = es.getUrl(bindAddress, bindPort).openConnection();

    OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8);
    writer.append("echo this!\n").flush();

    InputStreamReader reader = new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8);
    Assert.assertEquals("echo this!", CharStreams.toString(reader));
  }

  @Test(expected = MalformedURLException.class)
  public void testTcpNoPort() throws IOException {
    new URL("tcp://example.com");
  }

  @Test(expected = MalformedURLException.class)
  public void testPathSupplied() throws IOException {
    new URL("tcp://example.com:10/");
  }

  @Test(expected = MalformedURLException.class)
  public void testQuerySupplied() throws IOException {
    new URL("tcp://example.com:10?x=y");
  }

  @Test(expected = MalformedURLException.class)
  public void testNotRegistered() throws IOException {
    new URL("unsupported://example.com/unregistered");
  }

  @Test
  public void testJreDefaults() throws IOException {
    new URL("https://example.com/jre/default");
  }
}

