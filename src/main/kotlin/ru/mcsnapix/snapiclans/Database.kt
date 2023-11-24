package ru.mcsnapix.snapiclans

import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.transactions.TransactionManager

fun nativeSelect(query: String, fieldsIndex: Map<Expression<*>, Int>): List<ResultRow> {
    val resultRows = mutableListOf<ResultRow>()
    TransactionManager.current().exec(query) { resultSet ->
        while (resultSet.next()) {
            resultRows.add(ResultRow.create(resultSet, fieldsIndex))
        }
    }

    return resultRows
}