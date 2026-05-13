package com.keikuethas.irlhideandseek.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.keikuethas.irlhideandseek.Ability
import com.keikuethas.irlhideandseek.Intel
import com.keikuethas.irlhideandseek.PersonalBomb
import com.keikuethas.irlhideandseek.R
import com.keikuethas.irlhideandseek.SafeHouse
import com.keikuethas.irlhideandseek.SafeMansion
import com.keikuethas.irlhideandseek.Scan
import com.keikuethas.irlhideandseek.Shield
import com.keikuethas.irlhideandseek.Snare
import com.keikuethas.irlhideandseek.Trap
import kotlin.reflect.KClass

@Composable
fun KClass<out Ability>.name(): String = when (this) {
    Intel::class -> stringResource(R.string.IntelName)
    PersonalBomb::class -> stringResource(R.string.PersonalBombName)
    SafeHouse::class -> stringResource(R.string.SafeHouseName)
    SafeMansion::class -> stringResource(R.string.SafeMansionName)
    Scan::class -> stringResource(R.string.ScanName)
    Shield::class -> stringResource(R.string.ShieldName)
    Snare::class -> stringResource(R.string.SnareName)
    Trap::class -> stringResource(R.string.TrapName)
    else -> this.toString()
}

val Ability.name: String
    @Composable
    get() = this::class.name()

fun KClass<out Ability>.description(): String = when (this) {
    Intel::class -> null
    PersonalBomb::class -> null
    SafeHouse::class -> null
    SafeMansion::class -> null
    Scan::class -> null
    Shield::class -> null
    Snare::class -> null
    Trap::class -> null
    else -> null
} ?: "not implemented yet"

// todo
// resource
val Ability.description: String
    get() = this::class.description()


fun KClass<out Ability>.color(): Color = when (this) {
    Intel::class -> null
    PersonalBomb::class -> null
    SafeHouse::class -> null
    SafeMansion::class -> null
    Scan::class -> null
    Shield::class -> null
    Snare::class -> null
    Trap::class -> null
    else -> null
} ?: Color.Unspecified

val Ability.color: Color
    get() = this::class.color()

@Composable
fun paramName(techName: String) = when (techName) {
    "duration_seconds" -> stringResource(R.string.DurationSecondsName)
    "number_uses" -> stringResource(R.string.NumberUsesName)
    "recharge_time" -> stringResource(R.string.RechargeTimeName)
    "radius" -> stringResource(R.string.RadiusName)
    "trap_duration_seconds" -> stringResource(R.string.TrapDurationSecondsName)
    "damage" -> "Урон"
    else -> techName
}

@Composable
fun unitName(paramTechName: String) = when (paramTechName) {
    "trap_duration_seconds", "recharge_time", "duration_seconds" -> stringResource(R.string.SecondsUnit)
    "radius" -> stringResource(R.string.MetersUnit)
    "damage" -> stringResource(R.string.HPUnit)
    else -> stringResource(R.string.ItemsUnit)
}