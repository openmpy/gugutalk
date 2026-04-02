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

    @Column(name = "last_message_id")
    var lastMessageId: Long? = null,

    @Column(name = "last_message_at")
    var lastMessageAt: LocalDateTime? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "deleted_at")
    var deletedAt: LocalDateTime? = null,
) {

    fun updateLastMessage(lastMessageId: Long, lastMessageAt: LocalDateTime) {
        this.lastMessageId = lastMessageId
        this.lastMessageAt = lastMessageAt
    }

    fun delete() {
        this.deletedAt = LocalDateTime.now()
    }
}