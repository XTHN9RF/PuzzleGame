package puzzles.xthn9rf;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Xfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;

import org.jetbrains.annotations.Nullable;


public class PuzzleActivity extends AppCompatActivity {
    private ArrayList<PuzzlePiece> pieces;
    @Nullable
    private String mCurrentPhotoPath;
    @Nullable
    private String mCurrentPhotoUri;

    public final ArrayList<PuzzlePiece> getPieces() {
        return this.pieces;
    }

    public final void setPieces(ArrayList<PuzzlePiece> puzzlePieces) {
        this.pieces = puzzlePieces;
    }

    @Nullable
    public final String getMCurrentPhotoPath() {
        return this.mCurrentPhotoPath;
    }

    public final void setMCurrentPhotoPath(@Nullable String path_to_photo) {
        this.mCurrentPhotoPath = path_to_photo;
    }

    @Nullable
    public final String getMCurrentPhotoUri() {
        return this.mCurrentPhotoUri;
    }

    public boolean isLost;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_puzzle);
        final RelativeLayout layout = (RelativeLayout) this.findViewById(R.id.layout);
        final ImageView imageView = (ImageView) this.findViewById(R.id.picImageView);
        Intent intent = this.getIntent();
        final String assetName = intent.getStringExtra("assetName");
        this.mCurrentPhotoPath = intent.getStringExtra("mCurrentPhotoPath");
        this.mCurrentPhotoUri = intent.getStringExtra("mCurrentPhotoUri");
        TextView timerText = findViewById(R.id.timerTextView);
        GameTimer gameTimer = new GameTimer(timerText);
        gameTimer.resetTimer();
        gameTimer.startTimer();
        imageView.post((Runnable) (() -> {
            PuzzleActivity puzzleActivity;
            String photo_path;
            ImageView imageView1;
            if (assetName != null) {
                puzzleActivity = PuzzleActivity.this;
                photo_path = assetName;
                imageView1 = imageView;
                puzzleActivity.setPicFromAsset(photo_path, imageView1);
            } else if (PuzzleActivity.this.getMCurrentPhotoPath() != null) {
                puzzleActivity = PuzzleActivity.this;
                photo_path = PuzzleActivity.this.getMCurrentPhotoPath();
                imageView1 = imageView;
                puzzleActivity.setPicFromPath(photo_path, imageView1);
            } else if (PuzzleActivity.this.getMCurrentPhotoUri() != null) {
                imageView.setImageURI(Uri.parse(PuzzleActivity.this.getMCurrentPhotoUri()));
            }

            PuzzleActivity.this.setPieces(PuzzleActivity.this.splitImage());
            TouchListener touchListener = new TouchListener(PuzzleActivity.this);
            Collections.shuffle(PuzzleActivity.this.getPieces());
            ArrayList<PuzzlePiece> puzzlePieces = PuzzleActivity.this.getPieces();
            Iterator<PuzzlePiece> pieceIterator = puzzlePieces.iterator();

            while (pieceIterator.hasNext()) {
                PuzzlePiece piece = pieceIterator.next();
                piece.setOnTouchListener((View.OnTouchListener) touchListener);
                layout.addView((View) piece);
                ViewGroup.LayoutParams layoutParams = piece.getLayoutParams();
                if (layoutParams == null) {
                    throw new NullPointerException("null cannot be cast to non-null type android.widget.RelativeLayout.LayoutParams");
                }

                RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) layoutParams;
                Random random = new Random();
                RelativeLayout relativeLayout = layout;
                lParams.leftMargin = random.nextInt(relativeLayout.getWidth() - piece.getPieceWidth());
                RelativeLayout relativeLayout2 = layout;
                lParams.topMargin = relativeLayout2.getHeight() - piece.getPieceHeight();
                piece.setLayoutParams((ViewGroup.LayoutParams) lParams);
            }

        }));
    }

    @SuppressLint("WrongConstant")
    private void setPicFromAsset(String assetName, ImageView imageView) {
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();
        AssetManager am = this.getAssets();

        try {
            InputStream open_file = am.open("img/" + assetName);
            InputStream input_stream = open_file;
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(input_stream, new Rect(-1, -1, -1, -1), bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;
            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
            input_stream.reset();
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;
            Bitmap bitmap = BitmapFactory.decodeStream(input_stream, new Rect(-1, -1, -1, -1), bmOptions);
            imageView.setImageBitmap(bitmap);
        } catch (IOException ioException) {
            ioException.printStackTrace();
            Toast.makeText((Context) this, (CharSequence) ioException.getLocalizedMessage(), 0).show();
        }

    }

    private ArrayList<PuzzlePiece> splitImage() {
        int piecesNumber = 12;
        int rows = 4;
        int cols = 3;
        ImageView imageView = (ImageView) this.findViewById(R.id.picImageView);
        ArrayList<PuzzlePiece> pieces = new ArrayList<>(piecesNumber);
        Drawable imageViewDrawable = imageView.getDrawable();
        if (imageViewDrawable == null) {
            throw new NullPointerException("null cannot be cast to non-null type android.graphics.drawable.BitmapDrawable");
        } else {
            BitmapDrawable drawable = (BitmapDrawable) imageViewDrawable;
            Bitmap bitmap = drawable.getBitmap();
            int[] dimensions = this.getBitmapPositionInsideImageView(imageView);
            int scaledBitmapLeft = dimensions[0];
            int scaledBitmapTop = dimensions[1];
            int scaledBitmapWidth = dimensions[2];
            int scaledBitmapHeight = dimensions[3];
            int croppedImageWidth = scaledBitmapWidth - 2 * Math.abs(scaledBitmapLeft);
            int croppedImageHeight = scaledBitmapHeight - 2 * Math.abs(scaledBitmapTop);
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaledBitmapWidth, scaledBitmapHeight, true);
            Bitmap croppedBitmap = Bitmap.createBitmap(scaledBitmap, Math.abs(scaledBitmapLeft), Math.abs(scaledBitmapTop), croppedImageWidth, croppedImageHeight);
            int pieceWidth = croppedImageWidth / cols;
            int pieceHeight = croppedImageHeight / rows;
            int yCoord = 0;
            int row = 0;

            for (byte rows_cnt = (byte) rows; row < rows_cnt; ++row) {
                int xCoord = 0;
                int col = 0;

                for (byte cols_cnt = (byte) cols; col < cols_cnt; ++col) {
                    int offsetX = 0;
                    int offsetY = 0;
                    if (col > 0) {
                        offsetX = pieceWidth / 3;
                    }

                    if (row > 0) {
                        offsetY = pieceHeight / 3;
                    }

                    Bitmap pieceBitmap = Bitmap.createBitmap(croppedBitmap, xCoord - offsetX, yCoord - offsetY, pieceWidth + offsetX, pieceHeight + offsetY);
                    PuzzlePiece piece = new PuzzlePiece(this.getApplicationContext());
                    piece.setImageBitmap(pieceBitmap);
                    piece.setXCoord(xCoord - offsetX + imageView.getLeft());
                    piece.setYCoord(yCoord - offsetY + imageView.getTop());
                    piece.setPieceWidth(pieceWidth + offsetX);
                    piece.setPieceHeight(pieceHeight + offsetY);
                    Bitmap puzzlePiece = Bitmap.createBitmap(pieceWidth + offsetX, pieceHeight + offsetY, Bitmap.Config.ARGB_8888);
                    int bumpSize = pieceHeight / 4;
                    Canvas canvas = new Canvas(puzzlePiece);
                    Path path = new Path();
                    path.moveTo((float) offsetX, (float) offsetY);
                    if (row == 0) {

                        path.lineTo((float) pieceBitmap.getWidth(), (float) offsetY);
                    } else {

                        path.lineTo((float) (offsetX + (pieceBitmap.getWidth() - offsetX) / 3), (float) offsetY);
                        path.cubicTo((float) (offsetX + (pieceBitmap.getWidth() - offsetX) / 6), (float) (offsetY - bumpSize), (float) (offsetX + (pieceBitmap.getWidth() - offsetX) / 6 * 5), (float) (offsetY - bumpSize), (float) (offsetX + (pieceBitmap.getWidth() - offsetX) / 3 * 2), (float) offsetY);
                        path.lineTo((float) pieceBitmap.getWidth(), (float) offsetY);
                    }

                    if (col == cols - 1) {
                        path.lineTo((float) pieceBitmap.getWidth(), (float) pieceBitmap.getHeight());
                    } else {
                        path.lineTo((float) pieceBitmap.getWidth(), (float) (offsetY + (pieceBitmap.getHeight() - offsetY) / 3));
                        path.cubicTo((float) (pieceBitmap.getWidth() - bumpSize), (float) (offsetY + (pieceBitmap.getHeight() - offsetY) / 6), (float) (pieceBitmap.getWidth() - bumpSize), (float) (offsetY + (pieceBitmap.getHeight() - offsetY) / 6 * 5), (float) pieceBitmap.getWidth(), (float) (offsetY + (pieceBitmap.getHeight() - offsetY) / 3 * 2));
                        path.lineTo((float) pieceBitmap.getWidth(), (float) pieceBitmap.getHeight());
                    }

                    if (row == rows - 1) {
                        path.lineTo((float) offsetX, (float) pieceBitmap.getHeight());
                    } else {
                        path.lineTo((float) (offsetX + (pieceBitmap.getWidth() - offsetX) / 3 * 2), (float) pieceBitmap.getHeight());
                        path.cubicTo((float) (offsetX + (pieceBitmap.getWidth() - offsetX) / 6 * 5), (float) (pieceBitmap.getHeight() - bumpSize), (float) (offsetX + (pieceBitmap.getWidth() - offsetX) / 6), (float) (pieceBitmap.getHeight() - bumpSize), (float) (offsetX + (pieceBitmap.getWidth() - offsetX) / 3), (float) pieceBitmap.getHeight());
                        path.lineTo((float) offsetX, (float) pieceBitmap.getHeight());
                    }

                    if (col == 0) {
                        path.close();
                    } else {
                        path.lineTo((float) offsetX, (float) (offsetY + (pieceBitmap.getHeight() - offsetY) / 3 * 2));
                        path.cubicTo((float) (offsetX - bumpSize), (float) (offsetY + (pieceBitmap.getHeight() - offsetY) / 6 * 5), (float) (offsetX - bumpSize), (float) (offsetY + (pieceBitmap.getHeight() - offsetY) / 6), (float) offsetX, (float) (offsetY + (pieceBitmap.getHeight() - offsetY) / 3));
                        path.close();
                    }

                    Paint paint = new Paint();
                    paint.setColor(-16777216);
                    paint.setStyle(Paint.Style.FILL);
                    canvas.drawPath(path, paint);
                    paint.setXfermode((Xfermode) (new PorterDuffXfermode(PorterDuff.Mode.SRC_IN)));
                    canvas.drawBitmap(pieceBitmap, 0.0F, 0.0F, paint);
                    Paint border = new Paint();
                    border.setColor(-2130706433);
                    border.setStyle(Paint.Style.STROKE);
                    border.setStrokeWidth(8.0F);
                    canvas.drawPath(path, border);
                    border = new Paint();
                    border.setColor(Integer.MIN_VALUE);
                    border.setStyle(Paint.Style.STROKE);
                    border.setStrokeWidth(3.0F);
                    canvas.drawPath(path, border);
                    piece.setImageBitmap(puzzlePiece);
                    pieces.add(piece);
                    xCoord += pieceWidth;
                }

                yCoord += pieceHeight;
            }

            return pieces;
        }
    }

    private int[] getBitmapPositionInsideImageView(ImageView imageView) {
        int[] position = new int[4];
        if (imageView != null && imageView.getDrawable() != null) {
            float[] f = new float[9];
            imageView.getImageMatrix().getValues(f);
            float scaleX = f[0];
            float scaleY = f[4];
            Drawable d = imageView.getDrawable();
            int origW = d.getIntrinsicWidth();
            int origH = d.getIntrinsicHeight();
            int actW = Math.round((float) origW * scaleX);
            int actH = Math.round((float) origH * scaleY);
            position[2] = actW;
            position[3] = actH;
            int imgViewW = imageView.getWidth();
            int imgViewH = imageView.getHeight();
            int top = (imgViewH - actH) / 2;
            int left = (imgViewW - actW) / 2;
            position[0] = left;
            position[1] = top;
            return position;
        } else {
            return position;
        }
    }

    public void checkGameOver() {
        if (GameTimer.finished){
            (new AlertDialog.Builder((Context) this)).setTitle((CharSequence) "You lost").setIcon(R.drawable.loose_ic).setMessage((CharSequence) "Do you want to try again?").setPositiveButton((CharSequence) "Yes", (DialogInterface.OnClickListener) ((dialog, $noName_1) -> {
                PuzzleActivity.this.finish();
                dialog.dismiss();
            })).setNegativeButton((CharSequence) "No", (DialogInterface.OnClickListener) ((dialog, $noName_1) -> {
                PuzzleActivity.this.finish();
                dialog.dismiss();
            })).create().show();
        }
        else if (isPlayerWon()){
            (new AlertDialog.Builder((Context) this)).setTitle((CharSequence) "You won .. !!").setIcon(R.drawable.ic_celebration).setMessage((CharSequence) "Do you want a New Game?").setPositiveButton((CharSequence) "Yes", (DialogInterface.OnClickListener) ((dialog, $noName_1) -> {
                PuzzleActivity.this.finish();
                dialog.dismiss();
            })).setNegativeButton((CharSequence) "No", (DialogInterface.OnClickListener) ((dialog, $noName_1) -> {
                PuzzleActivity.this.finish();
                dialog.dismiss();
            })).create().show();
        }


    }

    private boolean isPlayerWon() {
        ArrayList<PuzzlePiece> puzzlePieces = this.pieces;
        Iterator<PuzzlePiece> pieceIterator = puzzlePieces.iterator();
        PuzzlePiece piece;
        do {
            if (!pieceIterator.hasNext()){
                return true;
            }
            piece = pieceIterator.next();
        } while (!piece.getCanMove());
        return false;
    }

    @SuppressLint("WrongConstant")
    private void setPicFromPath(String mCurrentPhotoPath, ImageView imageView) {
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        Bitmap rotatedBitmap = bitmap;

        try {
            ExifInterface ei = new ExifInterface(mCurrentPhotoPath);
            int orientation = ei.getAttributeInt("Orientation", 0);
            switch (orientation) {
                case 3:
                    rotatedBitmap = rotateImage(bitmap, 180.0F);
                case 4:
                case 5:
                case 7:
                default:
                    break;
                case 6:
                    rotatedBitmap = rotateImage(bitmap, 90.0F);
                    break;
                case 8:
                    rotatedBitmap = rotateImage(bitmap, 270.0F);
            }
        } catch (IOException ioException) {
            Toast.makeText((Context) this, (CharSequence) ioException.getLocalizedMessage(), 0).show();
        }

        imageView.setImageBitmap(rotatedBitmap);
    }

    private Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        Bitmap rotated = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
        return rotated;
    }
}