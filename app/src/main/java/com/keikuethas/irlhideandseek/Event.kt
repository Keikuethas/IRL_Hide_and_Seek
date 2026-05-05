package com.keikuethas.irlhideandseek

enum class FrequencyType {FREQUENT, RARE, COMMON}

abstract class Event(
    open var activation_frequency: FrequencyType,

)