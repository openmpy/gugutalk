package com.pidulgi.server.member.entity

import com.pidulgi.server.member.entity.type.Gender
import jakarta.persistence.*
import org.hibernate.annotations.SQLRestriction
import org.locationtech.jts.geom.Point
import java.time.LocalDateTime

@SQLRestriction("deleted_at IS NULL")
@Entity
@Table(name = "member")
class Member(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "uuid", nullable = false)
    val uuid: String,

    @Column(name = "phone_number", nullable = false)
    val phoneNumber: String,

    @Column(name = "password", nullable = false)
    val password: String,

    @Column(name = "profile_key", nullable = true)
    var profileKey: String? = null,

    @Column(name = "nickname", nullable = false)
    var nickname: String,

    @Column(name = "birth_year", nullable = false)
    var birthYear: Int = 2000,

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    val gender: Gender,

    @Column(name = "bio", nullable = true)
    var bio: String? = null,

    @Column(name = "comment", nullable = true)
    var comment: String? = "반갑습니다.",

    @Column(columnDefinition = "geography(Point,4326)")
    var location: Point? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "deleted_at", nullable = true)
    var deletedAt: LocalDateTime? = null
) {

    fun activate(profileKey: String?, nickname: String, birthYear: Int, bio: String?) {
        this.profileKey = profileKey
        this.nickname = nickname
        this.birthYear = birthYear
        this.bio = bio
    }

    fun withdraw() {
        this.deletedAt = LocalDateTime.now()
    }

    fun bump(location: Point?) {
        this.location = location
        this.updatedAt = LocalDateTime.now()
    }

    fun updateLocation(location: Point?) {
        this.location = location
        this.updatedAt = LocalDateTime.now()
    }

    fun updateComment(comment: String) {
        this.comment = comment
        this.updatedAt = LocalDateTime.now()
    }

    fun updateProfile(profileKey: String?, nickname: String, birthYear: Int, bio: String?) {
        this.profileKey = profileKey
        this.nickname = nickname
        this.birthYear = birthYear
        this.bio = bio
        this.updatedAt = LocalDateTime.now()
    }
}