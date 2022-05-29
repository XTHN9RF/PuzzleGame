package puzzles.xthn9rf;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.BitmapFactory.Options;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ImageAdapter extends BaseAdapter {
    public AssetManager am;
    public String[] files;
    public Context mContext;

    public int getCount() {
        String[] photos = files;
        return photos.length;
    }

    @Nullable
    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0L;
    }

    @NonNull
    public View getView(int position, @Nullable View convertView, @NotNull ViewGroup parent) {
        @SuppressLint("ViewHolder") View v = LayoutInflater.from(this.mContext).inflate(R.layout.grid_element, null);
        ImageView imageView = v.findViewById(R.id.picImageView);
        Runnable ImageRunnable = new Runnable() {
            @SuppressWarnings("unchecked")
            @Override
            public final void run() {
                @SuppressLint("StaticFieldLeak") AsyncTask execute = new AsyncTask(){
                    public Bitmap bitmap;
                    protected Object doInBackground(@NotNull Void... p0) {
                        ImageAdapter picFromAsset = ImageAdapter.this;
                        ImageView imageView1 = imageView;
                        String[] photos = files;
                        bitmap = picFromAsset.getPicFromAsset(imageView1, photos[position]);
                        return null;
                    }
                    public Object doInBackground(Object[] photo_obj) {
                        return this.doInBackground((Void[]) photo_obj);
                    }
                    protected void onPostExecute(@Nullable Void aVoid) {
                        super.onPostExecute(aVoid);
                        imageView.setImageBitmap(bitmap);
                    }
                    public void onPostExecute(Object photo_result) {
                        this.onPostExecute((Void) photo_result);
                    }
                }.execute(new Void[0]);
            }
        };
        imageView.post(ImageRunnable);
        return v;
    }

    private Bitmap getPicFromAsset(ImageView imageView, String assetName) {
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();
        Bitmap image;
        if (targetW != 0 && targetH != 0) {
            Bitmap cur_image;
            try {
                InputStream open_file = am.open(".img/" +assetName);
                Options bmOptions = new Options();
                bmOptions.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(open_file, new Rect(-1, -1, -1, -1), bmOptions);
                int photoW = bmOptions.outWidth;
                int photoH = bmOptions.outHeight;
                int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
                open_file.reset();
                bmOptions.inJustDecodeBounds = false;
                bmOptions.inSampleSize = scaleFactor;
                bmOptions.inPurgeable = true;
                cur_image = BitmapFactory.decodeStream(open_file, new Rect(-1, -1, -1, -1), bmOptions);
            } catch (IOException ioException) {
                ioException.printStackTrace();
                cur_image = null;
            }
            image = cur_image;
        } else {
            image = null;
        }
        return image;
    }

    public ImageAdapter(@NotNull Context mContext) {
        super();
        this.mContext = mContext;
        AssetManager assetManager = mContext.getAssets();
        this.am = assetManager;

        try {
            files = assetManager.list("D:\\A_Labs\\PuzzleGame\\app\\src\\main\\assets\\img");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

    }

}