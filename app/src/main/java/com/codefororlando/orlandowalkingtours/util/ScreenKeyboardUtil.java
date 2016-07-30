package com.codefororlando.orlandowalkingtours.util;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class ScreenKeyboardUtil {
    public static boolean hideScreenKeyboard(Activity activity) {
        InputMethodManager imm =
                (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            return false;
        }

        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        return true;
    }
}
