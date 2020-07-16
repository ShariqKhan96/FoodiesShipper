package arkitchen.karachi.foodiesshipper.utils;

import android.content.Context;
import android.content.SharedPreferences;


public class PrefUtils {

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences("SHARED_PREFERENCES", Context.MODE_PRIVATE);
    }

    public static void putString(String key, String value, Context context) {
        getSharedPreferences(context).edit().putString(key, value).apply();

    }

    public static String getString(String key, Context context) {
        return getSharedPreferences(context).getString(key, "null");
    }

    public static void remove(Context context) {
        getSharedPreferences(context).edit().clear().apply();
    }

    public static int getInt(String key, Context context) {
        return getSharedPreferences(context).getInt(key, 0);
    }

    public static void putInt(String key, int value, Context context) {
        getSharedPreferences(context).edit().putInt(key, value).apply();
    }

    public static void clearCache(Context context) {
        getSharedPreferences(context).edit().clear().apply();

    }

    public static void removeString(Context context, String stage) {
        getSharedPreferences(context).edit().remove(stage).apply();
    }
}
