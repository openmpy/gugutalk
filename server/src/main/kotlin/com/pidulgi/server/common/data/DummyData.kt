package com.pidulgi.server.common.data

import com.pidulgi.server.member.entity.Member
import com.pidulgi.server.member.entity.PrivateImageGrant
import com.pidulgi.server.member.entity.type.Gender
import com.pidulgi.server.member.repository.MemberRepository
import com.pidulgi.server.member.repository.PrivateImageGrantRepository
import com.pidulgi.server.social.entity.Block
import com.pidulgi.server.social.entity.Like
import com.pidulgi.server.social.repository.BlockRepository
import com.pidulgi.server.social.repository.LikeRepository
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import java.util.*

@Component
class DummyDataInit {

    private val geometryFactory = GeometryFactory()

    // 서울 중심 (37.5665, 126.9780) 기준으로 근처 좌표들
    // 약 1km = 위도/경도 0.009도 차이
    private val locations = listOf(
        Pair(126.9780, 37.5665),   // 서울 시청 (기준)
        Pair(126.9850, 37.5700),   // 약 0.8km
        Pair(126.9900, 37.5750),   // 약 1.5km
        Pair(127.0000, 37.5800),   // 약 2.5km
        Pair(127.0100, 37.5600),   // 약 3.2km
        Pair(126.9600, 37.5500),   // 약 3.8km
        Pair(127.0200, 37.5400),   // 약 5.1km
        Pair(126.9400, 37.5300),   // 약 6.5km
        Pair(127.0400, 37.5200),   // 약 8.3km
        Pair(126.9200, 37.5100),   // 약 9.7km
        Pair(127.0600, 37.5000),   // 약 11.2km
        Pair(126.9000, 37.4900),   // 약 13.0km
        Pair(127.0800, 37.4800),   // 약 14.8km
        Pair(126.8800, 37.4700),   // 약 16.5km
        Pair(127.1000, 37.4600),   // 약 18.3km
        Pair(126.8600, 37.4500),   // 약 20.1km
        Pair(127.1200, 37.4400),   // 약 22.0km
        Pair(126.8400, 37.4300),   // 약 24.2km
        Pair(127.1400, 37.4200),   // 약 26.1km
        Pair(126.8200, 37.4100),   // 약 28.0km
    )

    // 99개 중복 없는 닉네임 (20개 풀 × 반복 없이 조합)
    private val nicknames = listOf(
        "은하수별빛", "커피한잔", "봄바람살랑", "노을빛하늘", "달빛소나타",
        "새벽감성러", "초록숲속길", "바다향기가", "여름소나기", "겨울산책로",
        "하늘구름떼", "별빛여행자", "재즈카페인", "라떼아트왕", "조용한오후",
        "도시의새벽", "산들바람결", "따뜻한봄날", "고요한강변", "반짝이는눈",
        "낙엽밟는길", "홍차한잔씩", "무지개다리", "모닥불앞에", "파도소리에",
        "구름위산책", "꿈꾸는고양이", "연꽃피는곳", "빗소리사랑", "첫눈처럼요",
        "커피향가득", "책읽는오후", "음악과함께", "여행가고파", "고독한미식가",
        "초승달아래", "봄꽃바람결", "햇살가득히", "미소짓는날", "설레는아침",
        "바닐라라떼", "맑은하늘아", "저녁노을빛", "도넛한입에", "피아노선율",
        "풀향기나는", "투명한유리창", "기타치는밤", "새소리아침", "물결따라서",
        "소나타연주", "마음의온도", "창문너머로", "오렌지주스", "꿈속여행자",
        "노란민들레", "파란하늘색", "초코파이맛", "잔잔한음악", "빈티지감성",
        "두근두근봄", "달콤한하루", "별헤는밤에", "바다위돌핀", "지붕위고양",
        "숲속오두막", "첫사랑향기", "주황빛석양", "눈꽃송이야", "드림캐처야",
        "솜사탕구름", "인디고블루", "벚꽃엔딩에", "은은한향기", "잔디밭위에",
        "카라멜마끼", "오후의홍차", "무지개케이크", "모카치노한잔", "달달한사탕",
        "소풍가는날", "행복한점심", "설레는저녁", "고소한참기름", "달빛정원사",
        "여름밤별자리", "봄비맞으며", "가을단풍길", "겨울별빛아래", "새벽한강변",
        "잠못드는밤", "하루의끝에", "내일이기대", "오늘도설레", "반가운미소",
        "따뜻한인사", "즐거운하루", "함께라면OK", "만나서반가워", "잘부탁해요",
    )
    private val bios = listOf(
        "여행을 좋아해요 ✈️",
        "주말엔 등산 다녀요 🏔️",
        "커피 없인 못 살아요 ☕",
        "고양이 집사입니다 🐱",
        "요리하는 걸 즐겨요 🍳",
        "영화 마니아예요 🎬",
        "독서가 취미예요 📚",
        "음악 들으며 드라이브 🎵",
        "운동으로 하루를 시작해요 💪",
        "맛집 탐방이 취미 🍜",
        null,
        null,
    )
    private val comments = listOf(
        "안녕하세요, 잘 부탁드려요!",
        "좋은 인연 만나고 싶어요 😊",
        "반갑습니다~",
        "편하게 말 걸어주세요",
        "천천히 알아가요",
        "잘 부탁해요~",
        "소통 환영해요!",
        "반가워요 :)",
        "먼저 연락 주셔도 좋아요",
        "긍정적인 사람이에요",
    )

    @Bean
    fun init(
        memberRepository: MemberRepository,
        likeRepository: LikeRepository,
        privateImageGrantRepository: PrivateImageGrantRepository,
        blockRepository: BlockRepository,
    ): CommandLineRunner {
        return CommandLineRunner {
            // 회원
            if (memberRepository.count() == 0L) {
                val members = (1 until 100).map { i ->
                    val location = locations[(i - 1) % locations.size].let { (lng, lat) ->
                        val offsetLng = lng + (i / locations.size) * 0.002
                        val offsetLat = lat + (i / locations.size) * 0.001

                        geometryFactory.createPoint(Coordinate(offsetLng, offsetLat)).also {
                            it.srid = 4326
                        }
                    }
                    Member(
                        uuid = UUID.randomUUID().toString(),
                        phoneNumber = "010%04d%04d".format(1000 + i, 1000 + i),
                        password = "1234",
                        nickname = nicknames[i - 1],
                        gender = if (i % 2 == 0) Gender.MALE else Gender.FEMALE,
                        birthYear = 1993 + (i % 12),
                        bio = bios[(i - 1) % bios.size],
                        comment = comments[(i - 1) % comments.size],
                    ).also { it.updateLocation(location) }
                }
                memberRepository.saveAll(members)
                println("회원 데이터가 생성되었습니다. ${memberRepository.count()}")
            }

            // 좋아요
            if (likeRepository.count() == 0L) {
                val likes = (2 until 50).map { i ->
                    Like(
                        likerId = 1,
                        likedId = i.toLong()
                    )
                }

                likeRepository.saveAll(likes)
                println("좋아요 데이터가 생성되었습니다. ${likeRepository.count()}")
            }

            // 비밀 사진
            if (privateImageGrantRepository.count() == 0L) {
                val grants = (2 until 75).map { i ->
                    PrivateImageGrant(
                        granterId = 1,
                        granteeId = i.toLong()
                    )
                }

                privateImageGrantRepository.saveAll(grants)
                println("비밀 사진 권한 데이터가 생성되었습니다. ${privateImageGrantRepository.count()}")
            }

            // 차단
            if (blockRepository.count() == 0L) {
                val blocks = (2 until 100).map { i ->
                    Block(
                        blockerId = 1,
                        blockedId = i.toLong()
                    )
                }

                blockRepository.saveAll(blocks)
                println("차단 데이터가 생성되었습니다. ${blockRepository.count()}")
            }
        }
    }
}