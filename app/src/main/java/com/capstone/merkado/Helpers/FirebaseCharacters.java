package com.capstone.merkado.Helpers;

import java.util.HashMap;
import java.util.Map;

public class FirebaseCharacters {
    private static final Map<Character, String> ENCODE_MAP = new HashMap<>();
    static {
        ENCODE_MAP.put('.', "__DOT__");
        ENCODE_MAP.put('$', "__DOLLAR__");
        ENCODE_MAP.put('[', "__LEFTBRACKET__");
        ENCODE_MAP.put(']', "__RIGHTBRACKET__");
        ENCODE_MAP.put('#', "__HASH__");
    }

    private static final Map<String, Character> DECODE_MAP = new HashMap<>();
    static {
        DECODE_MAP.put("__DOT__", '.');
        DECODE_MAP.put("__DOLLAR__", '$');
        DECODE_MAP.put("__LEFTBRACKET__", '[');
        DECODE_MAP.put("__RIGHTBRACKET__", ']');
        DECODE_MAP.put("__HASH__", '#');
    }

    /**
     * Encodes the string into Firebase friendly characters.<br/>
     * "<b>.</b>" --> "<b>__DOT__</b>"<br/>
     * "<b>$</b>" --> "<b>__DOLLAR__</b>"<br/>
     * "<b>[</b>" --> "<b>__LEFTBRACKET__</b>"<br/>
     * "<b>]</b>" --> "<b>__RIGHTBRACKET__</b>"<br/>
     * "<b>#</b>" --> "<b>__HASH__</b>"
     * @param input raw string.
     * @return encoded string.
     */
    public static String encode(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        // Iterate through each character in the map and replace
        for (Map.Entry<Character, String> entry : ENCODE_MAP.entrySet()) {
            input = input.replace(entry.getKey().toString(), entry.getValue());
        }
        return input;
    }

    /**
     * Decodes the string into its original characters.<br/>
     * "<b>__DOT__</b>" --> "<b>.</b>"<br/>
     * "<b>__DOLLAR__</b>" --> "<b>$</b>"<br/>
     * "<b>__LEFTBRACKET__</b>" --> "<b>[</b>"<br/>
     * "<b>__RIGHTBRACKET__</b>" --> "<b>]</b>"<br/>
     * "<b>__HASH__</b>" --> "<b>#</b>"
     * @param input raw string.
     * @return decoded string.
     */
    public static String decode(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        // Iterate through each encoded string in the map and replace
        for (Map.Entry<String, Character> entry : DECODE_MAP.entrySet()) {
            input = input.replace(entry.getKey(), entry.getValue().toString());
        }
        return input;
    }
}
