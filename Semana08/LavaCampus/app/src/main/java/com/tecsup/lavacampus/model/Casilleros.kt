package com.tecsup.lavacampus.model

import java.util.Locale

object Casilleros {
    val todos: List<String> = (1..30).map { String.format(Locale.US, "C-%02d", it) }
}
