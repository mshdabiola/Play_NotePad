package com.mshdabiola.editscreen

import com.mshdabiola.common.Time12UserCase
import com.mshdabiola.designsystem.component.toTime
import kotlinx.datetime.LocalTime
import org.junit.Test
import kotlin.test.assertEquals

class Time12UserCaseTest {

    @Test
    fun invoke() {
        val time12UserCase = Time12UserCase()
        (636L).toTime()
        assertEquals("12 : 30 PM", time12UserCase(LocalTime(12, 30)))
        assertEquals("12 : 30 AM", time12UserCase(LocalTime(0, 30)))
        assertEquals("8 : 30 PM", time12UserCase(LocalTime(20, 30)))
        assertEquals("8 : 30 AM", time12UserCase(LocalTime(8, 30)))


    }
}