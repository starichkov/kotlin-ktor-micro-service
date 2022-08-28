package com.templatetasks.kotlin.ktor.dao

import com.templatetasks.kotlin.ktor.models.Article

/**
 * @author Vadim Starichkov (starichkovva@gmail.com)
 * @since 2022-05-21 14:38
 */
interface ArticlesDAO {

    suspend fun allArticles(): List<Article>

    suspend fun article(id: Int): Article?

    suspend fun addNewArticle(title: String, body: String): Article?

    suspend fun editArticle(id: Int, title: String, body: String): Boolean

    suspend fun deleteArticle(id: Int): Boolean
}
