package puzzles.xthn9rf;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import org.jetbrains.annotations.NotNull;


public final class TouchListener implements OnTouchListener {
    private float xDelta;
    private float yDelta;
    private final PuzzleActivity activity;

    public boolean onTouch(@NotNull View view, @NotNull MotionEvent motionEvent) {
        float x = motionEvent.getRawX();
        float y = motionEvent.getRawY();
        double tolerance = Math.sqrt(Math.pow((double)view.getWidth(), 2.0D) + Math.pow((double)view.getHeight(), 2.0D)) / (double)10;
        PuzzlePiece piece = (PuzzlePiece)view;
        if (!piece.getCanMove()) {
            return true;
        } else {
            LayoutParams layoutParams = view.getLayoutParams();
            if (layoutParams == null) {
                throw new NullPointerException("null cannot be cast to non-null type android.widget.RelativeLayout.LayoutParams");
            } else {
                android.widget.RelativeLayout.LayoutParams lParams = (android.widget.RelativeLayout.LayoutParams)layoutParams;
                switch(motionEvent.getAction() & 255) {
                    case 0:
                        this.xDelta = x - (float)lParams.leftMargin;
                        this.yDelta = y - (float)lParams.topMargin;
                        piece.bringToFront();
                        break;
                    case 1:
                        int xDiff = StrictMath.abs(piece.getXCoord() - lParams.leftMargin);
                        int yDiff = StrictMath.abs(piece.getYCoord() - lParams.topMargin);
                        if ((double)xDiff <= tolerance && (double)yDiff <= tolerance) {
                            lParams.leftMargin = piece.getXCoord();
                            lParams.topMargin = piece.getYCoord();
                            piece.setLayoutParams((LayoutParams)lParams);
                            piece.setCanMove(false);
                            this.sendViewToBack((View)piece);
                            this.activity.checkGameOver();
                        }
                        break;
                    case 2:
                        lParams.leftMargin = (int)(x - this.xDelta);
                        lParams.topMargin = (int)(y - this.yDelta);
                        view.setLayoutParams((LayoutParams)lParams);
                }

                return true;
            }
        }
    }

    public final void sendViewToBack(@NotNull View child) {
        ViewParent parentView = child.getParent();
        if (parentView == null) {
            throw new NullPointerException("null cannot be cast to non-null type android.view.ViewGroup");
        } else {
            ViewGroup parent = (ViewGroup)parentView;
            parent.removeView(child);
            parent.addView(child, 0);

        }
    }

    public TouchListener(@NotNull PuzzleActivity activity) {
        super();
        this.activity = activity;
    }
}
