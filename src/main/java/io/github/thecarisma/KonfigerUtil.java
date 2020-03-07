package io.github.thecarisma;

class KonfigerUtil {

    public static String escapeString(String value, char... extraEscapes) {
        StringBuilder finalValue = new StringBuilder();
        for (int i = 0; i < value.length(); ++i) {
            char c = value.charAt(i);
            switch (c) {
                case '\t':
                    finalValue.append("\\t");
                    break;
                case '\n':
                    finalValue.append("\\n");
                    break;
                case '\r':
                    finalValue.append("\\r");
                    break;
                case '\f':
                    finalValue.append("\\f");
                    break;
                case '\b':
                    finalValue.append("\\b");
                    break;
                case '\\':
                    if (extraEscapes.length > 0) {
                        for (char extraEscape : extraEscapes) {
                            if (value.charAt(i) == extraEscape) {
                                finalValue.append(value.charAt(i));
                                break;
                            }
                        }
                    } else {
                        finalValue.append("\\\\");
                    }
                    break;
                case '\'':
                    finalValue.append("\\'");
                    break;
                case '\"':
                    finalValue.append("\\\"");
                    break;
                case '\000':
                    finalValue.append("\\000");
                    break;
                default:
                    if (extraEscapes.length > 0) {
                        for (char extraEscape : extraEscapes) {
                            if (c==extraEscape) {
                                finalValue.append("\\\\");
                                break;
                            }
                        }
                    }
                    finalValue.append(c);
            }
        }
        return finalValue.toString();
    }

    public static String unEscapeString(String value, char... extraEscapes) {
        StringBuilder finalValue = new StringBuilder();
        for (int i = 0; i < value.length(); ++i) {
            char c = value.charAt(i);
            if (c=='\\') {
                if (i==value.length() - 1) {
                    finalValue.append(c);
                    break;
                }
                int d = ++i;
                switch (value.charAt(d)) {
                    case 't':
                        finalValue.append("\t");
                        break;
                    case 'n':
                        finalValue.append("\n");
                        break;
                    case 'r':
                        finalValue.append("\r");
                        break;
                    case 'f':
                        finalValue.append("\f");
                        break;
                    case 'b':
                        finalValue.append("\b");
                        break;
                    case '\\':
                        finalValue.append("\\");
                        break;
                    case '\'':
                        finalValue.append("'");
                        break;
                    case '"':
                        finalValue.append("\"");
                        break;
                    case '\000':
                        finalValue.append("\000");
                        break;
                    default:
                        if (extraEscapes.length > 0) {
                            boolean continua = false;
                            for (char extraEscape : extraEscapes) {
                                if (value.charAt(d) == extraEscape) {
                                    finalValue.append(extraEscape);
                                    continua = true;
                                    break;
                                }
                            }
                            if (continua) {
                                continue;
                            }
                        }
                        finalValue.append(value.charAt(d)).append(c);
                }
                continue;
            }
            finalValue.append(c);
        }
        return finalValue.toString();
    }

}
