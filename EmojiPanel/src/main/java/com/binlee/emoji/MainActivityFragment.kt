package com.binlee.emoji

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.binlee.emoji.compat.HttpEngine
import com.binlee.emoji.helper.LogHelper
import kotlinx.android.synthetic.main.fragment_main.*
import okhttp3.*
import java.io.IOException
import kotlin.concurrent.thread

/**
 * A placeholder fragment containing a simple view.
 */
class MainActivityFragment : Fragment() {

    private var request: HttpEngine.BaseRequest? = null
    // private val url = "https://www.baidu.com/"
    private val url = "http://www.minxing365.com/"

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        LogHelper.debug("Fragment", "setUserVisibleHint() -> $isVisibleToUser")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        LogHelper.debug("Fragment", "onAttach() -> $context")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LogHelper.debug("Fragment", "onCreate() -> $savedInstanceState")
        request = HttpEngine.BaseRequest.base(url, HttpEngine.GET)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        LogHelper.debug("Fragment", "onCreateView() -> $savedInstanceState")
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        LogHelper.debug("Fragment", "onActivityCreate() -> $savedInstanceState")
    }

    override fun onStart() {
        super.onStart()
        LogHelper.debug("Fragment", "onStart() -> $activity")
    }

    override fun onResume() {
        super.onResume()
        LogHelper.debug("Fragment", "onResume() -> $activity")
    }

    override fun onPause() {
        super.onPause()
        LogHelper.debug("Fragment", "onPause() -> $activity")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        LogHelper.debug("Fragment", "onSaveInstanceState() -> $outState")
    }

    override fun onStop() {
        super.onStop()
        LogHelper.debug("Fragment", "onStop() -> $activity")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        LogHelper.debug("Fragment", "onDestroyView() -> $activity")
    }

    override fun onDestroy() {
        super.onDestroy()
        LogHelper.debug("Fragment", "onDestroy() -> $activity")
    }

    override fun onDetach() {
        super.onDetach()
        LogHelper.debug("Fragment", "onDetach() -> $activity")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        syncRequest.setOnClickListener {
            sendRequest(request!!, false)
        }
        asyncRequest.setOnClickListener {
            sendRequest(request!!)
        }
    }

    private fun sendRequest(request: HttpEngine.BaseRequest, async: Boolean = true) {
        if (async) {
            HttpAdapter.engine().request(request, object : HttpEngine.Callback() {
                override fun onFailure(e: Throwable?) {
                    helloWorld.append(e?.message)
                }

                override fun onResponse(response: HttpEngine.BaseResponse?) {
                    response.let { helloWorld.text = it?.string() }
                }
            })
        } else {
            thread(true) {
                val response = HttpAdapter.engine().request(request)
                if (response != null) {
                    val result = response.string()
                    helloWorld.post { helloWorld.text = result }
                }
            }
        }
        // okhttpSend(async)
    }

    private fun okhttpSend(async: Boolean) {
        val client = OkHttpClient.Builder().build()
        val r = Request.Builder()
                .get()
                .url("https://www.baidu.com/")
                .build()
        if (async) {
            client.newCall(r).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val result = response.body()?.string()
                    Log.d("Http", "onResponse()\n$result")
                    helloWorld.post { helloWorld.append(result) }
                }

                override fun onFailure(call: Call, e: IOException) {
                    Log.d("Http", "onFailure()\n$e")
                }
            })
        } else {
            Thread {
                val response = client.newCall(r).execute()
                val result = response.body()?.string()
                Log.d("Http", "sync result\n$result")
                helloWorld.post { helloWorld.append(result) }
            }.start()
        }
    }
}
