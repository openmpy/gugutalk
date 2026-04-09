package com.pidulgi.server.social.repository.impl

import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderer
import com.pidulgi.server.member.entity.Member
import com.pidulgi.server.social.entity.Like
import com.pidulgi.server.social.repository.LikeCustomRepository
import com.pidulgi.server.social.repository.dto.LikeItemResponse
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository

@Repository
class LikeCustomRepositoryImpl(
    private val entityManager: EntityManager,
    private val jpqlRenderContext: JpqlRenderContext,
) : LikeCustomRepository {

    private val renderer = JpqlRenderer()

    override fun findLikesByCursor(
        likerId: Long,
        cursorId: Long?,
        size: Int
    ): List<LikeItemResponse> {
        val query = jpql {
            val cursorCondition = if (cursorId != null) {
                path(Like::id).lt(cursorId)
            } else null

            selectNew<LikeItemResponse>(
                path(Like::id),
                path(Member::id),
                path(Member::nickname),
                path(Member::gender),
                path(Member::birthYear),
                path(Member::profileKey),
                path(Like::createdAt),
            ).from(
                entity(Like::class),
                join(Member::class).on(path(Like::likedId).eq(path(Member::id)))
            ).whereAnd(
                path(Like::likerId).eq(likerId),
                cursorCondition
            ).orderBy(
                path(Like::id).desc()
            )
        }

        val rendered = renderer.render(query, jpqlRenderContext)
        val jpaQuery = entityManager.createQuery(rendered.query, LikeItemResponse::class.java)

        rendered.params.forEach { (name, value) ->
            jpaQuery.setParameter(name, value)
        }
        return jpaQuery.setMaxResults(size).resultList
    }
}