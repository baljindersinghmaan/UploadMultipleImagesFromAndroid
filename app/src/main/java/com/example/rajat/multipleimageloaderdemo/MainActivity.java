package com.example.rajat.multipleimageloaderdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import id.zelory.compressor.Compressor;

public class MainActivity extends AppCompatActivity {

    private Button btn;
    int PICK_IMAGE_MULTIPLE = 1;
    String imageEncoded;
    List<String> imagesEncodedList;
    private RecyclerView gvGallery;
    private GalleryAdapter galleryAdapter;
    List<Uri> mArrayUri = new ArrayList<>();
    List<Bitmap> compressedBitmapList = new ArrayList<>();
    List<String> imageFilePathList = new ArrayList<>();
    private Compressor compressor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        compressor = new Compressor(MainActivity.this);

        btn = findViewById(R.id.btn1);
        gvGallery = findViewById(R.id.gv);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE);
                if (mArrayUri != null) {
                    mArrayUri.clear();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            // When an Image is picked
            if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                imagesEncodedList = new ArrayList<String>();
                if (data.getData() != null) {

                    Uri mImageUri = data.getData();
                    //bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());

                    // Get the cursor
                    Cursor cursor = getContentResolver().query(mImageUri,
                            filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imageEncoded = cursor.getString(columnIndex);
                    cursor.close();


                    mArrayUri.add(mImageUri);
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageUri);
                    String path = getRealPathFromURI(getImageUri(MainActivity.this, bitmap));
                    imageFilePathList.add(path);
                    File f = new File(path);
                    compressedBitmapList.add(new Compressor(this).compressToBitmap(f));


                    galleryAdapter = new GalleryAdapter(getApplicationContext(), imageFilePathList, new GalleryAdapter.MyListener() {
                        @Override
                        public void onClick(int p, String data) {
                            imageFilePathList.remove(data);
                            galleryAdapter.notifyItemRemoved(p);
                            //gvGallery.removeViewAt(p);
                            //galleryAdapter.notifyItemRangeChanged(p, mArrayUri.size());
                        }
                    });
                    gvGallery.setAdapter(galleryAdapter);
                    gvGallery.setLayoutManager(new GridLayoutManager(MainActivity.this, 4, GridLayoutManager.VERTICAL, false));
                    //ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) gvGallery.getLayoutParams();
                    //mlp.setMargins(0, gvGallery.getHorizontalSpacing(), 0, 0);

                } else {
                    if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();


                        if (mClipData.getItemCount() <= 8) {
                            for (int i = 0; i < mClipData.getItemCount(); i++) {

                                ClipData.Item item = mClipData.getItemAt(i);
                                Uri uri = item.getUri();


                                mArrayUri.add(uri);

                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                                String path = getRealPathFromURI(getImageUri(MainActivity.this, bitmap));
                                imageFilePathList.add(path);
                                File f = new File(path);
                                compressedBitmapList.add(new Compressor(this).compressToBitmap(f));
                                // Get the cursor
                                Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                                // Move to first row
                                cursor.moveToFirst();

                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                imageEncoded = cursor.getString(columnIndex);
                                imagesEncodedList.add(imageEncoded);
                                cursor.close();


                                //gvGallery.setVerticalSpacing(gvGallery.getHorizontalSpacing());
                                //ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) gvGallery.getLayoutParams();
                                //mlp.setMargins(0, gvGallery.getHorizontalSpacing(), 0, 0);

                            }

                            galleryAdapter = new GalleryAdapter(getApplicationContext(), imageFilePathList, new GalleryAdapter.MyListener() {
                                @Override
                                public void onClick(int p, String data) {
                                    imageFilePathList.remove(data);
                                    //galleryAdapter.notifyDataSetChanged();
                                    galleryAdapter.notifyItemRemoved(p);
                                    //gvGallery.removeViewAt(p);
                                    //galleryAdapter.notifyItemRangeChanged(p, mArrayUri.size());
                                }
                            });
                            //new Compressor(this).compressToFile(actualImageFile);
                            gvGallery.setAdapter(galleryAdapter);
                            gvGallery.setLayoutManager(new GridLayoutManager(MainActivity.this, 4, GridLayoutManager.VERTICAL, false));
                            Log.v("LOG_TAG", "Selected Images" + mArrayUri.size());
                        } else {
                            Toast.makeText(this, "You can't select images more than 8", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }
}

