package com.awkiamaru.controllers

import com.awkiamaru.entities.CustomerRepository
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import java.util.UUID

@Controller("/customers")
class CustomerController(
  private val customerRepository: CustomerRepository
) {

  @Post
  fun create(@Body body: Request.Customer) = body.toEntity().run(customerRepository::save)

  @Get("/{customerId}")
  fun findById(customerId: UUID) = customerRepository.findById(customerId)
}