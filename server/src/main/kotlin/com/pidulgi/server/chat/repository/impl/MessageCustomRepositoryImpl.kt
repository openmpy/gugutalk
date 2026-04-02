package com.pidulgi.server.chat.repository.impl

import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.linecorp.kotlinjdsl.querymodel.jpql.entity.Entities.entity
import com.linecorp.kotlinjdsl.querymodel.jpql.path.Paths.path
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderer
import com.pidulgi.server.chat.entity.ChatRoomMember
import com.pidulgi.server.chat.entity.Message
import com.pidulgi.server.chat.repository.MessageCustomRepository
import com.pidulgi.server.chat.repository.dto.MessageItemResponse
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class MessageCustomRepositoryImpl(

    private val entityManager: EntityManager,
    private val jpqlRenderContext: JpqlRenderContext,
) : MessageCustomRepository {

    private val renderer = JpqlRenderer()

    override fun findAllChatRoomByCursor(
        memberId: Long,
        chatRoomId: Long,
        cursorId: Long?,
        cursorDate: LocalDateTime?,
        size: Int,
    ): List<MessageItemResponse> {
        val other = entity(ChatRoomMember::class, "other")

        val query = jpql {
            val cursorCondition = if (cursorId != null && cursorDate != null) {
                or(
                    path(Message::createdAt).lt(cursorDate),
                    and(
                        path(Message::createdAt).eq(cursorDate),
                        path(Message::id).lt(cursorId)
                    )
                )
            } else null

            selectNew<MessageItemResponse>(
                path(Message::id),
                path(Message::chatRoomId),
                path(Message::senderId),
                path(other, ChatRoomMember::memberId),
                path(Message::content),
                path(Message::type),
                path(Message::createdAt),
            ).from(
                entity(Message::class),
                join(other).on(
                    path(other, ChatRoomMember::chatRoomId).eq(path(Message::chatRoomId))
                        .and(path(other, ChatRoomMember::memberId).ne(memberId))
                ),
            ).whereAnd(
                path(Message::chatRoomId).eq(chatRoomId),
                cursorCondition,
            ).orderBy(
                path(Message::createdAt).desc(),
                path(Message::id).desc(),
            )
        }

        val rendered = renderer.render(query, jpqlRenderContext)
        val jpaQuery = entityManager.createQuery(rendered.query, MessageItemResponse::class.java)

        rendered.params.forEach { (name, value) ->
            jpaQuery.setParameter(name, value)
        }
        return jpaQuery.setMaxResults(size).resultList
    }
}