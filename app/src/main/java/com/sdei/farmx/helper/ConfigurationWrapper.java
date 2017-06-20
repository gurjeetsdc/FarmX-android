package com.sdei.farmx.helper;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.NonNull;

import com.fasterxml.jackson.core.util.VersionUtil;

import java.util.Locale;

public class ConfigurationWrapper {

    private ConfigurationWrapper() {
    }

    // Creates a Context with updated Locale.
    @SuppressWarnings("deprecation")
    public static Context wrapLocale(@NonNull Context context,
                                     @NonNull final Locale locale) {
        final Resources res = context.getResources();
        final Configuration config = res.getConfiguration();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(locale);
            context = context.createConfigurationContext(config);
        } else {
            config.locale = locale;
            res.updateConfiguration(config, res.getDisplayMetrics());
        }

        return new ContextWrapper(context);
    }

}
