package com.pidulgi.server.member.repository.impl

import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderer
import com.pidulgi.server.member.entity.Member
import com.pidulgi.server.member.entity.PrivateImageGrant
import com.pidulgi.server.member.repository.PrivateImageGrantCustomRepository
import com.pidulgi.server.member.repository.dto.PrivateImageGrantItemResponse
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository

@Repository
class PrivateImageGrantCustomRepositoryImpl(

    private val entityManager: EntityManager,
    private val jpqlRenderContext: JpqlRenderContext,
) : PrivateImageGrantCustomRepository {

    private val renderer = JpqlRenderer()

    override fun findGrantsByCursor(
        granterId: Long,
        cursorId: Long?,
        size: Int
    ): List<PrivateImageGrantItemResponse> {
        val query = jpql {
            val cursorCondition = if (cursorId != null) {
                path(PrivateImageGrant::id).lt(cursorId)
            } else null

            selectNew<PrivateImageGrantItemResponse>(
                path(PrivateImageGrant::id),
                path(Member::id),
                path(Member::nickname),
                path(Member::gender),
                path(Member::birthYear),
                path(Member::profileKey),
                path(PrivateImageGrant::createdAt),
            ).from(
                entity(PrivateImageGrant::class),
                join(Member::class).on(path(PrivateImageGrant::granteeId).eq(path(Member::id)))
            ).whereAnd(
                path(PrivateImageGrant::granterId).eq(granterId),
                cursorCondition
            ).orderBy(
                path(PrivateImageGrant::id).desc()
            )
        }

        val rendered = renderer.render(query, jpqlRenderContext)
        val jpaQuery = entityManager.createQuery(
            rendered.query, PrivateImageGrantItemResponse::class.java
        )

        rendered.params.forEach { (name, value) ->
            jpaQuery.setParameter(name, value)
        }
        return jpaQuery.setMaxResults(size).resultList
    }
}