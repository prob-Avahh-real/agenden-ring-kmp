// Task.kt — agenden-ring agenda task model
package com.agendenring.model

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable

enum class TaskCategory(val label: String, val colorValue: Long) {
    Work("工作", 0xFF4A90D9),
    Personal("个人", 0xFF34C759),
    Health("健康", 0xFFFF9500),
    Study("学习", 0xFFAF52DE),
    Social("社交", 0xFFFF2D55),
    Other("其他", 0xFF8E8E93)
}

@Serializable
data class Task(
    val id: String,
    val title: String,
    val description: String? = null,
    val startHour: Int,
    val startMinute: Int,
    val endHour: Int,
    val endMinute: Int,
    val category: TaskCategory,
    val isCompleted: Boolean = false
) {
    // val → angle calculation
    val startAngle: Double get() {
        val totalMin = startHour * 60 + startMinute
        return (totalMin / (24.0 * 60.0)) * 360.0
    }
    val sweepAngle: Double get() {
        val startMin = startHour * 60 + startMinute
        val endMin = endHour * 60 + endMinute
        return ((endMin - startMin) / (24.0 * 60.0)) * 360.0
    }

    companion object {
        fun currentAngle(): Double {
            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            val totalMin = now.hour * 60 + now.minute
            return (totalMin / (24.0 * 60.0)) * 360.0
        }
    }
}

@Serializable
data class DayAgenda(
    val dateKey: String, // "2026-06-07"
    val tasks: List<Task> = emptyList()
) {
    val completionRate: Double get() {
        if (tasks.isEmpty()) return 0.0
        return tasks.count { it.isCompleted }.toDouble() / tasks.size
    }
}
