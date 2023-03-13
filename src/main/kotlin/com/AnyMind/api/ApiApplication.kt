package com.AnyMind.api

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ApiApplication

fun main(args: Array<String>) {
    val logger = LoggerFactory.getLogger(ApiApplication::class.java)
    runApplication<ApiApplication>(*args)
    logger.info("Application started.")
}
