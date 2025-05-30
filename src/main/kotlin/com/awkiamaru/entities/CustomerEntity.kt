package com.awkiamaru.entities

import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect.POSTGRES
import io.micronaut.data.repository.CrudRepository
import io.micronaut.serde.annotation.Serdeable
import java.util.UUID

@Serdeable
@MappedEntity("customer")
data class CustomerEntity(
  @field:Id
  var id: UUID = UUID.randomUUID(),
  val name: String
)

@JdbcRepository(dialect = POSTGRES)
interface CustomerRepository: CrudRepository<CustomerEntity, UUID>