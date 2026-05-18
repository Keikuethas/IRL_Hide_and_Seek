package com.keikuethas.irlhideandseek.mvi.home

object HomeReducer {
    fun reduce(state: HomeState, result: HomeResult): HomeState {
        return when(result) {
            is HomeResult.NameEdited -> state.copy(nameText = result.value.take(HomeState.MAXNAMELENGTH))
            is HomeResult.RoomNameEdited -> state.copy(roomNameText = result.value)
            HomeResult.ErrorDismissed -> state.copy(error = null)
            is HomeResult.Error -> state.copy(error = HomeError(result.title, result.description))
            is HomeResult.Loading -> state.copy(isLoading = result.isLoading)
            HomeResult.PermissionGranted -> state.copy(isLocationPermissionGranted = true)
            HomeResult.PermissionDenied -> state.copy(isLocationPermissionGranted = false)
        }
    }
}