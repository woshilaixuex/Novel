package com.test.novel.utils

import com.test.novel.model.BookBrief
import kotlinx.coroutines.delay

/**
 * 最小化的WebCrawler - 只提供模拟数据
 */
object SimpleWebCrawler {
    
    /**
     * 模拟获取书籍列表
     */
    suspend fun fetchBookList(): List<BookBrief> {
        delay(100) // 模拟网络延迟
        
        return listOf(
            BookBrief(
                bookId = 1,
                title = "测试小说1",
                author = "测试作者1",
                type = listOf("玄幻"),
                status = "完结",
                brief = "这是测试小说1的简介",
                coverUrl = "https://example.com/cover1.jpg",
                isLocal = false
            ),
            BookBrief(
                bookId = 2,
                title = "测试小说2",
                author = "测试作者2",
                type = listOf("修仙"),
                status = "连载",
                brief = "这是测试小说2的简介",
                coverUrl = "https://example.com/cover2.jpg",
                isLocal = false
            )
        )
    }
}
