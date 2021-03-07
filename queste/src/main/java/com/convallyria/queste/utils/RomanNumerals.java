package com.convallyria.queste.utils;

import java.util.TreeMap;

public class RomanNumerals {

    private final static TreeMap<Integer, String> MAP = new TreeMap<>();

    static {
        MAP.put(1000, "M");
        MAP.put(900, "CM");
        MAP.put(500, "D");
        MAP.put(400, "CD");
        MAP.put(100, "C");
        MAP.put(90, "XC");
        MAP.put(50, "L");
        MAP.put(40, "XL");
        MAP.put(10, "X");
        MAP.put(9, "IX");
        MAP.put(5, "V");
        MAP.put(4, "IV");
        MAP.put(1, "I");
    }

    public static String toRomanNumerals(int number) {
        int l = MAP.floorKey(number);
        if (number == l) {
            return MAP.get(number);
        }
        return MAP.get(l) + toRomanNumerals(number - l);
    }
}
