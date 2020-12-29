package io.github.teddyxlandlee.nios.filesplit;

import io.github.teddyxlandlee.nios.filesplit.util.*;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static io.github.teddyxlandlee.nios.filesplit.util.ByteHelperKt.fromInt;
import static io.github.teddyxlandlee.nios.filesplit.util.ByteHelperKt.toInt;

public class Core {
    public static void run(CodecStatus codecStatus, File file, int maxOneFileSize, String outputDirectory) {
        if (codecStatus == CodecStatus.ENCODE)
            encode(file, maxOneFileSize, outputDirectory);
        else
            decode(file);
    }

    public static void encode(File file, int maxOneFileSize, String outputDirectory) {
        System.out.println("Try encoding...");
        File outputDirectoryFile = new File(outputDirectory);
        if (outputDirectoryFile.exists() || !outputDirectoryFile.mkdir()) {
            encode(file, maxOneFileSize, outputDirectory + "_");
            return;
        }

        int i = 0;
        FileOutputStream fileOutputStream;
        try {
            byte[] bytes;
            FileInputStream inputStream = new FileInputStream(file);
            while (true) {
                bytes = NBytesHelper.readNBytes(inputStream, maxOneFileSize - 4);
                if (bytes.length == 0) break;

                File outputOneFile = new File(outputDirectory + '/' + i + ".fsplit");
                if (!outputOneFile.createNewFile()) {
                    throw new IOException("cannot create new file: " + outputOneFile.getName());
                }
                fileOutputStream = new FileOutputStream(outputOneFile);
                fileOutputStream.write(fromInt(VersionKt.fsplitHeader));
                fileOutputStream.write(bytes);
                fileOutputStream.close();
                ++i;
            }
            inputStream.close();
            fileOutputStream = new FileOutputStream(outputDirectory + "/INFO.fsplitinfo");
            fileOutputStream.write(fromInt(VersionKt.fsplitinfoHeader));    // Magic number 4
            fileOutputStream.write(VersionKt.fsplitinfoVersion);    // fsplitinfo version 1
            fileOutputStream.write(fromInt(i)); // Max filename count
            String oldFileName = file.getName();
            byte[] b = oldFileName.getBytes(StandardCharsets.UTF_8);
            fileOutputStream.write(fromInt(b.length));  // Old Filename String Bytes length
            fileOutputStream.write(b);   // Old Filename String as UTF-8
            fileOutputStream.close();

            System.out.println("Process finished. Successfully created directory " + outputDirectoryFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void decode(File file) {
        System.out.println("Try decoding...");
        String directoryWithSlash = file.getAbsolutePath() + '/';
        byte[] cache = new byte[4];
        int iCache;
        try {
            FileInputStream inputStream = new FileInputStream(directoryWithSlash + "INFO.fsplitinfo");
            Fsplitinfo fsplitinfo = ByteFileExecutorKt.infoFromStream(inputStream);
            String newFileName = fsplitinfo.getFilename();
            String newFilePath = file.getParent() + '/' + newFileName;
            File newFile = new File(newFilePath);
            if (newFile.exists())
                throw new InvalidFileException(newFileName, 0x00000007);
            if (!newFile.createNewFile())
                throw new InvalidFileException(newFileName, 0x00000007);

            FileOutputStream outputStream = new FileOutputStream(newFile);

            iCache = 0; // current file name count
            for (int iCache2; iCache < fsplitinfo.getMaxFileNameCount(); ++iCache) {
                inputStream = new FileInputStream(file.getName() + '/' + iCache + ".fsplit");
                iCache2 = inputStream.read(cache, 0, 4);
                if (iCache2 != 4 || toInt(cache) != VersionKt.fsplitHeader) {
                    throw new InvalidFileException(iCache + ".fsplit", 0x00000002);
                }
                cache = inputStream.readAllBytes();
                outputStream.write(cache);
                inputStream.close();
            }
            outputStream.close();
            System.out.println("Successfully decode file at " + newFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void decodeGit(String server, String repo, String branch, String path) {
        try {
            URL url = new URL(String.format("%s/%s/raw/%s/%s", server, repo, branch, path));
            decodeGit(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void decodeGit(URL url) throws IOException {
        URL urlInfo = new URL(url.toString() + "/INFO.fsplitinfo");
        //File infoFile = NetworkHelperKt.tmpFileDownloaded(urlInfo);
        InputStream inputStream = NetworkHelperKt.httpInputStream(urlInfo);
        byte[] cache = new byte[4];

        Fsplitinfo fsplitinfo = ByteFileExecutorKt.infoFromStream(inputStream);
        String newFilename = fsplitinfo.getFilename();
        File newFile = new File(newFilename);
        if (newFile.exists())
            throw new InvalidFileException(newFilename, 0x00000007);
        if (!newFile.createNewFile())
            throw new InvalidFileException(newFilename, 0x00000007);

        FileOutputStream outputStream = new FileOutputStream(newFile);

        int filenameCount = 0;
        for (int iCache2; filenameCount < fsplitinfo.getMaxFileNameCount(); ++filenameCount) {
            urlInfo = new URL(url.toString() + '/' + filenameCount + ".fsplit");
            inputStream = NetworkHelperKt.httpInputStream(urlInfo);
            iCache2 = inputStream.read(cache, 0, 4);
            if (iCache2 != 4 || toInt(cache) != VersionKt.fsplitHeader)
                throw new InvalidFileException(iCache2 + ".fsplit", 0x00000002);
            cache = inputStream.readAllBytes();
            outputStream.write(cache);
            inputStream.close();
        }
        outputStream.close();
        System.out.println("Successfully decode file at " + newFile.getAbsolutePath());
    }
}
