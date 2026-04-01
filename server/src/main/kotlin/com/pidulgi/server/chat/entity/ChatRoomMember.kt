package com.pidulgi.server.chat.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "chat_room_member")
class ChatRoomMember(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "chat_room_id", nullable = false)
    val chatRoomId: Long,

    @Column(name = "member_id", nullable = false)
    val memberId: Long,

    @Column(name = "last_read_at")
    val lastReadAt: LocalDateTime? = null,
)