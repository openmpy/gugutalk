package com.pidulgi.server.chat.repository.impl

import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.linecorp.kotlinjdsl.querymodel.jpql.entity.Entities.entity
import com.linecorp.kotlinjdsl.querymodel.jpql.path.Paths.path
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderer
import com.pidulgi.server.chat.entity.ChatRoom
import com.pidulgi.server.chat.entity.ChatRoomMember
import com.pidulgi.server.chat.entity.Message
import com.pidulgi.server.chat.repository.ChatRoomCustomRepository
import com.pidulgi.server.chat.repository.dto.ChatRoomItemResponse
import com.pidulgi.server.member.entity.Member
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class ChatRoomCustomRepositoryImpl(

    private val entityManager: EntityManager,
    private val jpqlRenderContext: JpqlRenderContext,
) : ChatRoomCustomRepository {

    private val renderer = JpqlRenderer()

    override fun findMemberByCursor(
        memberId: Long,
        cursorId: Long?,
        cursorDate: LocalDateTime?,
        size: Int,
    ): List<ChatRoomItemResponse> {
        val my = entity(ChatRoomMember::class, "my")
        val other = entity(ChatRoomMember::class, "other")

        val query = jpql {
            val cursorCondition = if (cursorId != null && cursorDate != null) {
                or(
                    path(ChatRoom::lastMessageAt).lt(cursorDate),
                    and(
                        path(ChatRoom::lastMessageAt).eq(cursorDate),
                        path(ChatRoom::id).lt(cursorId)
                    )
                )
            } else null

            selectNew<ChatRoomItemResponse>(
                path(ChatRoom::id),
                path(other, ChatRoomMember::memberId),
                path(Member::profileKey),
                path(Member::nickname),
                path(Message::content),
                path(ChatRoom::lastMessageAt),
            ).from(
                entity(ChatRoom::class),
                join(my).on(
                    path(my, ChatRoomMember::chatRoomId).eq(path(ChatRoom::id))
                        .and(path(my, ChatRoomMember::memberId).eq(memberId))
                ),
                join(other).on(
                    path(other, ChatRoomMember::chatRoomId).eq(path(ChatRoom::id))
                        .and(path(other, ChatRoomMember::memberId).ne(memberId))
                ),
                join(Member::class).on(
                    path(Member::id).eq(path(other, ChatRoomMember::memberId))
                ),
                leftJoin(Message::class).on(
                    path(Message::id).eq(path(ChatRoom::lastMessageId))
                ),
            ).whereAnd(
                cursorCondition,
            ).orderBy(
                path(ChatRoom::lastMessageAt).desc().nullsLast(),
                path(ChatRoom::id).desc(),
            )
        }

        val rendered = renderer.render(query, jpqlRenderContext)
        val jpaQuery = entityManager.createQuery(rendered.query, ChatRoomItemResponse::class.java)

        rendered.params.forEach { (name, value) ->
            jpaQuery.setParameter(name, value)
        }
        return jpaQuery.setMaxResults(size).resultList
    }
}