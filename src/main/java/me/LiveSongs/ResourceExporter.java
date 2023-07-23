package me.LiveSongs;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * @author web
 */
public class ResourceExporter {

    public static void moveFolder(String sourcePath, String destinationPath) {
        sourcePath = sourcePath.replace('\\', '/');
        destinationPath = destinationPath.replace('\\', '/');
        File sourceFolder = new File(sourcePath);
        sourceFolder.mkdir();
        File[] files = sourceFolder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // 如果是子文件夹，递归调用moveFolder函数
                    file.mkdir();
                    moveFolder(file.getAbsolutePath(), destinationPath + "/" + file.getName());
                } else {
                    moveFile(file.getAbsolutePath(), destinationPath + "/" + file.getName());
                }
            }
        }
    }

    public static void moveFile(String o, String des) {
        try {
            Path pa = Paths.get(o);
            Path e = Paths.get(des);

            Files.createDirectories(e.getParent());
            LiveSongs.logger.info(des);
            Files.copy(pa, e, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
            LiveSongs.logger.info("[初始化] " + pa.getFileName() + " 移动成功!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
