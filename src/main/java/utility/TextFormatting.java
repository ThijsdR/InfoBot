package utility;

public class TextFormatting {
    public static String toBold(String text) {
        return "<b>" + text + "</b>";
    }

    public static String toItalic(String text) {
        return "<i>" + text + "</i>";
    }

    public static String toCode(String text) {
        return "<code>" + text + "</code>";
    }
}
