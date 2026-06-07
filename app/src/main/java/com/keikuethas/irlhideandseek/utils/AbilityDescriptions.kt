package com.keikuethas.irlhideandseek.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.keikuethas.irlhideandseek.R
import com.keikuethas.irlhideandseek.model.Ability
import com.keikuethas.irlhideandseek.model.AbilityType

@Composable
fun AbilityType.name(): String = when (this) {
    AbilityType.INTEL -> stringResource(R.string.IntelName)
    AbilityType.PERSONAL_BOMB -> stringResource(R.string.PersonalBombName)
    AbilityType.SAFE_HOUSE -> stringResource(R.string.SafeHouseName)
    AbilityType.SAFE_MANSION -> stringResource(R.string.SafeMansionName)
    AbilityType.SCAN -> stringResource(R.string.ScanName)
    AbilityType.SHIELD -> stringResource(R.string.ShieldName)
    AbilityType.SNARE -> stringResource(R.string.SnareName)
    AbilityType.TRAP -> stringResource(R.string.TrapName)
}

val Ability.name: String
    @Composable
    get() = abilityType.name()

fun AbilityType.description(): String = when (this) {
    AbilityType.INTEL -> null
    AbilityType.PERSONAL_BOMB -> null
    AbilityType.SAFE_HOUSE -> null
    AbilityType.SAFE_MANSION -> null
    AbilityType.SCAN -> null
    AbilityType.SHIELD -> null
    AbilityType.SNARE -> null
    AbilityType.TRAP -> null
} ?: "not implemented yet"

// todo
// resource
val Ability.description: String
    get() = abilityType.description()


val AbilityType.color: Color get() = when (this) {
    AbilityType.INTEL -> Color(0, 0,0)
    AbilityType.PERSONAL_BOMB -> Color(0, 0,0)
    AbilityType.SAFE_HOUSE -> Color(0, 0,0)
    AbilityType.SAFE_MANSION -> Color(0, 0,0)
    AbilityType.SCAN -> Color(0, 0,0)
    AbilityType.SHIELD -> Color(139, 195, 74, 255)
    AbilityType.SNARE -> Color(0, 0,0)
    AbilityType.TRAP -> Color(0, 0,0)
} ?: Color.Unspecified

val AbilityType.surfaceColor: Color get() = when (this) {
    AbilityType.INTEL -> Color(0, 0,0)
    AbilityType.PERSONAL_BOMB -> Color(0, 0,0)
    AbilityType.SAFE_HOUSE -> Color(0, 0,0)
    AbilityType.SAFE_MANSION -> Color(0, 0,0)
    AbilityType.SCAN -> Color(0, 0,0)
    AbilityType.SHIELD -> Color(0, 0,0)
    AbilityType.SNARE -> Color(0, 0,0)
    AbilityType.TRAP -> Color(0, 0,0)
} ?: Color.Unspecified

val Ability.color: Color
    get() = abilityType.color

@Composable
fun paramName(techName: String) = when (techName) {
    "duration_seconds" -> stringResource(R.string.DurationSecondsName)
    "number_uses" -> stringResource(R.string.NumberUsesName)
    "recharge_time" -> stringResource(R.string.RechargeTimeName)
    "radius" -> stringResource(R.string.RadiusName)
    "trap_duration_seconds" -> stringResource(R.string.TrapDurationSecondsName)
    "damage" -> stringResource(R.string.DamageName)
    "roleName" -> stringResource(R.string.RoleName)
    "health" -> stringResource(R.string.HealthName)
    else -> techName
}

@Composable
fun unitName(paramTechName: String) = when (paramTechName) {
    "trap_duration_seconds", "recharge_time", "duration_seconds" -> stringResource(R.string.SecondsUnit)
    "radius" -> stringResource(R.string.MetersUnit)
    "damage", "health" -> stringResource(R.string.HPUnit)
    "roleName" -> ""
    else -> stringResource(R.string.ItemsUnit)
}