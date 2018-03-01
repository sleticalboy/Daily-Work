package com.sleticalboy.dailywork.ui.activity;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.widget.Toast;

import com.sleticalboy.dailywork.R;
import com.sleticalboy.dailywork.base.BaseActivity;
import com.sleticalboy.dailywork.weight.xrecycler.adapter.XRecyclerAdapter;
import com.sleticalboy.dailywork.weight.xrecycler.decoration.DividerGridItemDecoration;
import com.sleticalboy.dailywork.weight.xrecycler.helper.SelectedItemDragItemTouchCallback;

/**
 * Created on 18-2-7.
 *
 * @author sleticalboy
 * @version 1.0
 * @description
 */
public class DecorationActivity extends BaseActivity {

    private Integer[] mImagesIds = {
            R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher,
            R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher,
    };

    @Override
    protected void initData() {
    }

    @Override
    protected void initView() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        ItemTouchAdapter adapter = new ItemTouchAdapter(this, mImagesIds);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 3);
        // layoutManager = new StaggeredGridLayoutManager(4, LinearLayout.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
//        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
//        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayout.HORIZONTAL));
        DividerGridItemDecoration decor = new DividerGridItemDecoration(this, 8);
        decor.setDivider(R.drawable.divider);
        recyclerView.addItemDecoration(decor);
//        recyclerView.addItemDecoration(new ItemTouchHelper(new SelectedItemDragItemTouchCallback(adapter)));
        new ItemTouchHelper(new SelectedItemDragItemTouchCallback(adapter)).attachToRecyclerView(recyclerView);
    }

    @Override
    protected int attachLayout() {
        return R.layout.activity_decoration;
    }
}
