package utility;

public class TextFormatting {
    public static String toBold(String text) {
        return "*" + text + "*";
    }

    public static String toItalic(String text) {
        return "_" + text + "_";
    }

    public static String toCode(String text) {
        return "`" + text + "`";
    }

    public static String toInlineUrl(String text, String url) {
        return "[" + text + "]" + "(" + url + ")";
    }
}
