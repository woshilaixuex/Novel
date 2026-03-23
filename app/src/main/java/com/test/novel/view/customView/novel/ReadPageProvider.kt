package com.test.novel.view.customView.novel

import android.icu.text.CaseMap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.DialogTitle
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.test.novel.databinding.FragmentBookBriefBinding
import com.test.novel.databinding.FragmentCoverReadBinding
import com.test.novel.databinding.FragmentPageBinding
import com.test.novel.model.vo.ReadingPageVo

class ReadPageProvider : PageProvider {

    private val pageDataList = mutableListOf<PageData>()
    enum class PageType{
        Cover,
        Text
    }
    data class PageData (
        val content: String,
        val title: String,
        val pageIndex: Int,
        val totalPages: Int,
        val pageType: PageType = PageType.Cover
    )

    override fun getPageCount(): Int {
        return pageDataList.size
    }

    override fun bindPage(pageView: View, pageIndex: Int) {
        Log.d("ReadPageProvider", "bindPage called, pageIndex: $pageIndex, pageDataList.size: ${pageDataList.size}")
        if (pageIndex < 0 || pageIndex >= getPageCount()) {
            Log.w("PageProvider","page数组越界")
            return
        }
        val data = pageDataList[pageIndex]
        Log.d("ReadPageProvider", "Binding page with content length: ${data.content.length}")
        // 填充内容
        when (data.pageType) {
            PageType.Cover -> bindCoverPage(pageView, data)
            else -> {

            }
        }
//        if(pages[pageIndex] is PageType.NormalPage){
//            val page = FragmentPageBinding.inflate(
//                LayoutInflater.from(pageView.context),
//                pageView as ViewGroup,
//                true
//            )
////            page.topic.layoutParams.let {
////                it as ConstraintLayout.LayoutParams
////            }.apply {
////                setMargins(0, 36, 0, 0)
////            }
//            page.novelText.text = pages[pageIndex].content as String
//            page.pageIndex.text = "${pageIndex + 1}/${pages.size}"
//        }else{
//            val page = FragmentBookBriefBinding.inflate(
//                LayoutInflater.from(pageView.context),
//                pageView as ViewGroup,
//                true
//            )
//            val brief = pages[pageIndex].content as BookBrief
//            Glide.with(pageView.context).load(brief.coverUrl).into(page.bookCover)
//            page.bookName.text = brief.title
//            page.author.text = brief.author
//            page.introduction.text = brief.brief
//            page.type.text = brief.type.joinToString("\u3000")
//        }
    }
    private fun bindErrorPage(pageView: View) {}

    private fun bindLoadingPage(pageView: View) {

    }

    private fun bindCoverPage(
        pageView: View,
        data: PageData
    ) {
        Log.d("ReadPageProvider", "bindCoverPage called, pageView: $pageView, content: ${data.content}")
        val pageViewGroup = pageView as ViewGroup
        pageViewGroup.removeAllViews()
        val page = FragmentCoverReadBinding.inflate(
            LayoutInflater.from(pageView.context),
            pageViewGroup,
            true
        )
        page.novelText.text = data.content
        page.title.text = data.title
        page.title.visibility = if (data.pageIndex == 0) View.VISIBLE else View.GONE
        Log.d("ReadPageProvider", "Text set to novelText, child count: ${pageViewGroup.childCount}")
    }

    override fun addPage(pageData: PageData): Int {
        Log.d("ReadPageProvider", "addPage called, pageData: $pageData, current size: ${pageDataList.size}")
        pageDataList.add(pageData)
        Log.d("ReadPageProvider", "addPage completed, new size: ${pageDataList.size}")
        return pageDataList.size - 1
    }

}
