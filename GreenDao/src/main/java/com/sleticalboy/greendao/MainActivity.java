package com.sleticalboy.greendao;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sleticalboy.greendao.bean.StudentMsgBean;
import com.sleticalboy.greendao.bean.StudentMsgBeanDao;

import org.greenrobot.greendao.query.Query;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created on 18-3-2.
 *
 * @author sleticalboy
 * @version 1.0
 * @description
 */
public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Button buttonAdd;
    private EditText etHint;
    private RecyclerView ssRecyclerView;

    private StudentAdapter mAdapter;
    private StudentMsgBeanDao mDao;
    private Query<StudentMsgBean> mQuery;

    private final StudentAdapter.OnItemClickListener mListener = new StudentAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(int position) {
            final StudentMsgBean bean = mAdapter.getStudent(position);
            mDao.deleteByKey(bean.getId());
            Log.d("MainActivity", "delete this student " + bean.getId());
            updateList();
        }
    };

    private void updateList() {
        mAdapter.setDataSet(mQuery.list());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setupViews();
        mDao = ((App) getApplication()).getDaoSession().getStudentMsgBeanDao();
        mQuery = mDao.queryBuilder().orderAsc(StudentMsgBeanDao.Properties.Text).build();
        updateList();
    }

    private void setupViews() {
        setSupportActionBar(toolbar);

        ssRecyclerView.setHasFixedSize(true);
        ssRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new StudentAdapter(mListener);
        ssRecyclerView.setAdapter(mAdapter);

        buttonAdd.setEnabled(false);

        etHint.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    addSS();
                    return true;
                }
                return false;
            }
        });
        etHint.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                buttonAdd.setEnabled(s.length() != 0);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSS();
            }
        });
    }

    private void addSS() {
        String text = etHint.getText().toString();
        etHint.setText("");

        DateFormat format = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
        String comment = "Add on" + format.format(new Date());
        StudentMsgBean bean = new StudentMsgBean();
        bean.setText(text);
        bean.setComment(comment);
        bean.setDate(new Date());
        mDao.insert(bean);
        Log.d("insert", "insert data successful");
        updateList();
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        buttonAdd = (Button) findViewById(R.id.buttonAdd);
        etHint = (EditText) findViewById(R.id.etHint);
        ssRecyclerView = (RecyclerView) findViewById(R.id.ssRecyclerView);
    }
}
