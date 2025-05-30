package com.awkiamaru

import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.utility.DockerImageName

object TestContainersEnvironment {
  private const val POSTGRESQL_IMAGE = "postgres:16-alpine"
  private const val KAFKA_IMAGE = "confluentinc/cp-kafka:latest"

  private val kafka = KafkaContainer(DockerImageName.parse(KAFKA_IMAGE)).apply {
    withEmbeddedZookeeper()
    withEnv("KAFKA_AUTO_CREATE_TOPICS_ENABLE", "true")
  }


  private const val POSTGRES_DATABASE = "postgres"
  private val postgres = PostgreSQLContainer(POSTGRESQL_IMAGE).apply {
    withDatabaseName(POSTGRES_DATABASE)
  }

  val kafkaBootstrapServers: String
    get() = kafka.bootstrapServers

  val jdbcUrl
    get() = postgres.jdbcUrl

  val driveClassName
    get() = postgres.driverClassName

  val username
    get() = postgres.username

  val password
    get() = postgres.password

  val databaseName
    get() = POSTGRES_DATABASE

  fun start() {
    postgres.start()
    kafka.start()
  }

  fun stop() {
    if (kafka.isRunning) {
      kafka.stop()
    }

    if (postgres.isRunning) {
      postgres.stop()
    }
  }
}