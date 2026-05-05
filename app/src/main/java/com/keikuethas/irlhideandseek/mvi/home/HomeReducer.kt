package com.keikuethas.irlhideandseek.mvi.home

object HomeReducer {
    fun reduce(state: HomeState, result: HomeResult): HomeState {
        return when(result) {
            is HomeResult.NameEdited -> state.copy(nameText = result.value.take(HomeState.nameLengthLimit))
            is HomeResult.RoomNameEdited -> state.copy(roomNameText = result.value.take(HomeState.roomNameLengthLimit))
            HomeResult.ErrorDismissed -> state.copy(error = null)
            is HomeResult.Error -> state.copy(error = HomeError(result.title, result.description))
        }
    }
}