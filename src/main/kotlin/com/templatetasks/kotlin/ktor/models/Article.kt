package com.templatetasks.kotlin.ktor.models

import org.jetbrains.exposed.sql.Table

/**
 * @author Vadim Starichkov (starichkovva@gmail.com)
 * @since 2022-05-21 14:30
 */
data class Article(val id: Int, val title: String, val body: String)

object Articles : Table() {
    val id = integer("id").autoIncrement()
    val title = varchar("title", 128)
    val body = varchar("body", 1024)

    override val primaryKey = PrimaryKey(id)
}
