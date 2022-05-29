package puzzles.xthn9rf;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity {
    private String mCurrentPhotoPath;

    @SuppressLint("WrongConstant")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);
        AssetManager am = getAssets();
        try {
            String[] files = am.list("D:\\A_Labs\\PuzzleGame\\app\\src\\main\\assets");
            GridView grid = findViewById(R.id.grid);
            grid.setAdapter(new ImageAdapter(this));
            grid.setOnItemClickListener(((adapterView, view, i, l) -> {
                Intent intent = new Intent(MainActivity.this.getApplicationContext(), PuzzleActivity.class);
                String[] photos = files;
                intent.putExtra("assetName", photos[i % files.length]);
                MainActivity.this.startActivity(intent);
            }));
        } catch (IOException ioException) {
            Toast.makeText((Context)this, (CharSequence)ioException.getLocalizedMessage(), 0).show();
        }

    }

    @SuppressLint("WrongConstant")
    public void onImageFromCameraClick(View view) {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        if (intent.resolveActivity(this.getPackageManager()) != null) {
            File photoFile = (File)null;

            try {
                photoFile = this.createImageFile();
            } catch (IOException ioException) {
                Toast.makeText((Context)this, (CharSequence)ioException.getMessage(), 1).show();
            }

            if (photoFile != null) {
                Context context = (Context)this;
                StringBuilder stringBuilder = new StringBuilder();
                Context applicationContext = this.getApplicationContext();
                Uri photoUri = FileProvider.getUriForFile(context, stringBuilder.append(applicationContext.getPackageName()).append(".fileprovider").toString(), photoFile);
                intent.putExtra("output", (Parcelable)photoUri);
                this.startActivityForResult(intent, 1);
            }
        }

    }

    private File createImageFile() throws IOException {
        if (ContextCompat.checkSelfPermission((Context)this, "android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
            ActivityCompat.requestPermissions((Activity)this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 2);
            return null;
        } else {
            String timeStamp = (new SimpleDateFormat("yyyyMMdd_HHmmss")).format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File image = File.createTempFile(imageFileName, ".jpg", storageDir);
            this.mCurrentPhotoPath = image.getAbsolutePath();
            return image;
        }
    }

    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 2) {
            if (grantResults.length > 0 && grantResults[0] == 0) {
                this.onImageFromCameraClick(new View((Context) this));
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == -1) {
            Intent intent = new Intent((Context)this, PuzzleActivity.class);
            intent.putExtra("mCurrentPhotoPath", this.mCurrentPhotoPath);
            this.startActivity(intent);
        }

        if (requestCode == 4 && resultCode == -1) {

            Uri uri = data.getData();
            Intent intent = new Intent((Context)this, PuzzleActivity.class);
            intent.putExtra("mCurrentPhotoUri", String.valueOf(uri));
            this.startActivity(intent);
        }

    }

    public void onImageFromGalleryClick(View view) {
        if (ContextCompat.checkSelfPermission((Context)this, "android.permission.READ_EXTERNAL_STORAGE") != 0) {
            ActivityCompat.requestPermissions((Activity)this, new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 3);
        } else {
            Intent intent = new Intent("android.intent.action.GET_CONTENT");
            intent.setType("image/*");
            this.startActivityForResult(intent, 4);
        }

    }
    public void setMiddleDuration(View view)
    {
        GameTimer.prevChoise = GameTimer.MIDDLE_DIFFICULT;
        GameTimer.duration = GameTimer.MIDDLE_DIFFICULT;
    }

    public void setHardDuration(View view)
    {
        GameTimer.prevChoise = GameTimer.HARD_DIFFICULT;
        GameTimer.duration = GameTimer.HARD_DIFFICULT;
    }

    public void setInsaneDuration(View view)
    {
        GameTimer.prevChoise = GameTimer.INSANE_DIFFICULT;
        GameTimer.duration = GameTimer.INSANE_DIFFICULT;
    }

    public void onSettingsClick(View view) {
        Button hardBtn = findViewById(R.id.HardDif);
        Button middleBtn = findViewById(R.id.MiddleDif);
        Button insaneBtn = findViewById(R.id.InsaneDif);
        if (hardBtn.getVisibility() == View.VISIBLE) {
            hardBtn.setVisibility(View.INVISIBLE);
            middleBtn.setVisibility(View.INVISIBLE);
            insaneBtn.setVisibility(View.INVISIBLE);
        } else {
            hardBtn.setVisibility(View.VISIBLE);
            middleBtn.setVisibility(View.VISIBLE);
            insaneBtn.setVisibility(View.VISIBLE);
        }
    }

}
