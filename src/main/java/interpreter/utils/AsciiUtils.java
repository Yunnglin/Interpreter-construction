package interpreter.utils;

import interpreter.Const;

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

    public static Character decimal2Ascii(int d) {
        return byte2Ascii((byte) d);
    }
}
