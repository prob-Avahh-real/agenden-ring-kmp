// HomeScreen.kt — agenden-ring home with agenda ring
package com.agendenring.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agendenring.model.DayAgenda
import com.agendenring.model.Task
import com.agendenring.model.TaskCategory
import kotlin.math.*

@Composable
fun HomeScreen(agenda: DayAgenda, onTaskTap: (Task) -> Unit) {
    val animation = remember { Animatable(0f) }
    LaunchedEffect(Unit) { animation.animateTo(1f, animationSpec = tween(1200, easing = FastOutSlowInEasing)) }

    Column(Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {

        AgendaRing(agenda, animation.value, size = 300f, onTaskTap = onTaskTap)

        Spacer(Modifier.height(32.dp))

        // Task list
        agenda.tasks.sortedBy { it.startHour * 60 + it.startMinute }.forEach { task ->
            TaskRow(task) { onTaskTap(task) }
        }
    }
}

@Composable
fun AgendaRing(agenda: DayAgenda, anim: Float, size: Float, onTaskTap: (Task) -> Unit) {
    Canvas(
        modifier = Modifier.size(size.dp)
            .pointerInput(agenda) {
                detectTapGestures { offset ->
                    val center = Offset(size / 2, size / 2)
                    val dx = offset.x - center.x
                    val dy = offset.y - center.y
                    val dist = sqrt(dx * dx + dy * dy)

                    val outerRadius = size / 2 - 8
                    val ringWidth = outerRadius * 0.22f
                    val trackRadius = outerRadius - ringWidth / 2
                    val innerEdge = trackRadius - ringWidth * 0.5f
                    val outerEdge = trackRadius + ringWidth * 0.5f

                    if (dist < innerEdge || dist > outerEdge) return@detectTapGestures

                    var angle = atan2(dy.toDouble(), dx.toDouble()) + PI / 2
                    if (angle < 0) angle += 2 * PI
                    val tapDeg = (angle * 180 / PI)

                    for (task in agenda.tasks) {
                        val start = task.startAngle
                        val sweep = task.sweepAngle
                        val end = start + sweep
                        if (tapDeg >= start && tapDeg <= end) {
                            onTaskTap(task)
                            return@detectTapGestures
                        }
                    }
                }
            }
    ) {
        val center = Offset(size / 2, size / 2)
        val outerRadius = size / 2 - 8
        val ringWidth = outerRadius * 0.22f
        val trackRadius = outerRadius - ringWidth / 2

        // Background track
        drawCircle(Color(0xFF2A2A3A), trackRadius, style = Stroke(ringWidth))

        // Completion progress
        if (agenda.completionRate > 0) {
            drawArc(
                Brush.sweepGradient(listOf(Color(0xFFFF9500), Color(0xFF4A90D9))),
                startAngle = -90f,
                sweepAngle = (360f * agenda.completionRate * anim).toFloat(),
                false,
                topLeft = center + Offset(-trackRadius + ringWidth * 0.15f, -trackRadius + ringWidth * 0.15f),
                size = Size((trackRadius - ringWidth * 0.15f) * 2, (trackRadius - ringWidth * 0.15f) * 2),
                style = Stroke(ringWidth * 0.3f, cap = StrokeCap.Round)
            )
        }

        // Task segments
        for (task in agenda.tasks) {
            val color = Color(task.category.colorValue)
            val alpha = if (task.isCompleted) 0.3f else 0.85f
            val startRad = ((task.startAngle * PI / 180) - PI / 2).toFloat()
            val sweepRad = ((task.sweepAngle * anim * PI / 180).toFloat()).coerceAtMost(2 * PI.toFloat())

            drawArc(
                color.copy(alpha = alpha),
                startAngle = Math.toDegrees(startRad.toDouble()).toFloat(),
                sweepAngle = Math.toDegrees(sweepRad.toDouble()).toFloat(),
                false,
                topLeft = center + Offset(-trackRadius, -trackRadius),
                size = Size(trackRadius * 2, trackRadius * 2),
                style = Stroke(ringWidth * 0.65f)
            }
        }

        // Current time pointer
        val currentRad = ((Task.currentAngle() * PI / 180) - PI / 2).toFloat()
        val pointerX = center.x + (trackRadius - ringWidth / 2) * cos(currentRad)
        val pointerY = center.y + (trackRadius - ringWidth / 2) * sin(currentRad)
        drawCircle(Color(0xFFFF9500), 4.5f, Offset(pointerX, pointerY))
        drawCircle(Color(0xFFFF9500).copy(alpha = 0.2f), 8f, Offset(pointerX, pointerY), style = Stroke(2f))
    }
}

@Composable
fun TaskRow(task: Task, onClick: () -> Unit) {
    val color = Color(task.category.colorValue)
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
        color = color.copy(alpha = if (task.isCompleted) 0.1f else 0.15f)
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(categoryIcon(task.category), fontSize = 20.sp)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(task.title, fontWeight = FontWeight.SemiBold,
                    color = if (task.isCompleted) Color.Gray else Color.White)
                Text(
                    "${"%02d".format(task.startHour)}:${"%02d".format(task.startMinute)} - "
                            + "%02d".format(task.endHour) + ":%02d".format(task.endMinute),
                    color = Color.Gray, fontSize = 12.sp)
            }
            if (task.isCompleted) Text("✅", fontSize = 18.sp)
            else Checkbox(checked = false, onCheckedChange = null, colors = CheckboxDefaults.colors(checkedColor = color))
        }
    }
}

fun categoryIcon(cat: TaskCategory): String = when (cat) {
    TaskCategory.Work -> "💼"
    TaskCategory.Personal -> "🏠"
    TaskCategory.Health -> "💪"
    TaskCategory.Study -> "📚"
    TaskCategory.Social -> "🎉"
    TaskCategory.Other -> "📌"
}
