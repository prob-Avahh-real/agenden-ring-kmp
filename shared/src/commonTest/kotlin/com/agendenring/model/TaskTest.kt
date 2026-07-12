package com.agendenring.model

import kotlin.test.Test
import kotlin.test.assertEquals

class TaskTest {
    @Test
    fun `task at midnight has 0 degree start`() {
        val task = Task("1", "Test", startHour = 0, startMinute = 0, endHour = 1, endMinute = 0,
            category = TaskCategory.Other)
        assertEquals(0.0, task.startAngle, 0.01)
    }

    @Test
    fun `task at 6am has 90 degree start`() {
        val task = Task("1", "Test", startHour = 6, startMinute = 0, endHour = 7, endMinute = 0,
            category = TaskCategory.Other)
        assertEquals(90.0, task.startAngle, 0.01)
    }

    @Test
    fun `task at 12pm has 180 degree start`() {
        val task = Task("1", "Test", startHour = 12, startMinute = 0, endHour = 13, endMinute = 0,
            category = TaskCategory.Other)
        assertEquals(180.0, task.startAngle, 0.01)
    }

    @Test
    fun `one hour task sweeps 15 degrees`() {
        val task = Task("1", "Test", startHour = 9, startMinute = 0, endHour = 10, endMinute = 0,
            category = TaskCategory.Work)
        assertEquals(15.0, task.sweepAngle, 0.01)
    }

    @Test
    fun `30 minute task sweeps 7 point 5 degrees`() {
        val task = Task("1", "Test", startHour = 9, startMinute = 0, endHour = 9, endMinute = 30,
            category = TaskCategory.Work)
        assertEquals(7.5, task.sweepAngle, 0.01)
    }

    @Test
    fun `empty agenda has zero completion`() {
        val agenda = DayAgenda("2026-06-07", emptyList())
        assertEquals(0.0, agenda.completionRate)
    }

    @Test
    fun `half completed agenda`() {
        val agenda = DayAgenda("2026-06-07", listOf(
            Task("1", "A", startHour = 9, startMinute = 0, endHour = 10, endMinute = 0,
                category = TaskCategory.Work, isCompleted = true),
            Task("2", "B", startHour = 10, startMinute = 0, endHour = 11, endMinute = 0,
                category = TaskCategory.Work, isCompleted = false)
        ))
        assertEquals(0.5, agenda.completionRate, 0.01)
    }
}
