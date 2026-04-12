package com.pidulgi.server.chat.entity

import com.pidulgi.server.common.exception.CustomException
import com.pidulgi.server.common.jpa.BaseEntity
import jakarta.persistence.*
import org.hibernate.annotations.SQLRestriction
import java.time.LocalDateTime

private const val PREVIEW_MAX_LENGTH = 70

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
    var lastMessage: String? = null,

    @Column(name = "member1_last_read_message_id")
    var member1LastReadMessageId: Long? = null,

    @Column(name = "member2_last_read_message_id")
    var member2LastReadMessageId: Long? = null,

    @Column(name = "member1_unread_count")
    var member1UnreadCount: Int = 0,

    @Column(name = "member2_unread_count")
    var member2UnreadCount: Int = 0,

    @Column(name = "last_message_at")
    var lastMessageAt: LocalDateTime? = null,

    @Column(name = "deleted_at")
    var deletedAt: LocalDateTime? = null,
) : BaseEntity() {

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

    fun delete() {
        this.deletedAt = LocalDateTime.now()
    }

    fun update(lastMessage: String, lastMessageAt: LocalDateTime) {
        val preview = if (lastMessage.length > PREVIEW_MAX_LENGTH) {
            lastMessage.substring(0, PREVIEW_MAX_LENGTH - 3) + "..."
        } else {
            lastMessage
        }

        this.lastMessage = preview
        this.lastMessageAt = lastMessageAt
    }

    fun read(memberId: Long, lastMessageId: Long?) {
        when (memberId) {
            member1Id -> {
                if (lastMessageId != null) {
                    this.member1LastReadMessageId = lastMessageId
                }
                this.member1UnreadCount = 0
            }

            member2Id -> {
                if (lastMessageId != null) {
                    this.member2LastReadMessageId = lastMessageId
                }
                this.member2UnreadCount = 0
            }

            else -> throw CustomException("접근할 수 없는 채팅방입니다.")
        }
    }

    fun hasMember(memberId: Long) = member1Id == memberId || member2Id == memberId

    fun getUnreadCount(memberId: Long) = when (memberId) {
        member1Id -> member1UnreadCount
        else -> member2UnreadCount
    }
}