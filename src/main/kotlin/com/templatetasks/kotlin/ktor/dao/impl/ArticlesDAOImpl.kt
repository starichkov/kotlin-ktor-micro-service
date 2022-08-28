package com.templatetasks.kotlin.ktor.dao.impl

import com.templatetasks.kotlin.ktor.dao.ArticlesDAO
import com.templatetasks.kotlin.ktor.dao.DatabaseFactory.dbQuery
import com.templatetasks.kotlin.ktor.models.*
import org.jetbrains.exposed.sql.*

/**
 * @author Vadim Starichkov (starichkovva@gmail.com)
 * @since 2022-05-21 14:48
 */
class ArticlesDAOImpl : ArticlesDAO {

    private fun resultRowToArticle(row: ResultRow) = Article(
        id = row[Articles.id],
        title = row[Articles.title],
        body = row[Articles.body],
    )

    override suspend fun allArticles(): List<Article> = dbQuery {
        Articles.selectAll().map(::resultRowToArticle)
    }

    override suspend fun article(id: Int): Article? = dbQuery {
        Articles.select { Articles.id eq id }.map(::resultRowToArticle).singleOrNull()
    }

    override suspend fun addNewArticle(title: String, body: String): Article? = dbQuery {
        val insertStatement = Articles.insert {
            it[Articles.title] = title
            it[Articles.body] = body
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToArticle)
    }

    override suspend fun editArticle(id: Int, title: String, body: String): Boolean = dbQuery {
        Articles.update({ Articles.id eq id }) {
            it[Articles.title] = title
            it[Articles.body] = body
        } > 0
    }

    override suspend fun deleteArticle(id: Int): Boolean = dbQuery {
        Articles.deleteWhere { Articles.id eq id } > 0
    }
}