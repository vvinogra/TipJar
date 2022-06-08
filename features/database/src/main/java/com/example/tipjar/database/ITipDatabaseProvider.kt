package com.example.tipjar.database

interface ITipDatabaseProvider {
    fun close()
    fun get(): TipDatabase
}