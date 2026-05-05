package com.keikuethas.irlhideandseek

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

val emptyJson = JsonObject(emptyMap())

enum class AbilityType {
    SHIELD, INTEL, SCAN, PERSONAL_BOMB, TRAP, SNARE, SAFE_HOUSE, SAFE_MANSION;

    override fun toString(): String = when (this) {
        SHIELD -> "Щит"
        INTEL -> "Разведданные"
        SCAN -> "Сканирование местности"
        PERSONAL_BOMB -> "Персональная бомба"
        TRAP -> "Ловушка"
        SNARE -> "Капкан"
        SAFE_HOUSE -> "Я в домике"
        SAFE_MANSION -> "Я в особняке"
    }

    val description: String by lazy { //todo
        when (this) {
            SHIELD -> "Щит"
            INTEL -> "Разведданные"
            SCAN -> "Сканирование местности"
            PERSONAL_BOMB -> "Персональная бомба"
            TRAP -> "Ловушка"
            SNARE -> "Капкан"
            SAFE_HOUSE -> "Я в домике"
            SAFE_MANSION -> "Я в особняке"
        }
    }

    val color: Color by lazy { //todo
        when (this) {
            SHIELD -> Color.Unspecified
            INTEL -> Color.Unspecified
            SCAN -> Color.Unspecified
            PERSONAL_BOMB -> Color.Unspecified
            TRAP -> Color.Unspecified
            SNARE -> Color.Unspecified
            SAFE_HOUSE -> Color.Unspecified
            SAFE_MANSION -> Color.Unspecified
        }
    }
}

@Serializable
abstract class Ability(
    open var duration_seconds: Int,
    open var number_uses: Int,
    open var recharge_time: Int,


    /*
     Нам нужно поговорить о поле ниже.
     Это замашка на масштабируемость:
        - параметры можно добавлять и удалять
        - их имена не фиксированы -> может редактировать даже игрок
     Недостаток: обращаться придётся через метод, а не напрямую
     */
    @Transient private val additionalParams: LinkedHashMap<String, Number> = linkedMapOf(),
    @Transient val type: AbilityType = AbilityType.SHIELD,
) {

    @Transient
    val name: String = type.toString()
    @Transient
    val description: String = type.description
    @Transient
    val color: Color = type.color

    val additional_data: JsonObject =
        buildJsonObject {
            additionalParams.entries.forEach { param ->
                put(
                    param.key,
                    param.value
                )
            }
        }

    fun getParam(key: String): Number? = additionalParams[key]

    fun getIntParam(key: String): Int? = getParam(key) as? Int

    fun getDoubleParam(key: String): Double? = getParam(key) as? Double

    fun setParam(key: String, value: Number): Boolean {
        if (key !in additionalParams.keys) return false
        additionalParams[key] = value
        return true
    }

    fun addParam(key: String, value: Number): Boolean {
        if (key in additionalParams.keys) return false
        additionalParams[key] = value
        return true
    }

    fun removeParam(key: String): Boolean = additionalParams.remove(key) != null
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
    type = AbilityType.SHIELD
)

class Intel(
    override var duration_seconds: Int = 600,
    override var number_uses: Int = 2,
    override var recharge_time: Int = 60
) : Ability(
    duration_seconds,
    number_uses,
    recharge_time,
    type = AbilityType.INTEL
)

class Scan(
    override var duration_seconds: Int = 600,
    override var number_uses: Int = 2,
    override var recharge_time: Int = 60
) : Ability(
    duration_seconds,
    number_uses,
    recharge_time,
    type = AbilityType.SCAN
)

class PersonalBomb(
    override var duration_seconds: Int = 600,
    override var number_uses: Int = 2,
    override var recharge_time: Int = 60,
    radius: Double = 10.0,
    damage: Int = 100
) : Ability(
    duration_seconds,
    number_uses,
    recharge_time,
    type = AbilityType.PERSONAL_BOMB,

    additionalParams = linkedMapOf(
        "radius" to radius,
        "damage" to damage
    ),
) {
}

class Trap(
    override var duration_seconds: Int = 600,
    override var number_uses: Int = 2,
    override var recharge_time: Int = 60,
    var radius: Double = 10.0,
    var trap_duration_seconds: Int = 120
) : Ability(
    duration_seconds,
    number_uses,
    recharge_time,
    type = AbilityType.TRAP,

    additionalParams = linkedMapOf(
        "radius" to radius,
        "trap_duration_seconds" to trap_duration_seconds
    ),
)

class Snare(
    override var duration_seconds: Int = 600,
    override var number_uses: Int = 2,
    override var recharge_time: Int = 60,
    radius: Double = 10.0,
    trap_duration_seconds: Int = 600
) : Ability(
    duration_seconds,
    number_uses, recharge_time,
    type = AbilityType.SNARE,

    additionalParams = linkedMapOf(
        "radius" to radius,
        "trap_duration_seconds" to trap_duration_seconds
    ),
)

class SafeHouse(
    override var duration_seconds: Int = 600,
    override var number_uses: Int = 2,
    override var recharge_time: Int = 60,
    radius: Double = 20.0,
) : Ability(
    duration_seconds,
    number_uses, recharge_time,
    type = AbilityType.SAFE_HOUSE,

    additionalParams = linkedMapOf(
        "radius" to radius,
    ),
)

class SafeMansion(
    override var duration_seconds: Int = 600,
    override var number_uses: Int = 2,
    override var recharge_time: Int = 60,
    radius: Double = 30.0,
) : Ability(
    duration_seconds,
    number_uses, recharge_time,
    type = AbilityType.SAFE_MANSION,

    additionalParams = linkedMapOf(
        "radius" to radius,
    ),
)