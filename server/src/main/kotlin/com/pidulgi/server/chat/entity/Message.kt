package com.pidulgi.server.chat.entity

import com.pidulgi.server.chat.entity.type.MessageType
import com.pidulgi.server.common.jpa.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "message")
class Message(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "chat_room_id", nullable = false)
    val chatRoomId: Long = 0,

    @Column(name = "sender_id", nullable = false)
    val senderId: Long,

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    val content: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    val type: MessageType,
) : BaseEntity()