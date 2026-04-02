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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    val chatRoom: ChatRoom,

    @Column(name = "sender_id", nullable = false)
    val senderId: Long,

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    val content: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    val type: MessageType,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
)