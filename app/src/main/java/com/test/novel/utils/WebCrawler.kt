package com.test.novel.utils

import com.test.novel.model.BookBrief
import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit
import kotlin.time.measureTime

/**
 * 简单的网络数据获取工具类
 * 用于生成模拟数据，不进行实际网络请求
 */
object WebCrawler {
    
    /**
     * 模拟获取起点中文网排行榜
     */
    suspend fun fetchFQTop(): List<BookBrief> {
        delay(100) // 模拟网络延迟
        
        return listOf(
            BookBrief(
                bookId = 1001,
                title = "斗破苍穹",
                author = "天蚕土豆",
                type = listOf("玄幻", "热血"),
                status = "完结",
                brief = "天才少年萧炎在创造了家族空前绝后的修炼纪录后，突然成了废人...",
                coverUrl = "https://img.qidian.com/images/book/1001.jpg",
                isLocal = false
            ),
            BookBrief(
                bookId = 1002,
                title = "完美世界",
                author = "辰东",
                type = listOf("玄幻", "冒险"),
                status = "完结",
                brief = "一粒尘可填海，一根草斩尽日月星辰...",
                coverUrl = "https://img.qidian.com/images/book/1002.jpg"
            ),
            BookBrief(
                bookId = 1003,
                title = "遮天",
                author = "辰东",
                type = listOf("玄幻", "修仙"),
                status = "完结",
                brief = "冰冷与黑暗并存的宇宙深处，九具庞大的龙尸拉着一口青铜古棺...",
                coverUrl = "https://img.qidian.com/images/book/1003.jpg"
            )
        )
    }
    
    /**
     * 模拟获取笔趣阁排行榜
     */
    suspend fun fetchBQGTop(): List<BookBrief> {
        delay(100) // 模拟网络延迟
        
        return listOf(
            BookBrief(
                bookId = 2001,
                title = "凡人修仙传",
                author = "忘语",
                type = listOf("修仙", "仙侠"),
                status = "完结",
                brief = "一个普通山村小子，偶然下进入到当地江湖小门派...",
                coverUrl = "https://www.biquge.com/images/cover/2001.jpg",
                isLocal = false
            )
        )
    }
    
    /**
     * 模拟获取书籍详情
     */
    suspend fun fetchBookInfo(bookUrl: String): BookBrief {
        delay(50) // 模拟网络延迟
        
        return BookBrief(
            bookId = bookUrl.hashCode(),
            title = "测试小说",
            author = "测试作者",
            type = listOf("测试类型"),
            status = "连载",
            brief = "这是一个测试小说的简介...",
            coverUrl = "https://example.com/cover.jpg",
            isLocal = false
        )
    }
    
    /**
     * 模拟获取章节内容
     */
    suspend fun fetchChapter(bookId: Int, chapter: Int): Pair<String, String> {
        delay(80) // 模拟网络延迟
        
        val chapterTitle = "第${chapter}章"
        val chapterContent = """
            这是第${chapter}章的内容。
            
            主角在这个章节中经历了重要的成长，
            面临了新的挑战，也获得了新的机遇。
            
            通过不断的努力和坚持，
            主角终于突破了自己的瓶颈，
            达到了一个全新的境界。
        """.trimIndent()
        
        return Pair(chapterTitle, chapterContent)
    }
}

@Serializable
data class BookBrief(
    val bookId: Int = 0,
    val title: String = "",
    val author: String = "",
    val type: List<String> = listOf(),
    val status: String = "",
    val brief: String = "",
    val coverUrl: String = ""
)

fun main() {
    runBlocking {
        val time = measureTime {
            println(WebCrawler.fetchFQTop())
        }
        println(time)
    }
}
