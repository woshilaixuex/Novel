package com.test.novel.model.mapper

import com.test.novel.model.domain.Book
import com.test.novel.model.domain.BookStatus
import com.test.novel.model.dto.BookDto
import com.test.novel.model.vo.BookVo

/**
 * 数据转换映射器
 * 负责在不同数据层之间进行转换
 */
object BookMapper {

    /**
     * DTO -> Domain Model
     * 将网络数据转换为领域模型
     */
    fun toDomain(dto: BookDto): Book {
        return Book(
            id = dto.bookId,
            title = dto.bookName,
            author = dto.author,
            coverUrl = dto.coverUrl,
            description = dto.brief,
            categories = dto.type,
            status = dto.status.toBookStatus(),
            wordCount = dto.wordCount.toLongOrNull() ?: 0L,
            updateTime = dto.updateTime,
            chapters = emptyList(), // 章节需要单独加载
            isLocal = false
        )
    }

    /**
     * DTO -> View Object
     */
    fun dtoToVo(dto: BookDto): BookVo {
        return BookVo(
            id = dto.bookId,
            title = dto.bookName,
            author = dto.author,
            coverUrl = dto.coverUrl,
            description = dto.brief,
            categories = dto.type,
            status = dto.status,
            wordCount = formatWordCount(dto.wordCount.toLongOrNull() ?: 0L,),
            updateTime = dto.updateTime,
            lastChapterTitle = dto.lastChapterTitle ?: "",
            isFavorite = dto.isFavorite,
        )
    }

    /**
     * 批量转换 DTO List -> Vo List
     * 批量转换方法
     */
    fun dtoListToVoList(dtoList: List<BookDto>): List<BookVo> {
        return dtoList.map { dtoToVo(it) }
    }

    /**
     * 批量转换 DTO List -> Domain List
     */
    fun dtoListToDomainList(dtoList: List<BookDto>): List<Book> {
        return dtoList.map { toDomain(it) }
    }

    /**
     * 兼容旧的BookBrief结构
     * 将Domain Model转换为BookBrief用于导航传递
     */
    fun toBookBrief(domain: Book): com.test.novel.model.BookBrief {
        return com.test.novel.model.BookBrief(
            title = domain.title,
            bookId = domain.id.toIntOrNull() ?: 0,
            coverUrl = domain.coverUrl,
            author = domain.author,
            type = domain.categories,
            status = domain.status.name,
            brief = domain.description,
            isLocal = domain.isLocal
        )
    }

    /**
     * 字符串状态转换为枚举
     */
    private fun String.toBookStatus(): BookStatus {
        return when (this.lowercase()) {
            "连载中", "ongoing" -> BookStatus.ONGOING
            "已完结", "completed" -> BookStatus.COMPLETED
            "暂停", "paused" -> BookStatus.PAUSED
            else -> BookStatus.ONGOING
        }
    }

    /**
     * 格式化字数显示
     */
    private fun formatWordCount(wordCount: Long): String {
        return when {
            wordCount >= 10000 -> "${wordCount / 10000}万字"
            wordCount >= 1000 -> "${wordCount / 1000}千字"
            else -> "${wordCount}字"
        }
    }
}
