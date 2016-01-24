package com.kotlinchina.smallpockets.service

import android.content.Context
import android.util.Log
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import rx.Observable
import rx.lang.kotlin.observable

class HttpService {

    fun fetchDataWithUrl(url: String, applicationContext: Context): Observable<String> {
        return observable { subscriber ->
            val queue: RequestQueue = Volley.newRequestQueue(applicationContext)
            val stringRequest = StringRequest(url,
                    object : Response.Listener<String> {
                        override fun onResponse(response: String?) {
                            response.let {
                                subscriber.onNext(it)
//                                val start = it?.indexOf("<title>") as Int
//                                val end = it?.indexOf("</title>") as Int
//                                System.out.println(it?.subSequence(start + 7, end))
//                                val title = it?.subSequence(start + 7, end) as String
                            }
                        }
                    }, object : Response.ErrorListener {
                        override fun onErrorResponse(error: VolleyError?) {
                            error.let {
                                Log.d("TAG", error.toString())
                                subscriber.onError(Throwable(error.toString()))
                            }
                        }
                    })
            queue.add(stringRequest)
        }
    }
}