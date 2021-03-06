package com.kotlinchina.smallpockets.view

import com.kotlinchina.smallpockets.model.Link

interface ILinkListView {
    fun showDialog(link: String)
    fun showNoLinkWithMsg(msg: String)
    fun setSiteListData(data: List<Link>)
    fun showSaveScreenWithTitle(title: String, url: String)
}
