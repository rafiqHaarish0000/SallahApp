package dev.kosrat.muslimdata.database.tables.azkars

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 * Azkar chapter table class that will be used as azkar_chapter table.
 */
@Entity(tableName = "azkar_chapter")
internal data class AzkarChapterTable(
    @PrimaryKey val _id: Long,
    @ColumnInfo(name = "category_id") val categoryId: Long
)

/**
 * Azkar chapter translation table class that will be used as azkar_chapter_translation table.
 */
@Entity(tableName = "azkar_chapter_translation")
internal data class AzkarChapterTranslationTable(
    @PrimaryKey val _id: Long,
    @ColumnInfo(name = "chapter_id") val chapterId: Long,
    val language: String,
    @ColumnInfo(name = "chapter_name") val chapterName: String
)
