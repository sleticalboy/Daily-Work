package com.sleticalboy.glide4x;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterInside;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.sleticalboy.glide4x.custom.okhttp.ProgressInterceptor;
import com.sleticalboy.glide4x.listener.ProgressListener;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author sleticalboy
 */
public class MainActivity extends AppCompatActivity {
    
    private static final String BOOK_URL = "http://guolin.tech/book.png";
    private static final String BIG_IMAGE_URL = "https://tse1-mm.cn.bing.net/th?id=OIP.lInnFNdwiuvp0OJlna1CmAHaEx&pid=Api";
    private final ExecutorService singleThreadPool = new ThreadPoolExecutor(1, 1,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(1024),
            new ThreadFactory() {
                @Override
                public Thread newThread(@NonNull Runnable r) {
                    final Thread thread = new Thread(r);
                    thread.setDaemon(true);
                    return thread;
                }
            },
            new ThreadPoolExecutor.AbortPolicy());
    RequestOptions mOptions;
    private Button btnShowImage;
    private ImageView ivShow;
    private ProgressDialog mProgressDialog;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .error(R.mipmap.ic_launcher_round)
                .override(Target.SIZE_ORIGINAL)
                .skipMemoryCache(true)
                .transforms(new CenterInside())
                .fitCenter()
                .centerCrop()
                .circleCrop()
                .placeholder(R.mipmap.ic_launcher);
        
        btnShowImage = findViewById(R.id.btn_show_image);
        ivShow = findViewById(R.id.iv_show);
        btnShowImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImage();
            }
        });
    }
    
    private void showImage() {
        final RequestOptions options = RequestOptions.skipMemoryCacheOf(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .override(Integer.MIN_VALUE, Integer.MIN_VALUE)
                .circleCrop()
                // .transform(new RoundedCorners(50))
                // .transform(new CircleTransformation())
                // .transform(new CropSquareTransformation())
                // .transform(new BlurTransformation())
                // .transform(new MaskTransformation(20)) // 不知道啥玩意儿
                // .transform(new GrayscaleTransformation())
                ;
        Glide.with(getApplicationContext())
                .asBitmap()
                .load(BIG_IMAGE_URL)
                .apply(options)
                .into(ivShow);
        downloadOnly();
    }
    
    private void downloadOnly() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("加载中...");
        mProgressDialog.show();
        ProgressInterceptor.addListener(BIG_IMAGE_URL, new ProgressListener() {
            @Override
            public void onProgress(int progress) {
                mProgressDialog.setProgress(progress);
            }
        });
        singleThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final File file = Glide.with(getApplicationContext())
                            .asFile()
                            .load(BIG_IMAGE_URL)
                            .submit()
                            .get();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Image saved to \"" +
                                    file.getAbsolutePath() + "\"", Toast.LENGTH_SHORT).show();
                            mProgressDialog.dismiss();
                        }
                    });
                } catch (Exception ignore) {
                }
            }
        });
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        singleThreadPool.shutdown();
    }
}
