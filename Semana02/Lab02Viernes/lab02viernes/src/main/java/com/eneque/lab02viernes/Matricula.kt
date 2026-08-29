package com.eneque.lab02viernes

fun main() {
    println("==============================================")
    println("              SISTEMA DE MATRICULA")
    println("==============================================")

    print("Nombre del estudiante: ")
    val nombreEstudiante = readLine() ?: ""

    print("Cantidad de cursos: ")
    val cantidadCursos = readLine()!!.toInt()

    val nombresCursos = mutableListOf<String>()
    val creditosCursos = mutableListOf<Int>()
    val valoresCredito = mutableListOf<Double>()
    val costosCursos = mutableListOf<Double>()

    println()

    for (i in 1..cantidadCursos) {
        println("Curso $i")

        print("Nombre del curso: ")
        val nombreCurso = readLine() ?: ""

        print("Cantidad de creditos: ")
        val creditos = readLine()!!.toInt()

        print("Valor por credito: S/ ")
        val valorCredito = readLine()!!.toDouble()

        val costoCurso = creditos * valorCredito

        nombresCursos.add(nombreCurso)
        creditosCursos.add(creditos)
        valoresCredito.add(valorCredito)
        costosCursos.add(costoCurso)

        println()
    }

    var totalCreditos = 0
    var totalPagar = 0.0

    for (i in 0 until cantidadCursos) {
        totalCreditos += creditosCursos[i]
        totalPagar += costosCursos[i]
    }

    val cargaAcademica = when {
        totalCreditos <= 12 -> "Matricula Regular"
        totalCreditos <= 18 -> "Carga Completa"
        else -> "Requiere Autorizacion"
    }

    val cantidadCuotas: Int

    if (totalPagar > 2500) {
        cantidadCuotas = 3
    } else {
        cantidadCuotas = 2
    }

    val valorCuota = totalPagar / cantidadCuotas

    println()
    println("==============================================")
    println("             RESUMEN DE MATRICULA")
    println("==============================================")
    println("Estudiante: $nombreEstudiante")
    println()

    println(
        String.format(
            "%-25s %10s %15s",
            "Curso", "Creditos", "Costo"
        )
    )

    println("----------------------------------------------")

    for (i in 0 until cantidadCursos) {
        println(
            String.format(
                "%-25s %10d %15.2f",
                nombresCursos[i],
                creditosCursos[i],
                costosCursos[i]
            )
        )
    }

    println("----------------------------------------------")
    println()

    println("Cursos matriculados: $cantidadCursos")
    println("Total de creditos: $totalCreditos")
    println(String.format("TOTAL A PAGAR: S/ %.2f", totalPagar))
    println("Carga academica: $cargaAcademica")
    println(
        String.format(
            "Forma de pago: %d cuotas de S/ %.2f",
            cantidadCuotas,
            valorCuota
        )
    )

    println("==============================================")

}
