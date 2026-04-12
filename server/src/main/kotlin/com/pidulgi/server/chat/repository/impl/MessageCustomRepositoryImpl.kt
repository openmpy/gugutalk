package com.pidulgi.server.chat.repository.impl

import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderer
import com.pidulgi.server.chat.entity.Message
import com.pidulgi.server.chat.repository.MessageCustomRepository
import com.pidulgi.server.chat.repository.result.MessageItemResult
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class MessageCustomRepositoryImpl(

    private val context: JpqlRenderContext,
    private val entityManager: EntityManager,
) : MessageCustomRepository {

    private val renderer = JpqlRenderer()

    override fun findAllMessagesByCursor(
        chatRoomId: Long,
        cursorId: Long?,
        cursorDate: LocalDateTime?,
        size: Int
    ): List<MessageItemResult> {
        val query = jpql {
            selectNew<MessageItemResult>(
                path(Message::id),
                path(Message::senderId),
                path(Message::content),
                path(Message::type),
                path(Message::createdAt),
            ).from(
                entity(Message::class),
            ).whereAnd(
                path(Message::chatRoomId).eq(chatRoomId),
                if (cursorId != null && cursorDate != null) {
                    or(
                        path(Message::createdAt).lt(cursorDate),
                        and(
                            path(Message::createdAt).eq(cursorDate),
                            path(Message::id).lt(cursorId),
                        ),
                    )
                } else null,
            ).orderBy(
                path(Message::createdAt).desc(),
                path(Message::id).desc(),
            )
        }

        val rendered = renderer.render(query, context)
        val jpaQuery = entityManager.createQuery(rendered.query, MessageItemResult::class.java).apply {
            rendered.params.forEach { (name, value) ->
                setParameter(name, value)
            }
        }
        return jpaQuery.setMaxResults(size).resultList
    }
}