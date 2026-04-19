package com.test.novel.model.vo

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.parcelize.Parcelize


/**
 * 书籍视图对象 - 专门用于UI展示
 * 从Domain Model转换而来，只包含UI需要的字段
 */
@Parcelize
data class BookVo(
    val id: String, // 书籍id
    val title: String, // 书名
    val author: String, // 作者
    val coverUrl: String, // 封面
    val description: String, // 简述
    val categories: List<String>, // 类别tag
    val status: String, // 后续可能设置为Vip
    val wordCount: String, // 字数
    val updateTime: String, // 更新时间
    val lastChapterTitle: String = "", // 阅读记录
    val isFavorite: Boolean = false, // 是否收藏
    val readingProgress: Float = 0f , // 阅读进度
    val chapters: List<ChapterVo> = emptyList(), // 章节信息
) : Parcelable

/**
 * 章节视图对象
 */
@Parcelize
data class ChapterVo(
    val id: String, // 章节id
    val title: String, // 章节名
    val index: Int, // 索引，第..几章
    val isVip: Boolean = false,
    val isRead: Boolean = false
) : Parcelable

/**
 * 阅读页面视图对象
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class ReadingPageVo(
    val chapterId: String, // 章节id
    val chapterTitle: String, // 章节名
    val pageIndex: Int,
    val totalIndex: Int,
    val content: String
) : Parcelable{
}
