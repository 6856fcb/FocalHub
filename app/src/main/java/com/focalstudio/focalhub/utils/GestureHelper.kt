package com.focalstudio.focalhub.utils

import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputScope

suspend fun PointerInputScope.detectTwoFingerSwipeDown(onSwipeDown: () -> Unit) {
    awaitPointerEventScope {
        while (true) {
            val event = awaitPointerEvent(PointerEventPass.Initial)
            if (event.changes.size == 2) {
                val pointer1 = event.changes[0]
                val pointer2 = event.changes[1]

                // Detect the gesture when both fingers move downwards together
                if (pointer1.pressed && pointer2.pressed) {
                    val pointer1Start = pointer1.previousPosition
                    val pointer1End = pointer1.position
                    val pointer2Start = pointer2.previousPosition
                    val pointer2End = pointer2.position

                    if (pointer1End.y - pointer1Start.y > 50 && pointer2End.y - pointer2Start.y > 50) {
                        onSwipeDown()
                        // Consume the event to prevent other gestures from interfering
                        pointer1.consume()
                        pointer2.consume()
                    }
                }
            }
        }
    }
}