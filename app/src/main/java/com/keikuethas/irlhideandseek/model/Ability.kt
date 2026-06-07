package com.keikuethas.irlhideandseek.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

fun getAbilityByType(type: AbilityType): Ability = when (type) {
    AbilityType.SHIELD -> Shield()
    AbilityType.INTEL -> Intel()
    AbilityType.SCAN -> Scan()
    AbilityType.PERSONAL_BOMB -> PersonalBomb()
    AbilityType.TRAP -> Trap()
    AbilityType.SNARE -> Snare()
    AbilityType.SAFE_HOUSE -> SafeHouse()
    AbilityType.SAFE_MANSION -> SafeMansion()
}

fun getAbilityByType(type: AbilityType, paramMap: Map<String, Number>): Ability = when (type) {
    AbilityType.SHIELD -> Shield(paramMap)
    AbilityType.INTEL -> Intel(paramMap)
    AbilityType.SCAN -> Scan(paramMap)
    AbilityType.PERSONAL_BOMB -> PersonalBomb(paramMap)
    AbilityType.TRAP -> Trap(paramMap)
    AbilityType.SNARE -> Snare(paramMap)
    AbilityType.SAFE_HOUSE -> SafeHouse(paramMap)
    AbilityType.SAFE_MANSION -> SafeMansion(paramMap)
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

    @Transient private val additionalParams: LinkedHashMap<String, Double> = linkedMapOf(),
) {

    abstract val abilityType: AbilityType

    val additional_data: Map<String, Double>
        get() {
            val map = mutableMapOf<String, Double>()
            additionalParams.entries.forEach {
                map[it.key] = it.value
            }
            return map.toMap()
        }

    fun forEachParam(action: (name: String, value: Number) -> Unit) =
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
    override val abilityType = AbilityType.SHIELD

    constructor(params: Map<String, Number>) : this(
        duration_seconds = params["duration_seconds"]!!.toInt(),
        number_uses = params["number_uses"]!!.toInt(),
        recharge_time = params["recharge_time"]!!.toInt()
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
    override val abilityType = AbilityType.INTEL

    constructor(params: Map<String, Number>) : this(
        duration_seconds = params["duration_seconds"]!!.toInt(),
        number_uses = params["number_uses"]!!.toInt(),
        recharge_time = params["recharge_time"]!!.toInt()
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
    override val abilityType = AbilityType.SCAN

    constructor(params: Map<String, Number>) : this(
        duration_seconds = params["duration_seconds"]!!.toInt(),
        number_uses = params["number_uses"]!!.toInt(),
        recharge_time = params["recharge_time"]!!.toInt()
    )
}

class PersonalBomb(
    override val duration_seconds: Int = 600,
    override val number_uses: Int = 2,
    override val recharge_time: Int = 60,
    radius: Double = 10.0,
    damage: Int = 100
) : Ability(
    duration_seconds,
    number_uses,
    recharge_time,

    additionalParams = linkedMapOf(
        "radius" to radius,
        "damage" to damage.toDouble()
    ),
) {
    override val abilityType = AbilityType.PERSONAL_BOMB

    constructor(params: Map<String, Number>) : this(
        duration_seconds = params["duration_seconds"]!!.toInt(),
        number_uses = params["number_uses"]!!.toInt(),
        recharge_time = params["recharge_time"]!!.toInt(),
        radius = params["radius"] as Double,
        damage = params["damage"]!!.toInt()
    )
}

class Trap(
    override val duration_seconds: Int = 600,
    override val number_uses: Int = 2,
    override val recharge_time: Int = 60,
    radius: Double = 10.0,
    trap_duration_seconds: Int = 120
) : Ability(
    duration_seconds,
    number_uses,
    recharge_time,

    additionalParams = linkedMapOf(
        "radius" to radius,
        "trap_duration_seconds" to trap_duration_seconds.toDouble()
    ),
) {
    override val abilityType = AbilityType.TRAP

    constructor(params: Map<String, Number>) : this(
        duration_seconds = params["duration_seconds"]!!.toInt(),
        number_uses = params["number_uses"]!!.toInt(),
        recharge_time = params["recharge_time"]!!.toInt(),
        radius = params["radius"]!!.toDouble(),
        trap_duration_seconds = params["trap_duration_seconds"]!!.toInt()
    )
}

class Snare(
    override val duration_seconds: Int = 600,
    override val number_uses: Int = 2,
    override val recharge_time: Int = 60,
    radius: Double = 10.0,
    trap_duration_seconds: Int = 600
) : Ability(
    duration_seconds,
    number_uses, recharge_time,

    additionalParams = linkedMapOf(
        "radius" to radius,
        "trap_duration_seconds" to trap_duration_seconds.toDouble()
    ),
) {
    override val abilityType = AbilityType.SNARE

    constructor(params: Map<String, Number>) : this(
        duration_seconds = params["duration_seconds"]!!.toInt(),
        number_uses = params["number_uses"]!!.toInt(),
        recharge_time = params["recharge_time"]!!.toInt(),
        radius = params["radius"] as Double,
        trap_duration_seconds = params["trap_duration_seconds"]!!.toInt()
    )
}

class SafeHouse(
    override val duration_seconds: Int = 600,
    override val number_uses: Int = 2,
    override val recharge_time: Int = 60,
    radius: Double = 20.0,
) : Ability(
    duration_seconds,
    number_uses,
    recharge_time,

    additionalParams = linkedMapOf(
        "radius" to radius,
    ),
) {
    override val abilityType = AbilityType.SAFE_HOUSE

    constructor(params: Map<String, Number>) : this(
        duration_seconds = params["duration_seconds"]!!.toInt(),
        number_uses = params["number_uses"]!!.toInt(),
        recharge_time = params["recharge_time"]!!.toInt(),
        radius = params["radius"] as Double
    )
}

class SafeMansion(
    override val duration_seconds: Int = 600,
    override val number_uses: Int = 2,
    override val recharge_time: Int = 60,
    radius: Double = 30.0,
) : Ability(
    duration_seconds,
    number_uses,
    recharge_time,

    additionalParams = linkedMapOf(
        "radius" to radius,
    ),
) {
    override val abilityType = AbilityType.SAFE_MANSION

    constructor(params: Map<String, Number>) : this(
        duration_seconds = params["duration_seconds"]!!.toInt(),
        number_uses = params["number_uses"]!!.toInt(),
        recharge_time = params["recharge_time"]!!.toInt(),
        radius = params["radius"] as Double
    )
}