package com.pidulgi.server.social.repository.impl

import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderer
import com.pidulgi.server.member.entity.Member
import com.pidulgi.server.social.entity.Block
import com.pidulgi.server.social.repository.BlockCustomRepository
import com.pidulgi.server.social.repository.dto.BlockItemResponse
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository

@Repository
class BlockCustomRepositoryImpl(
    private val entityManager: EntityManager,
    private val jpqlRenderContext: JpqlRenderContext,
) : BlockCustomRepository {

    private val renderer = JpqlRenderer()

    override fun findBlocksByCursor(
        blockerId: Long,
        cursorId: Long?,
        size: Int
    ): List<BlockItemResponse> {
        val query = jpql {
            val cursorCondition = if (cursorId != null) {
                path(Block::id).lt(cursorId)
            } else null

            selectNew<BlockItemResponse>(
                path(Block::id),
                path(Member::id),
                path(Member::nickname),
                path(Member::gender),
                path(Member::birthYear),
                path(Member::profileKey),
                path(Block::createdAt),
            ).from(
                entity(Block::class),
                join(Member::class).on(path(Block::blockedId).eq(path(Member::id)))
            ).whereAnd(
                path(Block::blockerId).eq(blockerId),
                cursorCondition
            ).orderBy(
                path(Block::id).desc()
            )
        }

        val rendered = renderer.render(query, jpqlRenderContext)
        val jpaQuery = entityManager.createQuery(rendered.query, BlockItemResponse::class.java)

        rendered.params.forEach { (name, value) ->
            jpaQuery.setParameter(name, value)
        }
        return jpaQuery.setMaxResults(size).resultList
    }
}