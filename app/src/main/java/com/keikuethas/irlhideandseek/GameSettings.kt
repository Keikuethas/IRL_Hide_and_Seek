package com.keikuethas.irlhideandseek

//todo
//fixme

//NOTE
//CONCERN
//FIXME
//REFACTOR
//TEMP
//UPGRADE
//DESCRIBE: описание должно обновляться по мере обновления класса
/**
 * Класс, хранящий информацию о всех настройках игры (комнаты).
 * @param id Имя комнаты
 * @property playersCanChangeRole Может ли игрок изменять свою роль
 */
data class GameSettings(val id: String, val hostId: String) {
    var playersCanChangeRole: Boolean = true
}