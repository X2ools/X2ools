
package org.x2ools.t9apps.match;

import android.util.Log;

public class Matcher {

    public static String TAG = "Matcher";

    public static boolean match(String name, String key) {
        byte[] nameNumbers = ToPinYinUtils.getPinyinNum(name);
        byte[] keyNumbers = numberString2Byte(key);
        Log.d(TAG, "nameNumbers :" + nameNumbers);
        Log.d(TAG, "keyNumbers :" + keyNumbers);
        int namePointer = 0;
        int keyPointer = 0;
        while (keyPointer < keyNumbers.length
                && namePointer < nameNumbers.length) {
            if (keyNumbers[keyPointer] == nameNumbers[namePointer]) {
                keyPointer++;
                namePointer++;
            } else if(key.charAt(keyPointer) == name.charAt(namePointer)) {
                keyPointer++;
                namePointer++;
            } else {
                namePointer++;
            }
        }
        return keyPointer == keyNumbers.length;
    }

    private static byte[] numberString2Byte(String source) {
        byte[] bytes = new byte[source.length()];
        for (int i = 0; i < source.length(); i++) {
            byte b = char2Byte(source.charAt(i));
            bytes[i] = b;
        }
        return bytes;
    }

    private static byte char2Byte(char ch) {
        switch (ch) {
            case '0':
                return 0;
            case '1':
                return 1;
            case '2':
                return 2;
            case '3':
                return 3;
            case '4':
                return 4;
            case '5':
                return 5;
            case '6':
                return 6;
            case '7':
                return 7;
            case '8':
                return 8;
            case '9':
                return 9;
            default:
                throw new RuntimeException("Invalid input");
        }
    }
}
