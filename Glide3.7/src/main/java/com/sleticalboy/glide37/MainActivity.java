package com.sleticalboy.glide37;

import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BaseTarget;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.target.ViewTarget;
import com.sleticalboy.glide37.custom.DownloadImageTarget;
import com.sleticalboy.glide37.custom.okhttp.ProgressInterceptor;
import com.sleticalboy.glide37.listener.ProgressListener;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.GrayscaleTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * @author leebin
 */
public class MainActivity extends AppCompatActivity {

    private ImageView ivImage;
    private LinearLayout llRootView;
    private ImageView ivOri;
    private ImageView ivTransform;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        OkHttpClient.Builder builder = new OkHttpClient.Builder();
//        builder.addInterceptor(new ProgressInterceptor());
//        final OkHttpClient client = builder.build();
//        Glide.get(getApplicationContext())
//                .register(GlideUrl.class, InputStream.class, new OkHttpModelLoader.Factory(client));

        llRootView = findViewById(R.id.ll_root_view);
        ivImage = findViewById(R.id.iv_image);
        ivOri = findViewById(R.id.iv_ori);
        ivTransform = findViewById(R.id.iv_transform);

        findViewById(R.id.btn_show_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadImage();
            }
        });
        findViewById(R.id.btn_show_gif).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadGif();
            }
        });
        findViewById(R.id.btn_load_target).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadTarget();
            }
        });

        Glide.with(getApplicationContext())
                .load(Constants.PRELOAD_URL)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .preload();
        findViewById(R.id.btn_preload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preloadImage();
            }
        });

        findViewById(R.id.btn_download_only).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadOnly();
            }
        });
        findViewById(R.id.btn_transform).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transform();
            }
        });

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setMessage("加载中...");
        findViewById(R.id.btn_progress).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressLoad();
            }
        });
    }

    private void progressLoad() {
        ProgressInterceptor.addListener(Constants.BOOK_URL, new ProgressListener() {
            @Override
            public void onProgress(int progress) {
                mProgressDialog.setProgress(progress);
            }
        });
        Glide.with(getApplicationContext())
                .load(Constants.BOOK_URL)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .into(new GlideDrawableImageViewTarget(ivImage) {
                    @Override
                    public void onLoadStarted(Drawable placeholder) {
                        super.onLoadStarted(placeholder);
                        mProgressDialog.show();
                    }

                    @Override
                    public void onResourceReady(GlideDrawable resource,
                                                GlideAnimation<? super GlideDrawable> animation) {
                        super.onResourceReady(resource, animation);
                        mProgressDialog.dismiss();
                        ProgressInterceptor.removeListener(Constants.BOOK_URL);
                    }
                });
    }

    private void transform() {
        Glide.with(getApplicationContext())
                .load(Constants.BAIDU_LOGO)
                .dontTransform()
                .into(ivImage);
        Glide.with(getApplicationContext())
                .load(Constants.BAIDU_LOGO)
                .into(ivOri);
        Glide.with(getApplicationContext())
                .load(Constants.BAIDU_LOGO)
//                .transform(new CircleCrop(getApplicationContext()))
                .bitmapTransform(new BlurTransformation(getApplicationContext()),
                        new GrayscaleTransformation(getApplicationContext()),
                        new RoundedCornersTransformation(getApplicationContext(), 28, 0))
                .into(ivTransform);
    }

    private void preloadImage() {
        Glide.with(getApplicationContext())
                .load(Constants.PRELOAD_URL)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .fitCenter() // 默认的 transform
//                .transform(new FitCenter(getApplicationContext()))
//                .transform(new CenterCrop(getApplicationContext()))
                .into(ivImage);
    }

    /**
     * 适用于自定义 View
     */
    private void loadTarget() {
        SimpleTarget<GlideDrawable> drawableTarget = new SimpleTarget<GlideDrawable>() {
            @Override
            public void onResourceReady(GlideDrawable resource,
                                        GlideAnimation<? super GlideDrawable> glideAnimation) {
                ivImage.setImageDrawable(resource);
            }
        };
        Glide.with(getApplicationContext())
                .load(Constants.IMAGE_URL)
                .into(drawableTarget);

        ViewTarget<ViewGroup, GlideDrawable> viewTarget = new ViewTarget<ViewGroup, GlideDrawable>(llRootView) {
            @Override
            public void onResourceReady(GlideDrawable resource,
                                        GlideAnimation<? super GlideDrawable> glideAnimation) {
                getView().setBackground(resource);
            }
        };
        Glide.with(getApplicationContext())
                .load(Constants.IMAGE_URL)
                .into(viewTarget);
    }

    private void loadGif() {
        ProgressInterceptor.addListener(Constants.GIF_URL, new ProgressListener() {
            @Override
            public void onProgress(int progress) {
                mProgressDialog.setProgress(progress);
            }
        });
        mProgressDialog.show();
        Glide.with(getApplicationContext())
                .load(Constants.GIF_URL)
                .asGif() // 加载动态图
                .diskCacheStrategy(DiskCacheStrategy.NONE) // 缓存模式
//                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                .diskCacheStrategy(DiskCacheStrategy.RESULT)
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher_round)
                .listener(new RequestListener<String, GifDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model,
                                               Target<GifDrawable> target,
                                               boolean isFirstResource) {
                        mProgressDialog.dismiss();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GifDrawable resource,
                                                   String model,
                                                   Target<GifDrawable> target,
                                                   boolean isFromMemoryCache,
                                                   boolean isFirstResource) {
                        mProgressDialog.dismiss();
                        ProgressInterceptor.removeListener(Constants.GIF_URL);
                        return false;
                    }
                })
                .into(ivImage);
    }

    private void loadImage() {
        Glide.with(getApplicationContext())
                .load(Constants.IMAGE_URL)
                .asBitmap()
//                .override(200, 200)
//                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher_round)
                .format(DecodeFormat.PREFER_RGB_565)
                .into(ivImage);
    }

    private void downloadOnly() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final FutureTarget<File> futureTarget = Glide.with(getApplicationContext())
                            .load(Constants.DOWNLOAD_ONLY_URL)
                            .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
                    final File imageFile = futureTarget.get();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, imageFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception ignore) {
                }
            }
        }).start();

        Glide.with(getApplicationContext())
                .load(Constants.DOWNLOAD_ONLY_URL)
                .downloadOnly(new DownloadImageTarget());
    }

//    // 加载本地图片
//    File file = new File(getExternalCacheDir() + "/image.jpg");
//Glide.with(this).load(file).into(imageView);
//
//    // 加载应用资源
//    int resource = R.drawable.image;
//Glide.with(this).load(resource).into(imageView);
//
//    // 加载二进制流
//    byte[] image = getImageBytes();
//Glide.with(this).load(image).into(imageView);
//
//    // 加载Uri对象
//    Uri imageUri = getImageUri();
//Glide.with(this).load(imageUri).into(imageView);

}
