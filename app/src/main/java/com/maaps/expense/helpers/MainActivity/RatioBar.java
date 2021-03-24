package com.maaps.expense.helpers.MainActivity;

import android.graphics.Point;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.maaps.expense.R;
import com.maaps.expense.helpers.ImageViewResizeAnimation;

import modelv2.Group;

public class RatioBar {

    ImageView ratioBar;

    public RatioBar(ImageView ratioBar) {
        this.ratioBar = ratioBar;
    }

    public void initialize(Group group, float payedByCurrentUser, AppCompatActivity activity) {
        Point screenSizes = new Point();
        activity.getWindowManager().getDefaultDisplay().getRealSize(screenSizes);
        float payedByGroup = group.getAbsoluteTotal();
        float ratioUserPToGroupP = payedByCurrentUser / payedByGroup;
        int ratioBarWidth = Float.valueOf(screenSizes.x * (ratioUserPToGroupP)).intValue();

        animateToWidth(ratioBarWidth);
        updateColor(ratioUserPToGroupP);


    }

    private void updateColor(float ratio) {
        final int GREEN_BAR_LIMIT_PERCENT = 66;
        final int RED_BAR_LIMIT_PERCENT = 33;

        if (ratio * 100 > GREEN_BAR_LIMIT_PERCENT) {
            ratioBar.setImageResource(R.drawable.background_accent_variant);
        } else if (ratio * 100 < RED_BAR_LIMIT_PERCENT) {
            ratioBar.setImageResource(R.drawable.background_accent);
        } else {
            ratioBar.setImageResource(R.drawable.background_teal);
        }
    }

    private void animateToWidth(int targetWidth) {
        ImageViewResizeAnimation anim = new ImageViewResizeAnimation(ratioBar,
                ratioBar.getLayoutParams().width, ratioBar.getLayoutParams().height,
                targetWidth, ratioBar.getLayoutParams().height);

        ratioBar.setAnimation(anim);
        ratioBar.getLayoutParams().width = targetWidth;
    }

}
