package puzzles.xthn9rf;

import android.content.Context;
import androidx.appcompat.widget.AppCompatImageView;
import org.jetbrains.annotations.Nullable;

public final class PuzzlePiece extends AppCompatImageView {
    private int xCoord;
    private int yCoord;
    private int pieceWidth;
    private int pieceHeight;
    private boolean canMove;

    public final int getXCoord() {
        return this.xCoord;
    }

    public final void setXCoord(int var1) {
        this.xCoord = var1;
    }

    public final int getYCoord() {
        return this.yCoord;
    }

    public final void setYCoord(int var1) {
        this.yCoord = var1;
    }

    public final int getPieceWidth() {
        return this.pieceWidth;
    }

    public final void setPieceWidth(int var1) {
        this.pieceWidth = var1;
    }

    public final int getPieceHeight() {
        return this.pieceHeight;
    }

    public final void setPieceHeight(int var1) {
        this.pieceHeight = var1;
    }

    public final boolean getCanMove() {
        return this.canMove;
    }

    public final void setCanMove(boolean var1) {
        this.canMove = var1;
    }

    public PuzzlePiece(@Nullable Context context) {
        super(context);
        this.canMove = true;
    }
}
