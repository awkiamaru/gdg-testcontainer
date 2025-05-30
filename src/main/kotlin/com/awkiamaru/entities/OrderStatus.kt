package com.awkiamaru.entities

import io.micronaut.serde.annotation.Serdeable

@Serdeable
enum class OrderStatus {
  PENDING,
  CONFIRMED,
  CANCELED
}