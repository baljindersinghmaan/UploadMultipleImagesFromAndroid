package com.example.rajat.multipleimageloaderdemo;
import android.app.Activity;

import com.nostra13.universalimageloader.core.ImageLoader;

public class BaseActivity extends Activity {
    protected ImageLoader imageLoader = ImageLoader.getInstance();
}
