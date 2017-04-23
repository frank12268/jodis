package io.codis.jodis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.curator.test.TestingServer;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class ZooKeeperServer {

    private int zkPort;

    private TestingServer zkServer;

    private File directory;

    private ObjectMapper mapper = new ObjectMapper();

    public static void deleteDirectory(File directory) throws IOException {
        if (!directory.exists()) {
            return;
        }
        Files.walkFileTree(directory.toPath(), new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                    throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc)
                    throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }

        });
    }

    public ZooKeeperServer(int port, File tempDirectory) throws Exception {
        zkServer = new TestingServer(port, tempDirectory, true);
        zkPort = zkServer.getPort();
    }

    public void addNode(ZooKeeper zk, String zkProxyDir, String name, int port, String state, CreateMode mode)
            throws IOException, InterruptedException, KeeperException {
        if (zk.exists(zkProxyDir, null) == null) {
            zk.create(zkProxyDir, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
        ObjectNode node = mapper.createObjectNode();
        node.put("addr", "127.0.0.1:" + port);
        node.put("state", state);
        zk.create(zkProxyDir + "/" + name, mapper.writer().writeValueAsBytes(node),
                ZooDefs.Ids.OPEN_ACL_UNSAFE, mode);
    }

    public void removeNode(ZooKeeper zk, String zkProxyDir, String name) throws InterruptedException, KeeperException, IOException {
        zk.delete(zkProxyDir + "/" + name, -1);
    }

    public int getZkPort(){
        return zkPort;
    }

    public TestingServer getZkServer(){
        return zkServer;
    }
}
