package tech.getwell.t1.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author Wave
 * @date 2019/8/12
 */
public class ZipUtils {

    /**
     * 压缩成zip
     */
    public static void zipFile(File resFile, File zipFile) throws IOException {
        final int BUFF_SIZE = 1024 * 1024; // 1M Byte
        ZipOutputStream zipout = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile), BUFF_SIZE));
        zipFile(resFile, zipout, "");
        zipout.close();
    }

    /**
     *    * 压缩文件
     *    *
     *    * @param resFile 需要压缩的文件（夹）
     *    * @param zipout 压缩的目的文件
     *    * @param rootpath 压缩的文件路径
     *    * @throws FileNotFoundException 找不到文件时抛出
     *    * @throws IOException 当压缩过程出错时抛出
     *    
     */
    private static void zipFile(File resFile, ZipOutputStream zipout, String rootpath)throws FileNotFoundException, IOException {

        rootpath = rootpath + (rootpath.trim().length() == 0 ? "" : File.separator)
                + resFile.getName();
        rootpath = new String(rootpath.getBytes("8859_1"), "GB2312");
        if (resFile.isDirectory()) {
            File[] fileList = resFile.listFiles();
            for (File file : fileList) {
                zipFile(file, zipout, rootpath);
            }
        } else {

            final int BUFF_SIZE = 1024 * 1024; // 1M Byte
            byte buffer[] = new byte[BUFF_SIZE];
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(resFile),BUFF_SIZE);
            zipout.putNextEntry(new ZipEntry(rootpath));
            int realLength;
            while ((realLength = in.read(buffer)) != -1) {
                zipout.write(buffer, 0, realLength);
            }
            in.close();
            zipout.flush();
            zipout.closeEntry();
        }
    }
}
