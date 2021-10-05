package com.nightcode.mediapicker.domain.usecases

interface AbstractUseCase<Params, Output> {
    operator fun invoke(params: Params): Output
}