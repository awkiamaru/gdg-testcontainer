package com.awkiamaru

import io.micronaut.context.env.ActiveEnvironment
import io.micronaut.context.env.PropertySource
import io.micronaut.context.env.PropertySourceLoader
import io.micronaut.core.io.ResourceLoader
import java.io.InputStream
import java.util.Optional

class TestPropertySourceLoader : PropertySourceLoader {
  override fun load(
    resourceName: String?,
    resourceLoader: ResourceLoader?
  ): Optional<PropertySource> {
    return if (resourceName == "application") {
      Optional.of(
        PropertySource.of(
          mapOf(
            DATASOURCE_URL_CONFIG_PATH to TestContainersEnvironment.jdbcUrl,
            DATASOURCE_USERNAME_CONFIG_PATH to TestContainersEnvironment.username,
            DATASOURCE_PASSWORD_CONFIG_PATH to TestContainersEnvironment.password,
            DATASOURCE_DRIVER_CLASS_NAME to TestContainersEnvironment.driveClassName,
            FLYWAY_URL_CONFIG_PATH to TestContainersEnvironment.jdbcUrl,
            FLYWAY_USERNAME_CONFIG_PATH to TestContainersEnvironment.username,
            FLYWAY_PASSWORD_CONFIG_PATH to TestContainersEnvironment.password,
            FLYWAY_DEFAULT_SCHEMA_CONFIG_PATH to TestContainersEnvironment.databaseName,
            KAFKA_BOOTSTRAP_SERVERS_CONFIG_PATH to TestContainersEnvironment.kafkaBootstrapServers
          )
        )
      )
    } else {
      Optional.empty<PropertySource>()
    }
  }

  override fun read(name: String?, input: InputStream?): MutableMap<String, Any> = mutableMapOf()

  override fun loadEnv(
    resourceName: String?,
    resourceLoader: ResourceLoader?,
    activeEnvironment: ActiveEnvironment?
  ): Optional<PropertySource> = Optional.empty()

  companion object {
    const val DATASOURCE_URL_CONFIG_PATH = "datasources.default.url"
    const val DATASOURCE_USERNAME_CONFIG_PATH = "datasources.default.username"
    const val DATASOURCE_PASSWORD_CONFIG_PATH = "datasources.default.password"
    const val DATASOURCE_DRIVER_CLASS_NAME = "datasources.default.driver-class-name"
    const val FLYWAY_URL_CONFIG_PATH = "flyway.datasources.default.url"
    const val FLYWAY_USERNAME_CONFIG_PATH = "flyway.datasources.default.username"
    const val FLYWAY_PASSWORD_CONFIG_PATH = "flyway.datasources.default.password"
    const val FLYWAY_DEFAULT_SCHEMA_CONFIG_PATH = "flyway.datasources.default.default-schema"
    const val KAFKA_BOOTSTRAP_SERVERS_CONFIG_PATH = "kafka.bootstrap.servers"
  }
}
