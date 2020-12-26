package io.github.teddyxlandlee.nios.filesplit.util;

import java.util.HashMap;
import java.util.Map;

public class Emaps {
    public static Map<Character, Integer> fileSizeMap = new HashMap<>();
    static {
        fileSizeMap.put('K', 1024);
        fileSizeMap.put('M', 1024 * 1024);
        fileSizeMap.put('G', 1024 * 1024 * 1024);
    }

    public static Map<Integer, String> errMap = new HashMap<>();
    static {
        errMap.put(0x00000002, "bad file header for INFO.fsplitinfo");
        errMap.put(0x00000003, "invalid data version: < 1");
        errMap.put(0x00000004, "bad string byte length");
        errMap.put(0x00000005, "invalid or null new filename");
        errMap.put(0x00000006, "invalid file count");
        errMap.put(0x00000007, "new file is invalid or already exists");
    }
}
