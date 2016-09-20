package com.codefororlando.orlandowalkingtours.util;

import android.view.View;
import android.view.ViewGroup;

public class ViewUtil {
    public static void enable(View view, boolean enabled) {
        view.setEnabled(enabled);
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0, count = viewGroup.getChildCount(); i < count; i++) {
                enable(viewGroup.getChildAt(i), enabled);
            }
        }
    }
}
