package lbaker.app.autosnooze;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by Luke on 12/29/2014.
 */
public class AlarmListItemDecoration extends RecyclerView.ItemDecoration {
    private static final String LOG_TAG = "AlarmListItemDecoration";

    private Drawable divider;
    private int dividerHeightPX;

    public AlarmListItemDecoration(Resources resources) {
        divider = resources.getDrawable(R.drawable.divider);

        dividerHeightPX = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, resources.getDisplayMetrics()) - 1;
        if (dividerHeightPX < 1) dividerHeightPX = 1;
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        //Paint paint = new Paint();
        //paint.setARGB(31, 0, 0, 0);

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            divider.setBounds(child.getLeft(), child.getBottom() - dividerHeightPX, child.getRight(), child.getBottom());
            divider.draw(c);
            //c.drawLine(child.getLeft(), child.getBottom(), child.getRight(), child.getBottom(), paint);
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
