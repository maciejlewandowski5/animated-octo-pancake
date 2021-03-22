package com.maaps.expense.helpers.MainActivity;

import android.app.AlertDialog;
import android.graphics.Point;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.maaps.expense.MainActivity;
import com.maaps.expense.R;
import com.maaps.expense.helpers.Utils;

import modelv2.UserSession;

public class HorizontalTabsScroller {

    private final ConstraintLayout tab1;
    private final ConstraintLayout tab2;
    private final HorizontalScrollView horizontalScrollView;


    public HorizontalTabsScroller(ConstraintLayout tab1, ConstraintLayout tab2, HorizontalScrollView horizontalScrollView) {
        this.tab1 = tab1;
        this.tab2 = tab2;
        this.horizontalScrollView = horizontalScrollView;
    }

    public void showLeaveGroupWarning(AppCompatActivity activity) {
        String buttonText = activity.getString(R.string.leave_group);
        UserSession userSession = UserSession.getInstance();
        if (userSession.amILastUser()) { // TODO:: Method to implement
            buttonText = activity.getString(R.string.leaveAndDeleteGroup);
        }


        AlertDialog alertDialog = new AlertDialog.Builder(activity).create();

        alertDialog.setTitle(activity.getString(R.string.read_this));
        alertDialog.setMessage(Utils.getLeaveGroupWarning(activity));

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, buttonText,
                (dialog, which) -> {
                    if (!userSession.amILastUser()) {
                        try {
                            userSession.leaveCurrentGroup(activity);
                        } catch (IllegalStateException tooFewGroupsToLeave) {
                            Utils.toastMessage(tooFewGroupsToLeave.getMessage(),activity);
                        }
                    } else {
                        userSession.leaveAndDeleteCurrentGroup(activity);
                    }
                });

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, activity.getString(R.string.cancel),
                (dialog, which) -> {
                    //closes dialog
                });
        alertDialog.show();
    }

    public void initializeScrollTabs(AppCompatActivity app,ImageView[] pageIndicators) {

        Point screenSizes = new Point();
        app.getWindowManager().getDefaultDisplay().getSize(screenSizes);
        int screenWidth = screenSizes.x;
        int tab2Width = screenWidth / 3;

        ViewGroup.LayoutParams layoutParams = tab1.getLayoutParams();
        layoutParams.width = screenWidth;
        tab1.setLayoutParams(layoutParams);

        layoutParams = tab2.getLayoutParams();
        layoutParams.width = tab2Width;
        tab2.setLayoutParams(layoutParams);

        setHorizontalScrollingLogic(tab2Width,pageIndicators);

    }

    private void setHorizontalScrollingLogic(int tab2Width,ImageView[] pageIndicators) {

        final boolean[] tab2IsVisible = {false};


        final int TAB2_VISIBLE_ACKNOWLEDGMENT_PERCENT = 98;
        final int TAB2_INVISIBLE_ACKNOWLEDGMENT_PERCENT = 2;
        final int TAB2_PULL_START_LIMIT_PERCENT = 27;
        final int TAB1_PULL_START_LIMIT_PERCENT = 73;


        horizontalScrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {

            float scrolledPercent = horizontalScrollView.getScrollX() / (float) (tab2Width) * 100;

            if (!tab2IsVisible[0]) {
                if (scrolledPercent >= TAB2_VISIBLE_ACKNOWLEDGMENT_PERCENT) {
                    tab2IsVisible[0] = true;
                    setTabTwoIndicatorsVisible(pageIndicators);
                }
                if (scrolledPercent >= TAB2_PULL_START_LIMIT_PERCENT) {
                    horizontalScrollView.smoothScrollTo(horizontalScrollView.getRight(), 0);
                }
            } else {
                if (scrolledPercent <= TAB2_INVISIBLE_ACKNOWLEDGMENT_PERCENT) {
                    tab2IsVisible[0] = false;
                    setTabOneIndicatorsVisible(pageIndicators);
                }

                if (scrolledPercent <= TAB1_PULL_START_LIMIT_PERCENT) {
                    horizontalScrollView.smoothScrollTo(horizontalScrollView.getLeft(), 0);
                }
            }
        });
    }

    private void setTabTwoIndicatorsVisible(ImageView[] pageIndicators) {
        pageIndicators[0].setImageResource(R.drawable.circle_dark_grey);
        pageIndicators[1].setImageResource(R.drawable.circle_grey);
    }

    private void setTabOneIndicatorsVisible(ImageView[] pageIndicators) {
        pageIndicators[0].setImageResource(R.drawable.circle_grey);
        pageIndicators[1].setImageResource(R.drawable.circle_dark_grey);
    }

    public void scrollToTabOne() {
        horizontalScrollView.post(() -> horizontalScrollView.smoothScrollTo(0, 0));
    }

}
