package com.pidulgi.server.chat.entity

import jakarta.persistence.*
import org.hibernate.annotations.SQLRestriction
import java.time.LocalDateTime

@SQLRestriction("deleted_at IS NULL")
@Entity
@Table(name = "chat_room")
class ChatRoom(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "member1_id", nullable = false)
    val member1Id: Long,

    @Column(name = "member2_id", nullable = false)
    val member2Id: Long,

    @Column(name = "last_message")
    val lastMessage: String? = null,

    @Column(name = "member1_last_read_message_id")
    val member1LastReadMessageId: Long? = null,

    @Column(name = "member2_last_read_message_id")
    val member2LastReadMessageId: Long? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "deleted_at")
    val deletedAt: LocalDateTime? = null,
) {

    companion object {
        fun of(memberA: Long, memberB: Long): ChatRoom {
            val (member1, member2) = if (memberA < memberB) {
                memberA to memberB
            } else {
                memberB to memberA
            }

            return ChatRoom(
                member1Id = member1,
                member2Id = member2
            )
        }
    }
}