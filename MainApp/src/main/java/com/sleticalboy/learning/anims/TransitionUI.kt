package com.sleticalboy.learning.anims

import android.content.Intent
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import com.sleticalboy.learning.R
import com.sleticalboy.learning.base.BaseActivity


/**
 * Created by AS 3.3 on 19-1-6.
 *
 * @author leebin
 */
class TransitionUI : BaseActivity() {
    override fun layoutResId(): Int {
        return R.layout.activity_transition
    }

    override fun initView() {
        val rootView = findViewById<View>(R.id.rootView)
        val fakeSearch = findViewById<View>(R.id.fakeSearch)
        val realSearch = findViewById<View>(R.id.realSearch)
        if (intent.getStringExtra("place_holder") == null) {
            fakeSearch.setOnClickListener { view: View -> showShareAnimation(view) }
            fakeSearch.visibility = View.VISIBLE
            realSearch.visibility = View.INVISIBLE
            rootView.alpha = 1.0f
        } else {
            realSearch.visibility = View.VISIBLE
            fakeSearch.visibility = View.GONE
            rootView.alpha = 0.75f
        }
    }

    private fun showShareAnimation(view: View) {
        val intent = Intent(this, TransitionUI::class.java)
        // final Rect rect = new Rect();
        // view.getGlobalVisibleRect(rect);
        // intent.setSourceBounds(rect);
        intent.putExtra("place_holder", "place_holder")
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, "share_element_test")
        startActivity(intent, options.toBundle())
        overridePendingTransition(0, 0)
    }

    private fun jump(sharedElement: View) {
        supportFragmentManager
                .beginTransaction()
                .addSharedElement(sharedElement, "shared_element_test")
                .replace(R.id.container, null)
                .addToBackStack(null)
                .commit()
    }
}