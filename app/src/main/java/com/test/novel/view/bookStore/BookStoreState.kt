package com.test.novel.view.bookStore

import android.os.Parcelable
import com.test.novel.model.BookBrief
import com.test.novel.model.vo.BookVo
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
data class BookStoreState(
    val searchBarText:String = "",
    val isRefreshing:Boolean = false,
    val isLoading:Boolean = false,
    val rank:List<BookVo> = listOf(),
    val recommend:List<BookVo> = listOf(),
) : Parcelable

sealed class BookStoreIntent{
    /**
     * 自定义数据类型
     */
    data object DefaultData:BookStoreIntent()
    /**
     * 根据后端加载数据
     */
    data object LoadDataWithNet:BookStoreIntent()
    data object Refresh:BookStoreIntent()

    data object LoadMore:BookStoreIntent()

    data object Search:BookStoreIntent()

    data object InitData:BookStoreIntent()
}


