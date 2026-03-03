package com.test.novel.utils

import com.test.novel.model.dto.BookDto
import com.test.novel.model.dto.ChapterDto
import com.test.novel.model.dto.DeFaultParagraphDto
import com.test.novel.model.vo.ChapterVo

/**
 * 数据生成
 */
class DataMaker {
}
object BookStoreStateDataMaker{
    /**
     * 生成模拟书籍数据
     */
    fun generateMockBooks(): List<BookDto> {
        return listOf(
            BookDto(
                bookId = "1001",
                bookName = "斗破苍穹",
                author = "天蚕土豆",
                type = listOf("玄幻", "热血"),
                status = "完结",
                brief = "天才少年萧炎在创造了家族空前绝后的修炼纪录后，突然成了废人...",
                coverUrl = "https://img.qidian.com/images/book/1001.jpg",
                wordCount = "532万字",
                updateTime = "2024-01-15",
                lastChapterTitle = "大结局"
            ),
            BookDto(
                bookId = "1002",
                bookName = "完美世界",
                author = "辰东",
                type = listOf("玄幻", "冒险"),
                status = "完结",
                brief = "一粒尘可填海，一根草斩尽日月星辰，弹指间天翻地覆...",
                coverUrl = "https://img.qidian.com/images/book/1002.jpg",
                wordCount = "658万字",
                updateTime = "2024-01-10",
                lastChapterTitle = "大结局"
            ),
            BookDto(
                bookId = "1003",
                bookName = "遮天",
                author = "辰东",
                type = listOf("玄幻", "修仙"),
                status = "完结",
                brief = "冰冷与黑暗并存的宇宙深处，九具庞大的龙尸拉着一口青铜古棺...",
                coverUrl = "https://img.qidian.com/images/book/1003.jpg",
                wordCount = "635万字",
                updateTime = "2024-01-08",
                lastChapterTitle = "大结局"
            ),
            BookDto(
                bookId = "1004",
                bookName = "凡人修仙传",
                author = "忘语",
                type = listOf("修仙", "仙侠"),
                status = "完结",
                brief = "一个普通山村小子，偶然下进入到当地江湖小门派...",
                coverUrl = "https://img.qidian.com/images/book/1004.jpg",
                wordCount = "741万字",
                updateTime = "2024-01-05",
                lastChapterTitle = "大结局"
            ),
            BookDto(
                bookId = "1005",
                bookName = "仙逆",
                author = "耳根",
                type = listOf("仙侠", "玄幻"),
                status = "完结",
                brief = "顺为凡，逆则仙，只在心中一念间...",
                coverUrl = "https://img.qidian.com/images/book/1005.jpg",
                wordCount = "658万字",
                updateTime = "2024-01-03",
                lastChapterTitle = "大结局"
            ),
            BookDto(
                bookId = "1006",
                bookName = "我欲封天",
                author = "耳根",
                type = listOf("仙侠", "热血"),
                status = "完结",
                brief = "我若要有，天不可无。我若要无，天不许有...",
                coverUrl = "https://img.qidian.com/images/book/1006.jpg",
                wordCount = "428万字",
                updateTime = "2024-01-01",
                lastChapterTitle = "大结局"
            ),
            BookDto(
                bookId = "1007",
                bookName = "雪中悍刀行",
                author = "烽火戏诸侯",
                type = listOf("武侠", "权谋"),
                status = "完结",
                brief = "江湖是一张珠帘。大人物小人物，是珠子，大故事小故事...",
                coverUrl = "https://img.qidian.com/images/book/1007.jpg",
                wordCount = "461万字",
                updateTime = "2023-12-28",
                lastChapterTitle = "大结局"
            ),
            BookDto(
                bookId = "1008",
                bookName = "元尊",
                author = "天蚕土豆",
                type = listOf("玄幻", "热血"),
                status = "连载",
                brief = "天地为炉，造化为工，阴阳为炭，万物为铜...",
                coverUrl = "https://img.qidian.com/images/book/1008.jpg",
                wordCount = "298万字",
                updateTime = "2024-01-20",
                lastChapterTitle = "第598章 周天"
            ),
            BookDto(
                bookId = "1009",
                bookName = "圣墟",
                author = "辰东",
                type = listOf("玄幻", "科幻"),
                status = "完结",
                brief = "在破败中崛起，在寂灭中复苏。沧海成尘，雷电枯竭...",
                coverUrl = "https://img.qidian.com/images/book/1009.jpg",
                wordCount = "478万字",
                updateTime = "2023-12-25",
                lastChapterTitle = "大结局"
            ),
            BookDto(
                bookId = "1010",
                bookName = "武动乾坤",
                author = "天蚕土豆",
                type = listOf("玄幻", "热血"),
                status = "完结",
                brief = "修炼一途，乃窃阴阳，夺造化，转涅槃，夺生死，掌轮回...",
                coverUrl = "https://img.qidian.com/images/book/1010.jpg",
                wordCount = "395万字",
                updateTime = "2023-12-22",
                lastChapterTitle = "大结局"
            ),
            BookDto(
                bookId = "1011",
                bookName = "大主宰",
                author = "天蚕土豆",
                type = listOf("玄幻", "热血"),
                status = "完结",
                brief = "大千世界，位面交汇，万族林立，群雄荟萃...",
                coverUrl = "https://img.qidian.com/images/book/1011.jpg",
                wordCount = "518万字",
                updateTime = "2023-12-20",
                lastChapterTitle = "大结局"
            ),
            BookDto(
                bookId = "1012",
                bookName = "一念永恒",
                author = "耳根",
                type = listOf("仙侠", "搞笑"),
                status = "完结",
                brief = "一念成沧海，一念化桑田。一念斩千魔，一念诛万仙...",
                coverUrl = "https://img.qidian.com/images/book/1012.jpg",
                wordCount = "446万字",
                updateTime = "2023-12-18",
                lastChapterTitle = "大结局"
            ),
            BookDto(
                bookId = "1013",
                bookName = "求魔",
                author = "耳根",
                type = listOf("仙侠", "玄幻"),
                status = "完结",
                brief = "魔前一叩三千年，回首凡尘不做仙...",
                coverUrl = "https://img.qidian.com/images/book/1013.jpg",
                wordCount = "638万字",
                updateTime = "2023-12-15",
                lastChapterTitle = "大结局"
            ),
            BookDto(
                bookId = "1014",
                bookName = "诛仙",
                author = "萧鼎",
                type = listOf("仙侠", "爱情"),
                status = "完结",
                brief = "天地不仁，以万物为刍狗。这世间本是没有什么神仙...",
                coverUrl = "https://img.qidian.com/images/book/1014.jpg",
                wordCount = "201万字",
                updateTime = "2023-12-12",
                lastChapterTitle = "大结局"
            ),
            BookDto(
                bookId = "1015",
                bookName = "盘龙",
                author = "我吃西红柿",
                type = listOf("玄幻", "西方奇幻"),
                status = "完结",
                brief = "本书讲述了主人公林雷无意中从祖宅拣出一只奇怪的戒指...",
                coverUrl = "https://img.qidian.com/images/book/1015.jpg",
                wordCount = "348万字",
                updateTime = "2023-12-10",
                lastChapterTitle = "大结局"
            ),
            BookDto(
                bookId = "1016",
                bookName = "星辰变",
                author = "我吃西红柿",
                type = listOf("玄幻", "修仙"),
                status = "完结",
                brief = "一名孩童，天生无法修炼内功。为了得到父亲的重视关注...",
                coverUrl = "https://img.qidian.com/images/book/1016.jpg",
                wordCount = "295万字",
                updateTime = "2023-12-08",
                lastChapterTitle = "大结局"
            ),
            BookDto(
                bookId = "1017",
                bookName = "吞噬星空",
                author = "我吃西红柿",
                type = listOf("科幻", "玄幻"),
                status = "完结",
                brief = "小说描述了地球经历一场大灾难后，物种发生变异...",
                coverUrl = "https://img.qidian.com/images/book/1017.jpg",
                wordCount = "518万字",
                updateTime = "2023-12-05",
                lastChapterTitle = "大结局"
            ),
            BookDto(
                bookId = "1018",
                bookName = "莽荒纪",
                author = "我吃西红柿",
                type = listOf("玄幻", "修仙"),
                status = "完结",
                brief = "纪宁死后来到地府，得知自己原本是三界大能者转世...",
                coverUrl = "https://img.qidian.com/images/book/1018.jpg",
                wordCount = "478万字",
                updateTime = "2023-12-03",
                lastChapterTitle = "大结局"
            ),
            BookDto(
                bookId = "1019",
                bookName = "雪鹰领主",
                author = "我吃西红柿",
                type = listOf("玄幻", "奇幻"),
                status = "完结",
                brief = "夏族为了争夺资源，和蛮族展开了血战...",
                coverUrl = "https://img.qidian.com/images/book/1019.jpg",
                wordCount = "418万字",
                updateTime = "2023-12-01",
                lastChapterTitle = "大结局"
            ),
            BookDto(
                bookId = "1020",
                bookName = "飞剑问道",
                author = "我吃西红柿",
                type = listOf("仙侠", "修仙"),
                status = "完结",
                brief = "在这个世界，有狐仙、河神、水怪、大妖...",
                coverUrl = "https://img.qidian.com/images/book/1020.jpg",
                wordCount = "325万字",
                updateTime = "2023-11-28",
                lastChapterTitle = "大结局"
            )
        )
    }
    fun generateMockChapters(): List<ChapterDto> {
        return listOf(
            ChapterDto(
                chapterId = "chapter_001",
                bookId = "1001",
                title = "第一章 陨落的天才",
                content = "斗气大陆，这是一个属于斗气的世界，没有花俏艳丽的魔法，有的，仅仅是繁衍到巅峰的斗气！\n\n" +
                        "乌坦城，萧家府邸。\n\n" +
                        "少年萧炎，脸色苍白地躺在床上，呼吸微弱，曾经的天才，如今却成了人人嘲笑的废物...",
                index = 1,
                wordCount = 2850,
                updateTime = "2024-01-15 10:30:00",
                isVip = false
            ),
            ChapterDto(
                chapterId = "chapter_002",
                bookId = "1001",
                title = "第二章 云岚宗",
                content = "清晨的阳光透过窗户洒进房间，萧炎缓缓睁开眼睛，感受着体内那微弱的斗气...\n\n" +
                        "三年了，整整三年了！\n\n" +
                        "从天才到废物的转变，让这个曾经意气风发的少年，变得沉默寡言...",
                index = 2,
                wordCount = 3200,
                updateTime = "2024-01-15 11:00:00",
                isVip = false
            ),
            ChapterDto(
                chapterId = "chapter_003",
                bookId = "1001",
                title = "第三章 药老",
                content = "就在萧炎绝望之际，他手指上的古朴戒指突然发出了一阵微弱的黑光...\n\n" +
                        "一道苍老的声音在萧炎的脑海中响起：\n\n" +
                        "\"小子，想恢复实力吗？想重新站在巅峰吗？\"\n\n" +
                        "萧炎猛地坐起，震惊地环顾四周...",
                index = 3,
                wordCount = 3500,
                updateTime = "2024-01-15 11:30:00",
                isVip = true
            )
        )
    }
    fun generateMockParagraphs(): List<DeFaultParagraphDto> {
        return listOf(
            DeFaultParagraphDto(
                pageIndex = 1,
                totalIndex = 5,
                context = "斗气大陆，这是一个属于斗气的世界，没有花俏艳丽的魔法，有的，仅仅是繁衍到巅峰的斗气！\n\n" +
                        "乌坦城，萧家府邸。\n\n" +
                        "少年萧炎，脸色苍白地躺在床上，呼吸微弱，曾经的天才，如今却成了人人嘲笑的废物。"
            ),
            DeFaultParagraphDto(
                pageIndex = 2,
                totalIndex = 5,
                context = "三年了，整整三年了！\n\n" +
                        "从天才到废物的转变，让这个曾经意气风发的少年，变得沉默寡言。\n\n" +
                        "每天清晨，当其他家族子弟都在修炼场中刻苦修炼时，萧炎只能独自一人坐在房间中，感受着体内那微弱得几乎可以忽略不计的斗气。"
            ),
            DeFaultParagraphDto(
                pageIndex = 3,
                totalIndex = 5,
                context = "'萧炎哥哥，你又在这里发呆了。'\n\n" +
                        "一个清脆的声音从门外传来，紧接着，一个身着淡绿色长裙的少女推门而入。\n\n" +
                        "少女名叫萧薰儿，是萧家族长的孙女，也是萧炎唯一的朋友。"
            ),
            DeFaultParagraphDto(
                pageIndex = 4,
                totalIndex = 5,
                context = "'薰儿，你说我是不是很没用？'萧炎苦笑着问道。\n\n" +
                        "萧薰儿摇了摇头，认真地说道：'不，萧炎哥哥，你是我见过最厉害的人。我相信你一定能够重新站起来，再次成为家族的骄傲。'\n\n" +
                        "少女的话让萧炎心中涌起一股暖流，但更多的却是无奈。"
            ),
            DeFaultParagraphDto(
                pageIndex = 5,
                totalIndex = 5,
                context = "就在这时，萧炎手指上的古朴戒指突然发出了一阵微弱的黑光。\n\n" +
                        "一道苍老的声音在萧炎的脑海中响起：\n\n" +
                        "'小子，想恢复实力吗？想重新站在巅峰吗？'\n\n" +
                        "萧炎猛地坐起，震惊地环顾四周，却发现房间里空无一人。"
            )
        )
    }
}