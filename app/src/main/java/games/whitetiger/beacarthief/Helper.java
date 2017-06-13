package games.whitetiger.beacarthief;

import android.text.Html;
import android.text.Spanned;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Random;

class Helper {
    private final static Random number = new Random();

    static boolean isValidEmail(CharSequence target) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    private static int getRandomNumber() {
        return number.nextInt(100);
    }

    static boolean isAnEvent(int maxChance) {
        return getRandomNumber() <= maxChance;
    }

    static String thousandSeperator(int value) {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.GERMAN);
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();

        symbols.setGroupingSeparator('.');
        formatter.setDecimalFormatSymbols(symbols);
        return formatter.format(value);
    }

    /**
     * Adds a version check and use the old method on Android M and below,
     * on Android N and higher it should use the new method. If there is no version check
     * the app will break on lower Android versions.
     * @param html Html string to text
     * @return Spanned
     */
    @SuppressWarnings("deprecation")
    static Spanned fromHtml(String html){
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }

    static int getMaxExpForLevel(int level) {
        return level * 20;
    }

    static int getMaxExpForAllLevels(int level) {
        int exp = 0;

        for (int i = 0; i <= level; i ++) {
            exp += getMaxExpForLevel(i);
        }

        return exp;
    }
}