package com.pidulgi.server.member.entity

import com.pidulgi.server.member.entity.type.Gender
import com.pidulgi.server.member.entity.type.MemberRole
import com.pidulgi.server.member.entity.vo.*
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

    @Embedded
    @AttributeOverride(
        name = "value",
        column = Column(name = "uuid", nullable = false)
    )
    val uuid: MemberUuid,

    @Embedded
    @AttributeOverride(
        name = "value",
        column = Column(name = "phone_number", nullable = false)
    )
    val phoneNumber: MemberPhoneNumber,

    @Embedded
    @AttributeOverride(
        name = "value",
        column = Column(name = "password", nullable = false)
    )
    val password: MemberPassword,

    @Column(name = "profile_key", nullable = true)
    var profileKey: String? = null,

    @Embedded
    @AttributeOverride(
        name = "value",
        column = Column(name = "nickname", nullable = false)
    )
    var nickname: MemberNickname,

    @Embedded
    @AttributeOverride(
        name = "value",
        column = Column(name = "birth_year", nullable = false)
    )
    var birthYear: MemberBirthYear = MemberBirthYear(2000),

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    val gender: Gender,

    @Column(name = "bio", length = 1000, nullable = true)
    var bio: String? = null,

    @Column(name = "comment", length = 100, nullable = true)
    var comment: String? = "반갑습니다.",

    @Column(columnDefinition = "geography(Point,4326)")
    var location: Point? = null,

    @Column
    var isChatEnabled: Boolean = true,

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    var role: MemberRole = MemberRole.MEMBER,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "deleted_at", nullable = true)
    var deletedAt: LocalDateTime? = null
) {

    fun activate(profileKey: String?, nickname: String, birthYear: Int, bio: String?) {
        this.profileKey = profileKey
        this.nickname = MemberNickname(nickname)
        this.birthYear = MemberBirthYear(birthYear)
        this.bio = bio
    }

    fun withdraw() {
        this.deletedAt = LocalDateTime.now()
    }

    fun bump(location: Point?) {
        this.location = location
        this.updatedAt = LocalDateTime.now()
    }

    fun updateNickname(nickname: String) {
        this.nickname = MemberNickname(nickname)
        this.updatedAt = LocalDateTime.now()
    }

    fun updateBio(string: String) {
        this.bio = string
        this.updatedAt = LocalDateTime.now()
    }

    fun updateComment(comment: String) {
        this.comment = comment
        this.updatedAt = LocalDateTime.now()
    }

    fun updateProfile(profileKey: String?, nickname: String, birthYear: Int, bio: String?) {
        this.profileKey = profileKey
        this.nickname = MemberNickname(nickname)
        this.birthYear = MemberBirthYear(birthYear)
        this.bio = bio
        this.updatedAt = LocalDateTime.now()
    }

    fun toggleChatEnabled() {
        this.isChatEnabled = !isChatEnabled
    }

    fun updateProfileKey(profileKey: String?) {
        this.profileKey = profileKey
    }
}