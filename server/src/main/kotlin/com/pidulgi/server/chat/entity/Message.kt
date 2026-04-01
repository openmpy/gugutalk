package com.pidulgi.server.chat.entity

import com.pidulgi.server.chat.entity.type.MessageType
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "message")
class Message(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "chat_room_id", nullable = false)
    val chatRoomId: Long,

    @Column(name = "sender_id", nullable = false)
    val senderId: Long,

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    val content: String,

    @Column(name = "type", nullable = false)
    val type: MessageType = MessageType.TEXT,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
)