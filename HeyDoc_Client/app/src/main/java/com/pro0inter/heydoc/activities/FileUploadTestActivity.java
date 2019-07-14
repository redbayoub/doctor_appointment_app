package com.pro0inter.heydoc.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.pro0inter.heydoc.R;
import com.pro0inter.heydoc.api.ServiceGenerator;
import com.pro0inter.heydoc.api.Services.FileUploadService;
import com.pro0inter.heydoc.utils.ImageFilePath;
import com.squareup.picasso.Picasso;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FileUploadTestActivity extends AppCompatActivity {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final int IMAGE_PICK_REQUST_CODE = 100;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    ImageView fileImageView;
    ImageView uploadedImageView;
    String imagePath;

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, PERMISSIONS_STORAGE[1]);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_upload_test);

        verifyStoragePermissions(this);

        fileImageView = (ImageView) findViewById(R.id.FileUpload_imageView);
        uploadedImageView = (ImageView) findViewById(R.id.FileUpload_uploadedImageView);
        Button button = (Button) findViewById(R.id.FileUpload_upload_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imagePath != null)
                    uploadImage();
                else
                    Toast.makeText(getApplicationContext(), "Plz Pick Image", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void uploadImage() {
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("loading...");
        progressDialog.show();

        FileUploadService fileUploadService = ServiceGenerator.createService(FileUploadService.class);


        File file = new File(imagePath);

        //creating request body for file
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("uploaded_file", file.getName(), requestFile);


        Call<String> request = fileUploadService.uploadFile(body);
        progressDialog.show();
        request.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                    Toast.makeText(getApplicationContext(), response.body(), Toast.LENGTH_LONG).show();
                    Uri image_uri=Uri.parse(ServiceGenerator.getFileUrl(response.body()));
                    Log.d("FileUpload",image_uri.toString());
                    Picasso.get()
                            .load(image_uri)
                            .placeholder(R.drawable.ic_doctor_avatar_placeholder_100dp)
                            .error(R.drawable.ic_error_black_100dp)
                            .into(uploadedImageView);






                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("FileUpload",t.toString());
                progressDialog.dismiss();
            }
        });


    }

    public void showImagePopup(View view) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, IMAGE_PICK_REQUST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // When an Image is picked
        if (requestCode == IMAGE_PICK_REQUST_CODE && resultCode == RESULT_OK && null != data) {
            // Get the Image from data
            Uri selectedImage = data.getData();
            imagePath = ImageFilePath.getPath(this, selectedImage);

            fileImageView.setImageBitmap(BitmapFactory.decodeFile(imagePath));

        }

    }


}
