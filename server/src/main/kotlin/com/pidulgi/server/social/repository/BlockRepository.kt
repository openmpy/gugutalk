package com.pidulgi.server.social.repository

import com.pidulgi.server.social.entity.Block
import org.springframework.data.jpa.repository.JpaRepository

interface BlockRepository : JpaRepository<Block, Long>