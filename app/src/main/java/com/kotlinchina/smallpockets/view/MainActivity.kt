package com.kotlinchina.smallpockets.view

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.ListView
import android.widget.Toast
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.kotlinchina.smallpockets.R
import com.kotlinchina.smallpockets.adapter.ShowSiteListAdapter
import com.kotlinchina.smallpockets.presenter.IMainPresenter
import com.kotlinchina.smallpockets.presenter.MainPresenter
import com.kotlinchina.smallpockets.service.HttpService
import rx.functions.Action1
import java.util.*


class MainActivity : AppCompatActivity(), IMainView {

    val CLIPBOARD_TAG: String = "CLIPBOARD"

    var mainPresenter: IMainPresenter? = null

    var listview: ListView? = null

    val datas = ArrayList<HashMap<String, Any>>()

    var adapter:ShowSiteListAdapter<HashMap<String,Any>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.mainPresenter = MainPresenter(this)

        val resultString = getClipBoardData()
        this.mainPresenter?.checkClipBoardValidation(resultString)
        this.mainPresenter?.loadSiteListData()

        initView()
        initData()
        setOnclickListener()
    }

    private fun setOnclickListener() {
        listview?.setOnItemClickListener { adapterView, view, i, l ->
            Toast.makeText(this@MainActivity,"show detail"+ ": position" +l,Toast.LENGTH_SHORT).show()
        }
    }

    private fun initData() {
        adapter = ShowSiteListAdapter(this,datas)
        listview?.adapter = adapter

    }

    private fun initView() {
        listview = this.findViewById(R.id.listView) as ListView

    }

    private fun getClipBoardData(): String {
        val clipboardManager: ClipboardManager =
                getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        var resultString: String = ""

        if (clipboardManager.hasPrimaryClip()) {
            val clipData: ClipData = clipboardManager.primaryClip
            val clipDataCount = clipData.itemCount
            for (index in 0..clipDataCount - 1) {
                val item = clipData.getItemAt(index)
                val str = item.coerceToText(this)
                resultString += str
            }
        }

        return resultString
    }

    override fun showLink(link: String) {
        Log.e(CLIPBOARD_TAG, link)

        val dialog: AlertDialog.Builder = AlertDialog.Builder(this)
        dialog.setTitle("需要保存此链接么？")
        dialog.setMessage(link)
        dialog.setPositiveButton("OK", DialogInterface.OnClickListener { dialogInterface, i ->
            val service = HttpService()
            service.fetchDataWithUrl(link, this)
                    .map { t ->
                        val start = t?.indexOf("<title>") as Int
                        val end = t?.indexOf("</title>") as Int
                        t?.subSequence(start + 7, end) as String
                    }
                    .subscribe(object: Action1<String> {
                        override fun call(t: String?) {
                            Log.e("=======title", t)
                        }
                    })
        })
        dialog.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialogInterface, i ->
            Log.d(CLIPBOARD_TAG, "Cancel")
        })
        dialog.show()
    }

    override fun showNoLinkWithMsg(msg: String) {
        Log.e(CLIPBOARD_TAG, msg)
    }

    override fun setSiteListData(data: ArrayList<HashMap<String, Any>>) {
        datas.addAll(data)
    }
}
