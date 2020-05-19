package io.github.thecarisma;

public class KonfigerFieldsExposer {

    public static String[] getCurrentCachedObject(Konfiger konfiger) {
        return konfiger.currentCachedObject;
    }

    public static String[] getPrevCachedObject(Konfiger konfiger) {
        return konfiger.prevCachedObject;
    }

    public static String getKonfigerUtil_escapeString(String value, char... extraEscapes) {
        return KonfigerUtil.escapeString(value, extraEscapes);
    }

    public static String getKonfigerUtil_unEscapeString(String value, char... extraEscapes) {
        return KonfigerUtil.unEscapeString(value, extraEscapes);
    }

}
