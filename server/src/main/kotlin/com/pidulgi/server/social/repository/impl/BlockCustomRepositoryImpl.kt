package com.pidulgi.server.social.repository.impl

import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderer
import com.pidulgi.server.member.entity.Member
import com.pidulgi.server.social.entity.Block
import com.pidulgi.server.social.repository.BlockCustomRepository
import com.pidulgi.server.social.repository.dto.BlockItemResult
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository

@Repository
class BlockCustomRepositoryImpl(

    private val context: JpqlRenderContext,
    private val entityManager: EntityManager,
) : BlockCustomRepository {

    private val renderer = JpqlRenderer()

    override fun findBlocksByCursor(
        blockerId: Long,
        cursorId: Long?,
        size: Int
    ): List<BlockItemResult> {
        val query = jpql {
            selectNew<BlockItemResult>(
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
                cursorId?.let { path(Block::id).lt(cursorId) }
            ).orderBy(
                path(Block::id).desc()
            )
        }

        val rendered = renderer.render(query, context)
        val jpaQuery = entityManager.createQuery(rendered.query, BlockItemResult::class.java).apply {
            rendered.params.forEach { (name, value) ->
                setParameter(name, value)
            }
        }
        return jpaQuery.setMaxResults(size).resultList
    }
}