package com.test.novel.model.mapper

import com.test.novel.model.dto.DeFaultParagraphDto
import com.test.novel.model.vo.NovelContext

/**
 * 段落数据转换映射器
 * 负责在DTO和VO之间进行转换
 */
object ParagraphMapper {

    /**
     * DTO -> VO
     * 将网络数据转换为视图对象
     */
    fun dtoToVo(dto: DeFaultParagraphDto): NovelContext.Default {
        return NovelContext.Default(
            pageIndex = dto.pageIndex,
            totalIndex = dto.totalIndex,
            context = dto.context
        )
    }

    /**
     * VO -> DTO
     * 将视图对象转换为网络数据
     */
    fun voToDto(vo: NovelContext.Default): DeFaultParagraphDto {
        return DeFaultParagraphDto(
            pageIndex = vo.pageIndex,
            totalIndex = vo.totalIndex,
            context = vo.context
        )
    }

    /**
     * 批量转换 DTO List -> VO List
     */
    fun dtoListToVoList(dtoList: List<DeFaultParagraphDto>): List<NovelContext.Default> {
        return dtoList.map { dtoToVo(it) }
    }

    /**
     * 批量转换 VO List -> DTO List
     */
    fun voListToDtoList(voList: List<NovelContext.Default>): List<DeFaultParagraphDto> {
        return voList.map { voToDto(it) }
    }
}
