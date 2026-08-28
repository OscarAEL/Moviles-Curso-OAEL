package com.oscar.lab02eneque

fun main() {
    println("=========================================")
    println("          SISTEMA DE MATRÍCULA")
    println("=========================================")

    print("Nombre del estudiante: ")
    val nombreEstudiante = readLine() ?: ""

    print("Cantidad de cursos: ")
    val cantidadCursos = readLine()!!.toInt()

    println()

    for (i in 1..cantidadCursos) {
        println("Curso $i")

        print("Nombre del curso: ")
        val nombreCurso = readLine() ?: ""

        print("Cantidad de creditos: ")
        val creditos = readLine()!!.toInt()

        print("Valor por credito: S/ ")
        val valorCredito = readLine()!!.toDouble()

        println()
    }

    println("Datos ingresados correctamente.")

}
