/**
 * @Description
 * @Author everforcc
 * @Date 2022-11-17 15:17
 * Copyright
 */

package cn.cc.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * 3. copy
 */
public class Test3FilesCopy {

    public static void main(String[] args) throws IOException {
        String source = "F:\\Cache\\BaiduNetdiskDownload\\Netty网络编程\\Netty教程源码资料";
        String target = "F:\\Cache\\BaiduNetdiskDownload\\Netty网络编程\\Netty教程源码资料1";
        Files.walk(Paths.get(source)).forEach(path -> {
            try {
                String targetName = path.toString().replace(source, target);
                if (Files.isDirectory(path)) {
                    Files.createDirectory(Paths.get(targetName));
                } else if (Files.isRegularFile(path)) {
                    Files.copy(path, Paths.get(targetName));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

}
