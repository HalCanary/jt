package org.halcanary.jt;

import java.nio.charset.StandardCharsets;
import org.junit.Assert;
import org.junit.Test;
import org.zeromq.ZMQ;

public class AppTest {
    @Test
    public void testZmqPubSub() {
        final String testSocketAddr = "ipc:///tmp/foo_bar";
        final String message = "Hello, World!";

        ZMQ.Context zmqContext = ZMQ.context(1);

        ZMQ.Socket subSocket = zmqContext.socket(ZMQ.SUB);
        Assert.assertNotNull(subSocket);

        try {
            subSocket.bind(testSocketAddr);
        } catch (org.zeromq.ZMQException e) {
            Assert.fail(e.toString());
        }

        subSocket.subscribe(new byte[0]); // Subscribe to everything.

        ZMQ.Socket pubSocket = zmqContext.socket(ZMQ.PUB);
        Assert.assertNotNull(pubSocket);

        try {
            pubSocket.connect(testSocketAddr);
        } catch (org.zeromq.ZMQException e) {
            Assert.fail(e.toString());
        }

        try {
            Thread.sleep(1000);  // ms
        } catch (InterruptedException e) {}

        pubSocket.send(message.getBytes(StandardCharsets.UTF_8), 0);

        try {
            Thread.sleep(1000);  // ms
        } catch (InterruptedException e) {}

        byte[] received = subSocket.recv(ZMQ.DONTWAIT);

        if (null == received) {
            Assert.fail("subSocket.recv(ZMQ.DONTWAIT) returned null");
        }
        Assert.assertEquals(message, new String(received, StandardCharsets.UTF_8));
    }
}
