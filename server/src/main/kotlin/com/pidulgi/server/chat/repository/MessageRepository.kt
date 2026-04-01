package com.pidulgi.server.chat.repository

import com.pidulgi.server.chat.entity.Message
import org.springframework.data.jpa.repository.JpaRepository

interface MessageRepository : JpaRepository<Message, Long>