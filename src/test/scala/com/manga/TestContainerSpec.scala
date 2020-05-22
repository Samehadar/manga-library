package com.manga

import java.sql.DriverManager

import com.dimafeng.testcontainers.{ForAllTestContainer, PostgreSQLContainer}
import org.scalatest.FlatSpec

class TestContainerSpec extends FlatSpec with ForAllTestContainer {

  override val container = PostgreSQLContainer()

  it should "download and setup PostgreSQL test container" in {
    Class.forName(container.driverClassName)
    val connection = DriverManager.getConnection(container.jdbcUrl, container.username, container.password)

    connection
  }
}