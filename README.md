# Manga Library

It is a http server using:
 [http4s](http://http4s.org/),
 [cats-effects](https://github.com/typelevel/cats-effect),
 [pureconfig](https://github.com/pureconfig/pureconfig),
 [circe](https://github.com/circe/circe),
 [doobie](http://tpolecat.github.io/doobie/),
 [testcontainers](https://www.testcontainers.org/),
 [testcontainers-scala](https://github.com/testcontainers/testcontainers-scala),
 [flyway](https://flywaydb.org/)
 
 Todo: add tapir and OpenAPI.

## To run from docker:

1. sbt stage
2. sbt docker:publishLocal
3. docker run -p 8080:8080 manga-library:0.1

Profit.

## To test

 - sbt run
