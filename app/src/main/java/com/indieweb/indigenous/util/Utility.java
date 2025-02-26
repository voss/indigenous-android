package com.indieweb.indigenous.util;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Utility {

    public static List<String> dateFormatStrings = Arrays.asList("yyyy-MM-dd'T'HH:mm:ssZ", "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd HH:mm:ssZ", "yyyy-MM-dd HH:mmZ");

    /**
     * Copy to clipboard.
     *
     * @param copyText
     *   The text to copy to clipboard.
     * @param label
     *   The clipboard label
     * @param context
     *   The current context.
     */
    @SuppressWarnings("deprecation")
    public static void copyToClipboard(String copyText, String label, Context context) {
        int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboard != null) {
                clipboard.setText(copyText);
            }
        }
        else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText(label, copyText);
            if (clipboard != null) {
                clipboard.setPrimaryClip(clip);
            }
        }
    }

    /**
     * Trim a char sequence.
     *
     * @param text
     *   The text to trim.
     *
     * @return text
     */
    public static CharSequence trim(CharSequence text) {
        try {
            if (text.length() > 0) {
                while (text.charAt(text.length() - 1) == '\n') {
                    text = text.subSequence(0, text.length() - 1);
                }
            }
        }
        catch (Exception ignored) {}

        return text;
    }

    /**
     * Shows a DateTimePicker dialog.
     */
    public static void showDateTimePickerDialog(final Context context, final TextView t) {
        final Date[] value = {new Date()};
        final Calendar cal = Calendar.getInstance();
        cal.setTime(value[0]);
        new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {
                    @Override public void onDateSet(DatePicker view, int y, int m, int d) {
                        cal.set(Calendar.YEAR, y);
                        cal.set(Calendar.MONTH, m);
                        cal.set(Calendar.DAY_OF_MONTH, d);

                        // now show the time picker
                        new TimePickerDialog(context,
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override public void onTimeSet(TimePicker view, int h, int min) {
                                        cal.set(Calendar.HOUR_OF_DAY, h);
                                        cal.set(Calendar.MINUTE, min);
                                        value[0] = cal.getTime();

                                        @SuppressLint("SimpleDateFormat")
                                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:00Z");
                                        String result;
                                        try {
                                            result = df.format(value[0]);
                                            t.setText(result);
                                        } catch (Exception ignored) { }

                                    }
                                }, cal.get(Calendar.HOUR_OF_DAY),
                                cal.get(Calendar.MINUTE), true).show();
                    }
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    /**
     * Make sure url is an absolute URL.
     *
     * @param url
     *   The url to test.
     * @param domain
     *   The domain to prefix the url with.
     *
     * @return string
     */
    public static String checkAbsoluteUrl(String url, String domain) {
        String returnUrl;

        if (!url.startsWith("http://") && !url.startsWith("https://")) {

            try {
                URL baseUrl = new URL(domain);
                URI uri = baseUrl.toURI();
                URI newUri = uri.resolve(domain + "/" + url);
                returnUrl = newUri.normalize().toURL().toString();
            }
            catch (MalformedURLException | URISyntaxException e) {
                // This shouldn't happen. We concatenate although it will still likely fail.
                returnUrl = domain + url;
            }

        }
        else {
            returnUrl = url;
        }

        return returnUrl;
    }

}
