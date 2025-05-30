package com.awkiamaru

import io.micronaut.data.connection.jdbc.operations.DefaultDataSourceConnectionOperations
import jakarta.inject.Singleton

@Singleton
class DatabaseCleanUpService(private val dataSource: DefaultDataSourceConnectionOperations) {
  operator fun invoke(excludeTables: List<String> = emptyList()) {
    dataSource.write(
      queries =
        mutableListOf<String>().apply {
          addAll(
            getTablesNames()
              .filterNot { excludeTables.contains(it.lowercase()) }
              .map { DELETE_TABLE_QUERY.format(it) }
          )
        }
    )
  }

  private fun getTablesNames() =
    TABLE_NAMES.ifEmpty { dataSource.read(TABLES_NAMES_QUERY).also { TABLE_NAMES.addAll(it) } }

  companion object {
    private const val DELETE_TABLE_QUERY = "DELETE FROM \"%s\";"

    private const val TABLES_NAMES_QUERY =
      """
             SELECT
                 table_name
             FROM
                 information_schema.tables
             WHERE
                     table_schema = current_schema()
                 AND table_type = 'BASE TABLE'
                 AND table_name NOT IN ('flyway_schema_history');
             """
    private val TABLE_NAMES = mutableListOf<String>()
  }
}


fun DefaultDataSourceConnectionOperations.read(query: String): List<String> =
  executeRead {
    it.connection.createStatement().executeQuery(query).use {
      generateSequence { if (it.next()) it.getString(1) else null }.toList()
    }
  }

fun DefaultDataSourceConnectionOperations.write(queries: List<String>) {
  executeWrite {
    it.connection.createStatement().apply {
      queries.forEach(::addBatch)
      executeBatch()
    }
  }
}