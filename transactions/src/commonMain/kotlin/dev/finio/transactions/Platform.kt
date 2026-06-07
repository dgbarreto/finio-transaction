package dev.finio.transactions

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform