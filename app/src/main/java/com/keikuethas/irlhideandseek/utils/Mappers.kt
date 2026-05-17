package com.keikuethas.irlhideandseek.utils

import com.keikuethas.irlhideandseek.Ability
import com.keikuethas.irlhideandseek.Intel
import com.keikuethas.irlhideandseek.PersonalBomb
import com.keikuethas.irlhideandseek.SafeHouse
import com.keikuethas.irlhideandseek.SafeMansion
import com.keikuethas.irlhideandseek.Scan
import com.keikuethas.irlhideandseek.Shield
import com.keikuethas.irlhideandseek.Snare
import com.keikuethas.irlhideandseek.Trap
import com.keikuethas.irlhideandseek.Websocket.AbilityType
import java.io.InvalidObjectException
import kotlin.reflect.KClass

// KClass<out Ability> <-> AbilityType

fun KClass<out Ability>.toAbilityType(): AbilityType = when(this) {
    Shield::class -> AbilityType.SHIELD
    Intel::class -> AbilityType.INTEL
    Scan::class-> AbilityType.SCAN
    PersonalBomb::class -> AbilityType.PERSONAL_BOMB
    Snare::class -> AbilityType.SNARE
    Trap::class -> AbilityType.TRAP
    SafeHouse::class -> AbilityType.SAFE_HOUSE
    SafeMansion::class -> AbilityType.SAFE_MANSION

    else -> throw InvalidObjectException("Mapper not set for type $this")
}

fun AbilityType.toKClass(): KClass<out Ability> = when(this) {
    AbilityType.SHIELD -> Shield::class
    AbilityType.INTEL -> Intel::class
    AbilityType.SCAN -> Scan::class
    AbilityType.PERSONAL_BOMB -> PersonalBomb::class
    AbilityType.SNARE -> Snare::class
    AbilityType.TRAP -> Trap::class
    AbilityType.SAFE_HOUSE -> SafeHouse::class
    AbilityType.SAFE_MANSION -> SafeMansion::class
}