package com.sleticalboy.objectbox;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sleticalboy.objectbox.bean.circle.Attachment;
import com.sleticalboy.objectbox.bean.circle.Message;
import com.sleticalboy.objectbox.bean.im.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.TxCallback;
import io.objectbox.query.Query;
import io.objectbox.query.QueryFilter;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView insertCount = findViewById(R.id.insertCount);
        BoxHelper.getInstance().startService(this);
        final Box<User> personBox = BoxHelper.getInstance().box(this, User.class);
        BoxStore boxStore = BoxHelper.getInstance().getBoxStore();
        final List<User> users = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            User user = new User();
            user.setName("leebin " + i);
            user.setBirthday(new Date());
            users.add(user);
        }
        boxStore.runInTx(new Runnable() {
            @Override
            public void run() {
                // 边读边写
                List<User> result = personBox.getAll();
                for (User user : result) {
                    if (user.dbId % 3 == 0) {
                        user.setName("leebin " + user.dbId);
                        personBox.put(user);
                    }
                }
            }
        });
        boxStore.runInReadTx(new Runnable() {
            @Override
            public void run() {
                // 只读
                List<User> users = personBox.getAll();
                for (User p : users) {
                    Log.d("ObjectBox", "-> " + p.toString());
                }
            }
        });
        boxStore.runInTxAsync(new Runnable() {
            @Override
            public void run() {
                // 异步执行
                personBox.put(users);
            }
        }, new TxCallback<Void>() {
            @Override
            public void txFinished(@Nullable Void result, @Nullable Throwable error) {
                // 异步操作回调
                Log.d(TAG, "txFinished() called with: result = [" + result + "], error = [" + error + "]");
            }
        });
        insertCount.setText("insert result = " + personBox.count());
        // 查询操作
        List<User> userList = personBox.getAll();
        for (User p : userList) {
            Log.d("ObjectBox", p.toString());
        }
        // 条件查询
        Query<User> query = personBox.query().filter(new QueryFilter<User>() {
            @Override
            public boolean keep(@NonNull User entity) {
                return entity.dbId >= 20 && entity.dbId < 30;
            }
        }).build();
        for (User user : query.find()) {
            Log.d(TAG, user.toString());
        }
        // 删除
        personBox.remove(10);
        Log.d("ObjectBox", "count -> " + personBox.count());
        messageStore();
    }

    private void messageStore() {
        final Message message = new Message();
        message.setRichText("this is a message");
        message.setCreateAt("2019年01月27日19:50:58");
        User user = new User();
        user.setName("李斌");
        user.setBirthday(new Date());
        message.getUser().setTarget(user);
        for (int i = 0; i < 5; i++) {
            Attachment attachment = new Attachment();
            attachment.setThumbnail("https://www.baidu.com");
            attachment.setType("link " + i);
            message.getAttachments().add(attachment);
        }
        BoxHelper.getInstance().box(this, Message.class).put(message);
    }
}
