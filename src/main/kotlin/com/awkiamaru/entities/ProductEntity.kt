package com.awkiamaru.entities

import io.micronaut.core.annotation.Introspected
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect.POSTGRES
import io.micronaut.data.repository.CrudRepository
import io.micronaut.serde.annotation.Serdeable
import java.util.UUID

@Serdeable
@MappedEntity("product")
data class ProductEntity(
  @field:Id
  var id: UUID = UUID.randomUUID(),
  val name: String,
  val stock: Int
)


@JdbcRepository(dialect = POSTGRES)
interface ProductRepository: CrudRepository<ProductEntity, UUID> {
  fun findByIdIn(ids: List<UUID>): List<ProductEntity>
}