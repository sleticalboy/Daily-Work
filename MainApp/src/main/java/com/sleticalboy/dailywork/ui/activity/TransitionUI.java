package com.sleticalboy.dailywork.ui.activity;

import android.content.Intent;
import android.view.View;

import com.sleticalboy.dailywork.R;
import com.sleticalboy.dailywork.base.BaseActivity;

import androidx.core.app.ActivityOptionsCompat;

/**
 * Created by AS 3.3 on 19-1-6.
 *
 * @author leebin
 */
public class TransitionUI extends BaseActivity {

    @Override
    protected int layoutResId() {
        return R.layout.transition_activity;
    }

    @Override
    protected void initView() {
        final View rootView = findViewById(R.id.rootView);
        final View fakeSearch = findViewById(R.id.fakeSearch);
        final View realSearch = findViewById(R.id.realSearch);
        if (getIntent().getStringExtra("place_holder") == null) {
            fakeSearch.setOnClickListener(this::showShareAnimation);
            fakeSearch.setVisibility(View.VISIBLE);
            realSearch.setVisibility(View.INVISIBLE);
            rootView.setAlpha(1.0f);
        } else {
            realSearch.setVisibility(View.VISIBLE);
            fakeSearch.setVisibility(View.GONE);
            rootView.setAlpha(0.75f);
        }
    }

    private void showShareAnimation(View view) {
        final Intent intent = new Intent(this, TransitionUI.class);
        // final Rect rect = new Rect();
        // view.getGlobalVisibleRect(rect);
        // intent.setSourceBounds(rect);
        intent.putExtra("place_holder", "place_holder");
        final ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, "share_element_test");
        startActivity(intent, options.toBundle());
        overridePendingTransition(0, 0);
    }

    private void jump(View sharedElement) {
        getSupportFragmentManager()
                .beginTransaction()
                .addSharedElement(sharedElement, "shared_element_test")
                .replace(R.id.container, null)
                .addToBackStack(null)
                .commit();
    }
}