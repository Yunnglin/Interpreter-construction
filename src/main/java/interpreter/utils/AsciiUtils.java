package interpreter.utils;

import interpreter.Const;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class AsciiUtils {

    private static final String HEX_DIGITAL = "0123456789abcdef";
    private static final int ESCAPE_MAX = 0x7f;

    public static Byte hexStr2Byte(String hex) {
        if (hex.length() > 2) {
            return null;
        }

        Byte b;

        try {
            b = (byte) Integer.parseInt(hex, 16);
        } catch (Exception | Error e) {
            System.out.println(e.getMessage());
            e.printStackTrace();

            b = null;
        }

        return b;
    }

    public static Character byte2Ascii(byte b) {
        if (b < 0) {
            // ANSI Ascii uses the last 7 bits
            System.out.println();
            return null;
        }

        byte[] bytes = new byte[] {b};
        String tempStr = new String(bytes, StandardCharsets.US_ASCII);

        return tempStr.charAt(0);
    }

    public static Character hex2Ascii(String hex) {
        Byte aByte = hexStr2Byte(hex);
        if (aByte == null) {
            return null;
        }

        return byte2Ascii(aByte);
    }

    public static Character controlChar2Ascii(char ch) {
        Byte aByte = Const.controlCharacters.get(ch);

        if (aByte == null) {
            System.out.println("No such control character in escape character set");
            return null;
        }

        return byte2Ascii(aByte);
    }

    public static Byte octal2Byte(String oct) {
        if (oct.length() > 3) {
            return null;
        }

        Byte b;
        try {
            b = (byte) Integer.parseInt(oct, 8);
        } catch (Exception | Error e) {
            System.out.println(e.getMessage());
            e.printStackTrace();

            b = null;
        }

        return b;
    }

    /**
     * convert octal number to ascii
     * @param oct String, octal number in string form
     * @return the ascii character
     */
    public static Character octal2Ascii(String oct) {
        Byte aByte = octal2Byte(oct);
        if (aByte == null) {
            return null;
        }

        return byte2Ascii(aByte);
    }

    public static boolean isAsciiCharacter(char ch) {
        byte[] bytes = Character.toString(ch).getBytes();

        return bytes.length == 1 && bytes[0] >= 0;
    }

    public static Character convert2EscapeCharacter(String str) {
        // \c | \010 | \x7f, the param only contains the part after '\'
        char[] chars = str.toCharArray();
        int len = chars.length;

        if (len < 1) {
            return null;
        }

        StringBuilder value = new StringBuilder();
        if (chars[0] == 'x') {
            // in hex form
            if (len < 2 || len > 3) {
                return null;
            }
            for (int i=1; i<len; ++i) {
                value.append(chars[i]);
            }

            return hex2Ascii(value.toString());
        } else if (len == 1 && Const.controlCharacters.get(chars[0]) != null) {
            // in control character form
            return controlChar2Ascii(chars[0]);
        } else {
            // in octal form (maybe)
            return octal2Ascii(str);
        }
    }

    public static String convert2AsciiString(String str) {
        byte[] bytes = str.getBytes(Charset.defaultCharset());
        return new String(bytes, StandardCharsets.US_ASCII);
    }
}
