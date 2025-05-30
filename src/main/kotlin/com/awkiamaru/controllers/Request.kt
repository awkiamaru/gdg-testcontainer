package com.awkiamaru.controllers

import com.awkiamaru.entities.CustomerEntity
import com.awkiamaru.entities.ProductEntity
import io.micronaut.serde.annotation.Serdeable
import java.util.UUID

sealed class Request {

  @Serdeable
  data class Product(val name: String, val stock: Int) {
    fun toEntity() = ProductEntity(name = name, stock = stock)
  }

  @Serdeable
  data class Customer(val name: String) {
    fun toEntity() = CustomerEntity(name = name)
  }

  @Serdeable
  data class Order(
    val customerId: UUID,
    val products: List<OrderItem>
  )

  @Serdeable
  data class OrderItem(val id: UUID, val quantity: Int)
}