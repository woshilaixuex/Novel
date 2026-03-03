package com.test.novel.model.mapper

import com.test.novel.model.dto.ChapterDto
import com.test.novel.model.vo.ChapterVo

/**
 * 章节数据转换映射器
 * 负责在DTO和VO之间进行转换
 */
object ChapterMapper {

    /**
     * DTO -> VO
     * 将网络数据转换为视图对象
     */
    fun dtoToVo(dto: ChapterDto): ChapterVo {
        return ChapterVo(
            id = dto.chapterId,
            title = dto.title,
            index = dto.index,
            isVip = dto.isVip,
            isRead = false // 默认未读，实际状态需要从阅读记录中获取
        )
    }

    /**
     * VO -> DTO
     * 将视图对象转换为网络数据
     */
    fun voToDto(vo: ChapterVo, bookId: String): ChapterDto {
        return ChapterDto(
            chapterId = vo.id,
            bookId = bookId,
            title = vo.title,
            content = "", // VO不包含内容，需要从其他地方获取
            index = vo.index,
            wordCount = 0, // VO不包含字数，需要从其他地方获取
            updateTime = "", // VO不包含更新时间，需要从其他地方获取
            isVip = vo.isVip
        )
    }

    /**
     * 批量转换 DTO List -> VO List
     */
    fun dtoListToVoList(dtoList: List<ChapterDto>): List<ChapterVo> {
        return dtoList.map { dtoToVo(it) }
    }

    /**
     * 批量转换 VO List -> DTO List
     */
    fun voListToDtoList(voList: List<ChapterVo>, bookId: String): List<ChapterDto> {
        return voList.map { voToDto(it, bookId) }
    }

    /**
     * 批量转换 DTO List -> VO List (带阅读状态)
     * 根据已读章节列表标记阅读状态
     */
    fun dtoListToVoListWithReadStatus(
        dtoList: List<ChapterDto>, 
        readChapterIds: Set<String>
    ): List<ChapterVo> {
        return dtoList.map { dto ->
            dtoToVo(dto).copy(
                isRead = readChapterIds.contains(dto.chapterId)
            )
        }
    }
}
