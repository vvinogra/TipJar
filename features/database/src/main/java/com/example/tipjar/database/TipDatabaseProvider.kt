package com.example.tipjar.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

private const val APP_DB = "tip_jar_database"

@Singleton
internal class TipDatabaseProvider @Inject constructor(
    @ApplicationContext private val context: Context
): ITipDatabaseProvider {

    @Volatile
    private var appDatabase: TipDatabase? = null

    override fun get(): TipDatabase =
        appDatabase ?: synchronized(this) {
            appDatabase ?: buildDatabase().also {
                appDatabase = it
            }
        }

    override fun close() {
        appDatabase?.close()
        appDatabase = null
    }

    private fun buildDatabase() =
        Room.databaseBuilder(context.applicationContext, TipDatabase::class.java, APP_DB)
            .build()
}
