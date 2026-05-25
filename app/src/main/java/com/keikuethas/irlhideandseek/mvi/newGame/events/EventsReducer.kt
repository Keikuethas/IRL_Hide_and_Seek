package com.keikuethas.irlhideandseek.mvi.newGame.events

object EventsReducer {
    fun reduce(state: ESState, result: ESResult): ESState = when (result) {
        is ESResult.AddEventDialogStateSet ->
            state.copy(showEventAddDialog = result.open)

        is ESResult.EventAdded ->
            state.copy(events = state.events + EventState(result.type))

        is ESResult.EventDeleted ->
            state.copy(events = state.events.filterNot { it.type == result.type })

        is ESResult.ParameterChanged ->
            with(state.showValueInputDialog!!) {
                val obj = state.events.find { it.type == eventType }!!
                val objIndex = state.events.indexOf(obj)
                val eventList = state.events.toMutableList()
                val paramList = state.events[objIndex].params.toMutableList()
                with(paramList) {
                    set(indexOf(find { it.first == result.name }), result.name to result.newValue)
                }
                eventList[objIndex] = eventList[objIndex].copy(params = paramList)
                state.copy(events = eventList.toList())
            }

        is ESResult.QuitDialogStateChanged -> state.copy(showQuitDialog = result.open)

        is ESResult.VIDStateChanged -> state.copy(showValueInputDialog = result.state)

        ESResult.EventsCleared -> state.copy(events = emptyList())
        is ESResult.FrequencyChanged -> {
            val obj = state.events.find { it.type == result.eventType }!!
            val objIndex = state.events.indexOf(obj)
            val eventList = state.events.toMutableList()

            eventList[objIndex] = eventList[objIndex].copy(frequency = result.newValue)
            state.copy(events = eventList.toList()) //^
        }

        is ESResult.Initialized -> result.newState
    }
}