/**
 * @Description
 * @Author everforcc
 * @Date 2022-11-17 14:47
 * Copyright
 */

package cn.cc.nio.file;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 2. 遍历 walkFileTree
 */
public class Test2FilesWalkFileTree {

    public static void main(String[] args) throws IOException {
        // 遍历文件夹和目录
        //walkFileTreeFileAndDir();

        // 遍历找jar
        //walkFileTreeJar();

        // 便利删除文件
        walkFileTreeDelete();
    }

    /**
     * 遍历文件和目录
     */
    private static void walkFileTreeFileAndDir() throws IOException {
        // 局部变量
        AtomicInteger dirCount = new AtomicInteger();
        AtomicInteger fileCount = new AtomicInteger();
        // 访问者模式
        Files.walkFileTree(Paths.get("C:\\everforcc\\java\\environment\\jdk\\jdk-8u201"), new SimpleFileVisitor<Path>() {

            // 遍历文件前
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                System.out.println("====>" + dir);
                dirCount.incrementAndGet();
                return super.preVisitDirectory(dir, attrs);
            }

            // 遍历文件
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                System.out.println(file);
                fileCount.incrementAndGet();
                return super.visitFile(file, attrs);
            }

            // 失败时
            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                return super.visitFileFailed(file, exc);
            }

            // 遍历文件后
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                return super.postVisitDirectory(dir, exc);
            }
        });

        System.out.println("dir count " + dirCount);
        System.out.println("file count " + fileCount);
    }

    /**
     * 遍历jar文件
     */
    private static void walkFileTreeJar() throws IOException {
        // 局部变量
        AtomicInteger jarCount = new AtomicInteger();
        // 访问者模式
        Files.walkFileTree(Paths.get("C:\\everforcc\\java\\environment\\jdk\\jdk-8u201"), new SimpleFileVisitor<Path>() {
            // 遍历文件
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.toString().endsWith(".jar")) {
                    System.out.println(file);
                    jarCount.incrementAndGet();
                }
                return super.visitFile(file, attrs);
            }
        });

        System.out.println("jar count " + jarCount);
    }

    /**
     * 便利删除文件
     * 不走回收站
     */
    private static void walkFileTreeDelete() throws IOException {
        // 访问者模式
        Files.walkFileTree(Paths.get("F:\\Cache\\BaiduNetdiskDownload\\Netty网络编程\\Netty教程源码资料 - 副本"), new SimpleFileVisitor<Path>() {

            // 遍历文件前
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                System.out.println("====> 进入 " + dir);
                return super.preVisitDirectory(dir, attrs);
            }

            // 遍历文件
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                System.out.println(file);
                Files.delete(file);
                return super.visitFile(file, attrs);
            }

            // 遍历文件后
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                System.out.println("<==== 退出 " + dir);
                Files.delete(dir);
                return super.postVisitDirectory(dir, exc);
            }
        });
    }

}
