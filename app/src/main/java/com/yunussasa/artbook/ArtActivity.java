package com.yunussasa.artbook;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.yunussasa.artbook.databinding.ActivityArtBinding;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class ArtActivity extends AppCompatActivity {
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionResultLauncher;
    private ActivityArtBinding binding;
    Bitmap selectedImage;
    SQLiteDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityArtBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);

        registerLauncher();
        database=this.openOrCreateDatabase("Arts",MODE_PRIVATE,null);


        Intent intent=getIntent();
        String info=intent.getStringExtra("info");

        if(info.equals("new")){
            binding.imageView.setImageResource(R.drawable.selectimage);
            binding.editArtistText.setText("");
            binding.editNameText.setText("");
            binding.editYearText.setText("");
            binding.button.setVisibility(View.VISIBLE);

        }else {
            int artId=intent.getIntExtra("artId",1);
            binding.button.setVisibility(View.INVISIBLE);

            try {
                Cursor cursor=database.rawQuery("Select * from arts where id=?",new String[] {String.valueOf(artId)});
                int artNameIndex=cursor.getColumnIndex("artname");
                int artistNameIndex=cursor.getColumnIndex("artistname");
                int yearIndex=cursor.getColumnIndex("year");
                int imageIndex=cursor.getColumnIndex("image");

                while (cursor.moveToNext()){
                    binding.editYearText.setText(cursor.getString(yearIndex));
                    binding.editNameText.setText(cursor.getString(artNameIndex));
                    binding.editArtistText.setText(cursor.getString(artistNameIndex));

                    byte[] bytes=cursor.getBlob(imageIndex);
                    Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    binding.imageView.setImageBitmap(bitmap);
                }
                cursor.close();

            }catch(Exception e){
                e.printStackTrace();
            }

        }


    }


    public void  save(View view){

        String name=binding.editNameText.getText().toString();
        String artistText=binding.editArtistText.getText().toString();
        String year=binding.editYearText.getText().toString();

        Bitmap smallImage=makeSmallerImage(selectedImage,300);

        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
        smallImage.compress(Bitmap.CompressFormat.PNG,50,outputStream);
        byte[] byteArray=outputStream.toByteArray();

        try {
            database.execSQL("Create Table if not exists arts (id INTEGER PRIMARY KEY,artname VARCHAR,artistname VARCHAR,year VARCHAR,image BLOB) ");

            String sqlString="insert into arts(artname,artistname,year,image) values (?,?,?,?)";

            SQLiteStatement sqLiteStatement=database.compileStatement(sqlString);
            sqLiteStatement.bindString(1,name);
            sqLiteStatement.bindString(2,artistText);
            sqLiteStatement.bindString(3,year);
            sqLiteStatement.bindBlob(4,byteArray);
            sqLiteStatement.execute();

        }catch (Exception e){
            e.printStackTrace();
        }

        Intent intent=new Intent(ArtActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public Bitmap makeSmallerImage(Bitmap image,int maxSize){
        int height=image.getHeight();
        int width=image.getWidth();

        float bitmapRatio=(float) width/(float) height;
        if (bitmapRatio>1){
            width=maxSize;
            height=(int)(width/bitmapRatio);
        }else{
            height=maxSize;
            width=(int)(height*bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image,width,height,true);
    }

    public void  selectImage(View view){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view,"Permission needed for gallery !", BaseTransientBottomBar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        permissionResultLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                }).show();
            }else {
                permissionResultLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }else {
            Intent intentToGallery= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intentToGallery);
        }
    }

    private void registerLauncher(){

        activityResultLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode()==RESULT_OK){
                    Intent intentFromResult=result.getData();
                    if(intentFromResult!=null){
                        Uri imageData=intentFromResult.getData();
                        //binding.imageView.setImageURI(imageData);

                        try {
                            if (Build.VERSION.SDK_INT>=28){
                                ImageDecoder.Source source=ImageDecoder.createSource(getContentResolver(),imageData);
                                selectedImage=ImageDecoder.decodeBitmap(source);
                                binding.imageView.setImageBitmap(selectedImage);
                            }else{
                                selectedImage=MediaStore.Images.Media.getBitmap(getContentResolver(),imageData);
                                binding.imageView.setImageBitmap(selectedImage);
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                }
            }
        });

        permissionResultLauncher=registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if(result){
                    Intent intentToGallery= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentToGallery);
                }else {
                    Toast.makeText(ArtActivity.this,"Permission needed !",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}