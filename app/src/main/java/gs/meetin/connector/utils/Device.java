package gs.meetin.connector.utils;

import android.content.ContentResolver;
import android.os.Build;
import android.provider.Settings;

public class Device {

    public static String getDeviceName() {
        return String.format("%s %s", Build.MANUFACTURER, Build.MODEL);
    }

    public static String getAndroidId(ContentResolver cr) {
        return Settings.Secure.getString(cr, Settings.Secure.ANDROID_ID);
    }
}
