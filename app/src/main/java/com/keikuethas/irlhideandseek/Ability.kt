package com.keikuethas.irlhideandseek

import androidx.compose.runtime.Composable
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.security.InvalidParameterException
import kotlin.reflect.KClass

fun getAbilityByType(type: KClass<out Ability>): Ability = when (type) {
    Shield::class -> Shield()
    Intel::class -> Intel()
    Scan::class -> Scan()
    PersonalBomb::class -> PersonalBomb()
    Trap::class -> Trap()
    Snare::class -> Snare()
    SafeHouse::class -> SafeHouse()
    SafeMansion::class -> SafeMansion()
    else -> throw InvalidParameterException("Could not handle ability type $type")
}

fun getAbilityByType(type: KClass<out Ability>, paramMap:Map<String, Number>): Ability = when (type) {
    Shield::class -> Shield(paramMap)
    Intel::class -> Intel(paramMap)
    Scan::class -> Scan(paramMap)
    PersonalBomb::class -> PersonalBomb(paramMap)
    Trap::class -> Trap(paramMap)
    Snare::class -> Snare(paramMap)
    SafeHouse::class -> SafeHouse(paramMap)
    SafeMansion::class -> SafeMansion(paramMap)
    else -> throw InvalidParameterException("Could not handle ability type $type")
}

@Serializable
sealed class Ability(
    open val duration_seconds: Int,
    open val number_uses: Int,
    open val recharge_time: Int,

    /*
     Нам нужно поговорить о поле ниже.
     Это замашка на масштабируемость:
        - параметры можно добавлять и удалять
        - их имена не фиксированы -> может редактировать даже игрок
     Недостаток: обращаться придётся через метод, а не напрямую
     */

    @Transient private val additionalParams: LinkedHashMap<String, Number> = linkedMapOf(),
) {

    abstract val abilityType: String

    val additional_data =
        buildJsonObject {
            additionalParams.entries.forEach { param ->
                put(
                    param.key,
                    param.value
                )
            }
        }

    fun forEachParam(action: (name: String, value: Number) -> Unit) =
        additionalParams.forEach { (string: String, number: Number) -> action(string, number) }

    @Composable
    fun ComposableForEachParam(action: @Composable (name: String, value: Number) -> Unit) =
        additionalParams.forEach { (string: String, number: Number) -> action(string, number) }

}

data class Shield(
    override var duration_seconds: Int = 600,
    override var number_uses: Int = 2,
    override var recharge_time: Int = 60
) : Ability(
    duration_seconds,
    number_uses,
    recharge_time,
) {
    override val abilityType = "SHIELD"

    constructor(params: Map<String, Number>) : this(
        duration_seconds = params["duration_seconds"] as Int,
        number_uses = params["number_uses"] as Int,
        recharge_time = params["recharge_time"] as Int
    )
}

class Intel(
    override val duration_seconds: Int = 600,
    override val number_uses: Int = 2,
    override val recharge_time: Int = 60
) : Ability(
    duration_seconds,
    number_uses,
    recharge_time,
) {
    override val abilityType = "INTEL"

    constructor(params: Map<String, Number>) : this(
        duration_seconds = params["duration_seconds"] as Int,
        number_uses = params["number_uses"] as Int,
        recharge_time = params["recharge_time"] as Int
    )
}

class Scan(
    override val duration_seconds: Int = 600,
    override val number_uses: Int = 2,
    override val recharge_time: Int = 60
) : Ability(
    duration_seconds,
    number_uses,
    recharge_time,
) {
    override val abilityType = "SCAN"

    constructor(params: Map<String, Number>) : this(
        duration_seconds = params["duration_seconds"] as Int,
        number_uses = params["number_uses"] as Int,
        recharge_time = params["recharge_time"] as Int
    )
}

class PersonalBomb(
    override val duration_seconds: Int = 600,
    override val number_uses: Int = 2,
    override val recharge_time: Int = 60,
    radius: Float = 10.0f,
    damage: Int = 100
) : Ability(
    duration_seconds,
    number_uses,
    recharge_time,

    additionalParams = linkedMapOf(
        "radius" to radius,
        "damage" to damage
    ),
) {
    override val abilityType = "PERSONAL_BOMB"

    constructor(params: Map<String, Number>) : this(
        duration_seconds = params["duration_seconds"] as Int,
        number_uses = params["number_uses"] as Int,
        recharge_time = params["recharge_time"] as Int,
        radius = params["radius"] as Float,
        damage = params["damage"] as Int
    )
}

class Trap(
    override val duration_seconds: Int = 600,
    override val number_uses: Int = 2,
    override val recharge_time: Int = 60,
    radius: Float = 10.0f,
    trap_duration_seconds: Int = 120
) : Ability(
    duration_seconds,
    number_uses,
    recharge_time,

    additionalParams = linkedMapOf(
        "radius" to radius,
        "trap_duration_seconds" to trap_duration_seconds
    ),
) {
    override val abilityType = "TRAP"

    constructor(params: Map<String, Number>) : this(
        duration_seconds = params["duration_seconds"] as Int,
        number_uses = params["number_uses"] as Int,
        recharge_time = params["recharge_time"] as Int,
        radius = params["radius"] as Float,
        trap_duration_seconds = params["trap_duration_seconds"] as Int
    )
}

class Snare(
    override val duration_seconds: Int = 600,
    override val number_uses: Int = 2,
    override val recharge_time: Int = 60,
    radius: Float = 10.0f,
    trap_duration_seconds: Int = 600
) : Ability(
    duration_seconds,
    number_uses, recharge_time,

    additionalParams = linkedMapOf(
        "radius" to radius,
        "trap_duration_seconds" to trap_duration_seconds
    ),
) {
    override val abilityType = "SNARE"

    constructor(params: Map<String, Number>) : this(
        duration_seconds = params["duration_seconds"] as Int,
        number_uses = params["number_uses"] as Int,
        recharge_time = params["recharge_time"] as Int,
        radius = params["radius"] as Float,
        trap_duration_seconds = params["trap_duration_seconds"] as Int
    )
}

class SafeHouse(
    override val duration_seconds: Int = 600,
    override val number_uses: Int = 2,
    override val recharge_time: Int = 60,
    radius: Float = 20.0F,
) : Ability(
    duration_seconds,
    number_uses,
    recharge_time,

    additionalParams = linkedMapOf(
        "radius" to radius,
    ),
) {
    override val abilityType = "SAFE_HOUSE"

    constructor(params: Map<String, Number>) : this(
        duration_seconds = params["duration_seconds"] as Int,
        number_uses = params["number_uses"] as Int,
        recharge_time = params["recharge_time"] as Int,
        radius = params["radius"] as Float
    )
}

class SafeMansion(
    override val duration_seconds: Int = 600,
    override val number_uses: Int = 2,
    override val recharge_time: Int = 60,
    radius: Float = 30.0f,
) : Ability(
    duration_seconds,
    number_uses,
    recharge_time,

    additionalParams = linkedMapOf(
        "radius" to radius,
    ),
) {
    override val abilityType = "SAFE_MANSION"

    constructor(params: Map<String, Number>) : this(
        duration_seconds = params["duration_seconds"] as Int,
        number_uses = params["number_uses"] as Int,
        recharge_time = params["recharge_time"] as Int,
        radius = params["radius"] as Float
    )
}