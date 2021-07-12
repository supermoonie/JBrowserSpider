package com.github.supermoonie.jbrwoserspider.util;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author supermoonie
 * @since 2021/4/19
 */
public class UnzipUtil {

    private static final int BUFFER_SIZE = 4096;

    /**
     * 解压
     *
     * @param inputStream   input
     * @param destDirectory output
     * @throws IOException e
     */
    public static void unzip(InputStream inputStream, String destDirectory) throws IOException {
        File destDir = new File(destDirectory);
        if (!destDir.exists() && !destDir.mkdir()) {
            throw new IOException(destDirectory + " make dir fail");
        }
        ZipInputStream zipIn = new ZipInputStream(inputStream);
        ZipEntry entry = zipIn.getNextEntry();
        // iterates over entries in the zip file
        while (entry != null) {
            String filePath = destDirectory + File.separator + entry.getName();
            if (!entry.isDirectory()) {
                // if the entry is a file, extracts it
                extractFile(zipIn, filePath);
            } else {
                // if the entry is a directory, make the directory
                File dir = new File(filePath);
                if (!dir.mkdirs()) {
                    throw new IOException(dir.getAbsolutePath() + " make dir fail");
                }
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();
    }

    /**
     * Extracts a zip file specified by the zipFilePath to a directory specified by
     * destDirectory (will be created if does not exists)
     *
     * @param zipFilePath   input
     * @param destDirectory output
     * @throws IOException e
     */
    public static void unzip(String zipFilePath, String destDirectory) throws IOException {
        unzip(new FileInputStream(zipFilePath), destDirectory);
    }

    /**
     * Extracts a zip entry (file entry)
     *
     * @param zipIn    input
     * @param filePath output
     * @throws IOException e
     */
    private static void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }
}
