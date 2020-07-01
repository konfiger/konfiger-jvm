package io.github.thecarisma;

class KonfigerUtil {

    public static String escapeString(String value, char... extraEscapes) {
        StringBuilder finalValue = new StringBuilder();
        for (int i = 0; i < value.length(); ++i) {
            char c = value.charAt(i);
            if (extraEscapes.length > 0) {
                for (char extra : extraEscapes) {
                    if (c == extra) {
                        finalValue.append('^');
                        break;
                    }
                }
            }
            finalValue.append(c);
        }
        return finalValue.toString();
    }

    public static String unEscapeString(String value, char... extraEscapes) {
        StringBuilder finalValue = new StringBuilder();
        for (int i = 0; i < value.length(); ++i) {
            char c = value.charAt(i);
            if (c=='^') {
                if (i==value.length() - 1) {
                    finalValue.append(c);
                    break;
                }
                int d = ++i;
                if (extraEscapes.length > 0) {
                    boolean continua = false;
                    for (char extra : extraEscapes) {
                        if (value.charAt(d) == extra) {
                            finalValue.append(extra);
                            continua = true;
                            break;
                        }
                    }
                    if (continua) {
                        continue;
                    }
                }
                finalValue.append('^');
                finalValue.append(value.charAt(d));
                continue;
            }
            finalValue.append(c);
        }
        return finalValue.toString();
    }

}
