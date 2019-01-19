package com.sleticalboy.eventbus;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.sleticalboy.eventbus.event.EventMessage;

import org.greenrobot.eventbus.EventBus;

/**
 * Created on 18-6-9.
 *
 * @author sleticalboy
 * @description
 */
public class PostEventActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_event);
        Button btnPostAndExit = (Button) findViewById(R.id.btn_post_and_exit);
        btnPostAndExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Runnable runnable = new Runnable() {

                    @Override
                    public void run() {
                        EventMessage message = new EventMessage();
                        message.mMessage = "第二个界面返回的传递的数据";
                        EventBus.getDefault().post(message);
                    }
                };
                new Thread(runnable).start();
                finish();
            }
        });
    }
}
