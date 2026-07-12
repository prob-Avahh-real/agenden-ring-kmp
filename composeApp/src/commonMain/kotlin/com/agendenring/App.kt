// App.kt — agenden-ring entry point
package com.agendenring

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import com.agendenring.ui.screens.HomeScreen
import com.agendenring.model.DayAgenda
import com.agendenring.model.Task
import com.agendenring.model.TaskCategory

@Composable
fun App() {
    MaterialTheme {
        // TODO: load from persistence
        val agenda = remember {
            DayAgenda(
                dateKey = "2026-06-07",
                tasks = listOf(
                    Task("1", "晨会", startHour = 9, startMinute = 0, endHour = 10, endMinute = 0,
                        category = TaskCategory.Work),
                    Task("2", "午餐", startHour = 12, startMinute = 0, endHour = 13, endMinute = 0,
                        category = TaskCategory.Personal),
                    Task("3", "健身", startHour = 18, startMinute = 0, endHour = 19, endMinute = 30,
                        category = TaskCategory.Health),
                    Task("4", "读书", startHour = 20, startMinute = 0, endHour = 21, endMinute = 0,
                        category = TaskCategory.Study)
                )
            )
        }

        HomeScreen(agenda = agenda, onTaskTap = { /* show task detail */ })
    }
}
