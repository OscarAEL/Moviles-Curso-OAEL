package com.eneque.lab05navegacion.data

import androidx.annotation.DrawableRes
import com.eneque.lab05navegacion.R

data class Student(
    val id: Int,
    val name: String,
    val career: String,
    val email: String,
    val phone: String = "+51 987 654 321",
    val studentId: String,
    val faculty: String,
    val cycle: String,
    val bio: String,
    val initials: String,
    @param:DrawableRes val imageRes: Int = R.drawable.oscar_eneque,
)

object StudentRepository {

    val currentUser = Student(
        id = 1,
        name = "Oscar Eneque Lluen",
        career = "Diseño y Desarrollo de Software",
        email = "oscar.eneque.l@tecsup.edu.pe",
        phone = "+51 987 654 321",
        studentId = "2024-0001",
        faculty = "Tecnología Digital",
        cycle = "IV Ciclo",
        bio = "Estudiante de Tecsup enfocado en el desarrollo de aplicaciones móviles con Jetpack Compose y Kotlin.",
        initials = "OE",
        imageRes = R.drawable.oscar_eneque,
    )

    val students = listOf(
        currentUser,
        Student(
            id = 2,
            name = "Ana Lucía Gómez",
            career = "Diseño y Desarrollo de Software",
            email = "ana.gomez@tecsup.edu.pe",
            phone = "+51 912 345 678",
            studentId = "2024-0002",
            faculty = "Tecnología Digital",
            cycle = "IV Ciclo",
            bio = "Especialista en experiencia de usuario (UX/UI) y prototipado de aplicaciones móviles interactivas.",
            initials = "AG",
            imageRes = R.drawable.ana_gomez,
        ),
        Student(
            id = 3,
            name = "Carlos Mendoza Ríos",
            career = "Redes y Comunicaciones",
            email = "carlos.mendoza@tecsup.edu.pe",
            phone = "+51 923 456 789",
            studentId = "2024-0003",
            faculty = "Tecnología Digital",
            cycle = "V Ciclo",
            bio = "Interesado en ciberseguridad, infraestructura de red y administración de servicios en la nube.",
            initials = "CM",
            imageRes = R.drawable.carlos_mendoza,
        ),
        Student(
            id = 4,
            name = "María Fernanda Torres",
            career = "Big Data y Analítica",
            email = "m.torres@tecsup.edu.pe",
            phone = "+51 934 567 890",
            studentId = "2024-0004",
            faculty = "Tecnología Digital",
            cycle = "III Ciclo",
            bio = "Enfocada en procesamiento masivo de datos, aprendizaje automático y visualización de métricas.",
            initials = "MT",
            imageRes = R.drawable.maria_torres,
        ),
        Student(
            id = 5,
            name = "Luis Alberto Paredes",
            career = "Diseño y Desarrollo de Software",
            email = "luis.paredes@tecsup.edu.pe",
            phone = "+51 945 678 901",
            studentId = "2024-0005",
            faculty = "Tecnología Digital",
            cycle = "IV Ciclo",
            bio = "Desarrollador backend y móvil con interés en arquitecturas limpias y APIs RESTful.",
            initials = "LP",
            imageRes = R.drawable.luis_paredes,
        ),
        Student(
            id = 6,
            name = "Valeria Vargas Castro",
            career = "Ciberseguridad",
            email = "valeria.vargas@tecsup.edu.pe",
            phone = "+51 956 789 012",
            studentId = "2024-0006",
            faculty = "Tecnología Digital",
            cycle = "VI Ciclo",
            bio = "Investigadora en seguridad web, hacking ético y análisis defensivo de vulnerabilidades.",
            initials = "VV",
            imageRes = R.drawable.valeria_vargas,
        ),
    )

    fun getStudentById(id: Int): Student {
        return students.find { it.id == id } ?: currentUser
    }
}
