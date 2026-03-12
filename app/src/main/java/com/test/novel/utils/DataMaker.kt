package com.test.novel.utils

import com.test.novel.model.dto.BookDto
import com.test.novel.model.dto.ChapterDto

/**
 * 数据生成
 */
class DataMaker {
}
object BookDataMaker{
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
                        "少年萧炎，脸色苍白地躺在床上，呼吸微弱，曾经的天才，如今却成了人人嘲笑的废物。\n\n" +
                        "三年前，萧炎十二岁，就已经拥有了九段斗之气，成为了家族百年之内最年轻的斗者。那时的他，意气风发，是家族的希望，是所有人的骄傲。然而，就在他即将突破斗者，凝聚斗之气旋的时候，异变突生。\n\n" +
                        "他体内的斗之气，在一夜之间消失殆尽，从九段斗之气，直接跌落到三段斗之气。这种诡异的情况，让整个家族震惊，也让萧炎从天之骄子，变成了人人唾弃的废物。\n\n" +
                        "三年来，萧炎无数次尝试修炼，但每次凝聚的斗之气，都会在第二天消失得无影无踪。这种诡异的现象，让萧炎几近绝望，也让他在家族中的地位一落千丈。\n\n" +
                        "曾经对他毕恭毕敬的族人，如今看到他都是冷嘲热讽；曾经对他爱慕有加的少女，如今看到他都是避之不及。就连他的父亲，萧家族长萧战，也因为他的事情，在族中抬不起头来。\n\n" +
                        "但萧炎没有放弃，三年了，整整三年了！他每天都在坚持修炼，哪怕知道第二天斗之气会消失，他依然没有放弃。这种执着，这种倔强，让一些族人暗自佩服，但更多的人，却认为他是在做无用功。\n\n" +
                        "今天，是萧家的成年仪式，也是萧炎十六岁的生日。按照萧家的规矩，十六岁如果还没有达到七段斗之气，就会被分配到家族的产业中去，从此失去核心弟子的身份。\n\n" +
                        "而萧炎，如今只有三段斗之气，连五段都达不到，更别提七段了。他知道，今天之后，他将彻底沦为家族的边缘人物，但他不甘心，他真的不甘心！\n\n" +
                        "就在萧炎绝望之际，他手指上的古朴戒指突然发出了一阵微弱的黑光。这是母亲留给他的遗物，三年来他一直戴着，从未离身。但那黑光只是一闪而逝，仿佛从未出现过。\n\n" +
                        "萧炎猛地坐起，震惊地看着手中的戒指。三年了，这是他第一次看到戒指有反应。他仔细地检查着戒指，却发现戒指依然古朴无华，没有任何异常。\n\n" +
                        "是我眼花了？萧炎喃喃自语，但心中却燃起了一丝希望。母亲留给他的东西，绝对不会是普通之物。也许，这三年来的诡异现象，就和这枚戒指有关？\n\n" +
                        "萧炎重新躺下，闭上眼睛。不管如何，他都不会放弃。只要还有一丝希望，他就会坚持下去。斗气大陆，强者为尊，他萧炎，绝不甘心做一个废物！",
                index = 1,
                wordCount = 2850,
                updateTime = "2024-01-15 10:30:00",
                isVip = false
            ),
            ChapterDto(
                chapterId = "chapter_002",
                bookId = "1001",
                title = "第二章 云岚宗",
                content = "清晨的阳光透过窗户洒进房间，萧炎缓缓睁开眼睛，感受着体内那微弱的斗气。\n\n" +
                        "三年了，整整三年了！从天才到废物的转变，让这个曾经意气风发的少年，变得沉默寡言。\n\n" +
                        "但那道苍老的声音，却让萧炎心中燃起了希望。戒指里的神秘存在，三年来一直在吸收他的斗之气，如今终于苏醒了。\n\n" +
                        "萧炎坐在床上，仔细端详着手中的古朴戒指。这枚母亲留给他的遗物，三年来他一直戴着，从未离身。他万万没想到，自己斗之气消失的原因，竟然就在这枚戒指里。\n\n" +
                        "你到底是谁？萧炎在心中问道。那苍老的声音笑了笑，说道：我是谁？时间太久，我自己都快忘了。你可以叫我药老，我曾经是一名炼药师。\n\n" +
                        "炼药师！萧炎心中一震。在斗气大陆上，炼药师是最高贵的职业之一，能够炼制各种神奇的丹药，帮助斗者提升修为。一名高级的炼药师，地位甚至不亚于一名斗皇强者。\n\n" +
                        "药老继续说道：三年前，我在这枚戒指中苏醒，但灵魂力量太弱，需要吸收斗之气来恢复。这三年来，我一直在吸收你的斗之气，如今终于恢复了一些力量，可以和你交流了。\n\n" +
                        "萧炎心中复杂，既愤怒又无奈。三年的痛苦，三年的屈辱，竟然都是因为这枚戒指。但如果不被吸收斗之气，他可能永远不会知道母亲的遗物中还有这样的秘密。\n\n" +
                        "药老似乎感受到了萧炎的情绪，笑道：小子，别生气。这三年来我虽然吸收了你的斗之气，但也让你的经脉变得更加坚韧。现在我开始教导你修炼，你的进步会比以前更快。\n\n" +
                        "而且，作为补偿，我会传授你炼药术。有我的指导，你将来必定能成为一名出色的炼药师。到时候，什么云岚宗，什么纳兰嫣然，都不在话下。\n\n" +
                        "萧炎听到纳兰嫣然这个名字，心中一痛。三年前，在他还是天才的时候，云岚宗宗主云韵亲自上门，为他的弟子纳兰嫣然和他定下了婚约。那时的他，意气风发，是家族的希望，是所有人的骄傲。\n\n" +
                        "然而，就在他变成废物后不久，纳兰嫣然亲自来到萧家，要求解除婚约。那一天，是整个萧家的耻辱，也是萧炎一生中最屈辱的时刻。\n\n" +
                        "纳兰嫣然站在萧家大厅中，居高临下地看着萧炎，眼中带着不屑和怜悯。她说：萧炎，现在的你已经配不上我了。我将来是要成为云岚宗宗主的人，而你，只是一个废物。\n\n" +
                        "萧炎永远忘不了那一天，忘不了纳兰嫣然那高傲的表情，忘不了族人那失望的眼神，更忘不了父亲那痛苦的神情。\n\n" +
                        "从那一天起，萧炎就发誓，一定要重新站起来，一定要让纳兰嫣然后悔，一定要让所有人都知道，他萧炎，不是废物！\n\n" +
                        "药老感受到了萧炎的执念，满意地点点头：好！有这股劲头，何愁大事不成。从今天开始，你按照我的方法修炼，三个月后，我保证你能恢复到六段斗之气。\n\n" +
                        "萧炎握紧拳头，眼中闪烁着坚定的光芒。纳兰嫣然，云岚宗，你们等着。总有一天，我会亲自踏上云岚宗，让你们为当年的决定后悔！",
                index = 2,
                wordCount = 3200,
                updateTime = "2024-01-15 11:00:00",
                isVip = false
            ),
            ChapterDto(
                chapterId = "chapter_003",
                bookId = "1001",
                title = "第三章 药老",
                content = "就在萧炎绝望之际，他手指上的古朴戒指突然发出了一阵微弱的黑光。\n\n" +
                        "一道苍老的声音在萧炎的脑海中响起：\n\n" +
                        "小子，想恢复实力吗？想重新站在巅峰吗？\n\n" +
                        "萧炎猛地坐起，震惊地环顾四周，却看不到任何人。那声音仿佛直接在他脑海中响起，带着一种古老而神秘的气息。\n\n" +
                        "谁？是谁在说话？萧炎警惕地问道，手已经握住了床头的短剑。\n\n" +
                        "呵呵，别紧张，小子。我在你手上的戒指里。那苍老的声音带着一丝笑意，你这三年来一直戴着它，现在才想起问我是谁？\n\n" +
                        "萧炎低头看向手指上的古朴戒指，心中震惊不已。这枚母亲留给他的遗物，三年来他一直戴着，从未离身。他万万没想到，戒指里竟然藏着一个人。\n\n" +
                        "你到底是谁？为什么会在戒指里？萧炎一连串地问道，心中充满了疑惑和期待。如果这个神秘人真的能帮他恢复实力，那这三年来的一切屈辱，都可以洗刷了。\n\n" +
                        "苍老的声音叹了口气，说道：我叫药尘，你可以叫我药老。我曾经是斗气大陆上最顶尖的炼药师之一，也是一名斗尊强者。但在一次意外中，我失去了肉身，灵魂躲进了这枚戒指中沉睡。\n\n" +
                        "三年前，我在戒指中苏醒，但灵魂力量太弱，需要吸收斗之气来恢复。所以，这三年来，我一直在吸收你修炼的斗之气。这也是你斗之气消失的罪魁祸首。\n\n" +
                        "萧炎听到这里，心中既愤怒又无奈。原来，这三年来的痛苦，都是因为戒指里的这个家伙。但如果不被吸收斗之气，他可能永远不会知道戒指的秘密。\n\n" +
                        "药老继续说道：不过，这三年来，你的经脉在斗之气的反复冲刷下，变得更加坚韧宽阔。这对于你未来的修炼，有着巨大的好处。而且，作为补偿，我会传授你炼药术和功法。\n\n" +
                        "炼药师！萧炎心中一震。在斗气大陆上，炼药师是最尊贵的职业之一。他们能够炼制各种神奇的丹药，帮助斗者提升修为，甚至起死回生。\n\n" +
                        "一名高级的炼药师，地位甚至不亚于一名斗皇强者。无数强者愿意为他们卖命，只为求得一枚丹药。而成为炼药师的条件极为苛刻，需要极高的灵魂天赋，百万人中难出一个。\n\n" +
                        "从今天起，你就是我的弟子。药老的声音变得严肃起来，我会传授你焚诀，这是一套可以进化的功法。它能够吞噬异火来进化，最终达到天阶功法的层次。\n\n" +
                        "萧炎充满期待，异火，那是天地间最神奇的力量之一。每一种异火都有毁天灭地的威力，能够吞噬异火的人，无一不是斗气大陆上的顶尖强者。\n\n" +
                        "接下来的日子里，萧炎开始了艰苦的修炼。白天，他按照药老的指导，吸纳天地间的斗之气；晚上，他在药老的教导下，学习炼药术的基础知识。\n\n" +
                        "一个月后，萧炎成功炼制出了第一枚丹药——一品回血丹。虽然只是一品丹药，但对于初学者来说，这已经是很大的成就了。\n\n" +
                        "药老满意地点点头：不错，你的进步比我想象的还要快。按照这个速度，三个月后，你就能恢复到六段斗之气，半年后，你就能重新突破斗者。\n\n" +
                        "萧炎充满期待，他终于看到了希望。三年后，他将不再是那个任人羞辱的废物，而是一个真正的强者。",
                index = 3,
                wordCount = 3500,
                updateTime = "2024-01-15 11:30:00",
                isVip = true
            )
        )
    }

    fun generateMockChapter(index: Int): ChapterDto {
        val chapter = generateMockChapters()
        if (index < 0 || index >= chapter.size) {
            return ChapterDto()
        }
        return chapter[index]
    }
}