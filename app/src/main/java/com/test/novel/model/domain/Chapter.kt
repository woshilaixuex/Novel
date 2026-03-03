package com.test.novel.model.domain

import kotlinx.serialization.Serializable

/**
 * 统一的章节领域模型
 */
data class Chapter(
    val id: String,
    val title: String,

    // 核心内容
    val content: String,              // 原始完整内容
    val wordCount: Int = content.length,

    // 预处理数据（性能优化）
    val paragraphs: List<Paragraph> = emptyList(),     // 段落分割
    val lines: List<String> = emptyList(),              // 按行分割（滚动翻页用）
    val pages: List<PageContent> = emptyList(),         // 预分页（仿真/滑动翻页用）

    // 后续添加业务设计
    val isVip: Boolean = false,
)
data class Paragraph(
    val index: Int,
    val text: String,
    val type: ParagraphType = ParagraphType.TEXT,
    val startCharIndex: Int,    // 在原文中的起始位置
    val endCharIndex: Int      // 在原文中的结束位置
)
data class PageContent(
    val pageIndex: Int,
    val content: String,              // 当前页完整内容
    val startCharIndex: Int,          // 在原文中的起始位置
    val endCharIndex: Int,            // 在原文中的结束位置
    val paragraphIndices: List<Int>,   // 包含的段落索引
    val lineCount: Int,              // 行数（滚动翻页计算用）
    val estimatedReadTime: Int        // 预估阅读时间（秒）
)
enum class ParagraphType {
    TEXT,           // 普通文本
    DIALOGUE,       // 对话
    DESCRIPTION,    // 描述
    TITLE,          // 标题
    EMPTY           // 空行
}