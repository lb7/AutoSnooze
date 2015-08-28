package lbaker.app.autosnooze.ui;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

import lbaker.app.autosnooze.R;

/**
 * Created by Luke on 12/29/2014.
 *
 */
public class AlarmListItemDecoration extends RecyclerView.ItemDecoration {
    private static final String LOG_TAG = "AlarmListItemDecoration";

    private Drawable divider;
    private int dividerHeightPX;

    @SuppressWarnings("deprecation")
    public AlarmListItemDecoration(Resources resources) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            divider = resources.getDrawable(R.drawable.divider, null);
        } else {
            divider = resources.getDrawable(R.drawable.divider);
        }

        dividerHeightPX = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, resources.getDisplayMetrics()) - 1;
        if (dividerHeightPX < 1) dividerHeightPX = 1;
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            int top = child.getBottom() - dividerHeightPX + (int) child.getTranslationY();

            divider.setBounds(child.getLeft(), top, child.getRight(), top + dividerHeightPX);
            divider.draw(c);
        }
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
    }
}
