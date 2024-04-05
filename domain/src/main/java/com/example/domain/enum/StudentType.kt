package com.example.domain.enum

enum class StudentType {
    SCHOOL, UNIVERSITY;
    override fun toString() = when (this) {
        SCHOOL -> "HIGHSCHOOL"
        UNIVERSITY -> "UNIVERSITY"
    }
}
