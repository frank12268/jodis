/**
 * @(#)RedisServer.java, 2014-11-30. 
 * 
 * Copyright (c) 2014 CodisLabs.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.codis.jodis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * @author Apache9
 */
public class RedisServer {

    private final ProcessBuilder builder;

    private Process process;

    public RedisServer(int port) {
        builder = new ProcessBuilder().command("redis-server", "--port",
                Long.toString(port)).inheritIO();
    }

    public void start() throws IOException {
        process = builder.start();
    }

    public void stop() {
        if (process != null) {
            process.destroy();
        }
    }

    public static int probeFreePort() throws IOException {
        try (ServerSocket ss = new ServerSocket(0)) {
            ss.setReuseAddress(true);
            return ss.getLocalPort();
        }
    }

    public static void waitUntilRedisStarted(String host, int port) throws InterruptedException {
        for (;;) {
            try (Jedis jedis = new Jedis(host, port)) {
                if ("PONG".equals(jedis.ping())) {
                    break;
                }
            } catch (JedisException e) {}
            Thread.sleep(100);
        }
    }
}
