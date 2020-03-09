package com.example.ifestexplore.utils.camera;

import androidx.appcompat.app.AppCompatActivity;
import com.example.ifestexplore.R;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GetImage extends AppCompatActivity {

    private MaterialButton button_cancel, button_ok;
    private ImageView imageView;
    String fileDir = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_image);

        imageView = findViewById(R.id.preview_image);

        fileDir = getIntent().getStringExtra("filedir");

//        Toast.makeText(this, "received dir: "+ fileDir, Toast.LENGTH_SHORT).show();
        Bitmap bitmap = BitmapFactory.decodeFile(fileDir);
//        imageView.setImageBitmap(bitmap);

//        Working with Exif
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(fileDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

//        Matrix matrix = new Matrix();
//        switch (orientation) {
//            case ExifInterface.ORIENTATION_ROTATE_90:
//                matrix.postRotate(90);
//                Log.d("demo", "Rotation: "+orientation+" 90");
//                break;
//            case ExifInterface.ORIENTATION_ROTATE_180:
//                matrix.postRotate(180);
//                Log.d("demo", "Rotation: "+orientation+" 180");
//                break;
//            case ExifInterface.ORIENTATION_ROTATE_270:
//                matrix.postRotate(270);
//                Log.d("demo", "Rotation: "+orientation+" 270");
//                break;
//            default:
//                Log.d("demo", "Rotation: "+orientation+" 0");
//                break;
//        }

        float rotation = getRotation(orientation);

        bitmap = rotateBitmap(bitmap, rotation);

        String filepath = replaceImageFile(bitmap);

        imageView.setImageBitmap(bitmap);

        button_cancel = findViewById(R.id.materialButton_retry);
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
//                startActivity(new Intent(GetImage.this, TakePhoto.class));
                finish();
            }
        });

        button_ok = findViewById(R.id.materialButton_ok);
        final Bitmap finalBitmap = bitmap;
        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("filepath", filepath);
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });
    }

    private float getRotation(int orientation) {
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return 90;
            case ExifInterface.ORIENTATION_ROTATE_180:
                return 180;
            case ExifInterface.ORIENTATION_ROTATE_270:
                return 270;
            default:
                return 0;
        }
    }

    public Bitmap rotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    private String replaceImageFile(Bitmap bitmap) {
        // Create an image file name
        String currentPhotoPath = fileDir;
        try {
            FileOutputStream out = new FileOutputStream(currentPhotoPath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return currentPhotoPath;
    }
}
