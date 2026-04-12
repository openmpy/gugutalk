package com.pidulgi.server.member.repository.impl

import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderer
import com.pidulgi.server.member.entity.Member
import com.pidulgi.server.member.entity.PrivateImageGrant
import com.pidulgi.server.member.repository.PrivateImageGrantCustomRepository
import com.pidulgi.server.member.repository.result.PrivateImageGrantItemResult
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository

@Repository
class PrivateImageGrantCustomRepositoryImpl(

    private val context: JpqlRenderContext,
    private val entityManager: EntityManager,
) : PrivateImageGrantCustomRepository {

    private val renderer = JpqlRenderer()

    override fun findAllGrantsByCursor(
        granterId: Long,
        cursorId: Long?,
        size: Int
    ): List<PrivateImageGrantItemResult> {
        val query = jpql {
            selectNew<PrivateImageGrantItemResult>(
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
                cursorId?.let { path(PrivateImageGrant::id).lt(cursorId) }
            ).orderBy(
                path(PrivateImageGrant::id).desc()
            )
        }

        val rendered = renderer.render(query, context)
        val jpaQuery = entityManager.createQuery(rendered.query, PrivateImageGrantItemResult::class.java).apply {
            rendered.params.forEach { (name, value) ->
                setParameter(name, value)
            }
        }
        return jpaQuery.setMaxResults(size).resultList
    }
}