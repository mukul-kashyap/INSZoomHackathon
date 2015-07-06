package com.zoomlee.Zoomlee.ui.view;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Point;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.zoomlee.Zoomlee.R;

/**
 * Author vbevans94.
 */
public class BottomSheet extends Dialog {

    public BottomSheet(View view) {
        super(view.getContext(), R.style.DialogCustom);

        setContentView(view);

        // get display size
        WindowManager manager = (WindowManager) getContext().getSystemService(Activity.WINDOW_SERVICE);
        Point size = new Point();
        manager.getDefaultDisplay().getSize(size);

        // make dialog snap to bottom
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        getWindow().setAttributes(lp);

        setCanceledOnTouchOutside(true);
    }
}
