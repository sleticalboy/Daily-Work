package com.sleticalboy.dailywork.ui.activity;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.sleticalboy.dailywork.MainApp;
import com.sleticalboy.dailywork.R;
import com.sleticalboy.dailywork.base.BaseActivity;
import com.sleticalboy.dailywork.bean.DaoSession;
import com.sleticalboy.dailywork.bean.Note;

/**
 * Created on 18-3-5.
 *
 * @author sleticalboy
 * @version 1.0
 * @description 练习使用 GreenDao 数据库框架
 */
public class GreenDaoActivity extends BaseActivity {

    private EditText etNoteContent;
    private Button btnAddNote;
    private RecyclerView rvNoteList;
    private DaoSession mDaoSession;

    @Override
    protected void prepareWork() {
        mDaoSession = MainApp.getDaoSession();
    }


    @Override
    protected void initData() {
    }

    @Override
    protected void initView() {
        etNoteContent = (EditText) findViewById(R.id.etNoteContent);
        btnAddNote = (Button) findViewById(R.id.btnAddNote);
        rvNoteList = (RecyclerView) findViewById(R.id.rvNoteList);
        setupViews();
    }

    private void setupViews() {
        btnAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNote();
            }
        });
    }

    private void addNote() {
        Note note = null;
        String content = etNoteContent.getText().toString();
        if (!TextUtils.isEmpty(content)) {
            note = new Note();
            note.setName("----");
            note.setText(content);
        }
        mDaoSession.getNoteDao().insert(note);
        updateList();
    }

    private void updateList() {

    }

    @Override
    protected int layoutResId() {
        return R.layout.activity_green_dao;
    }
}
