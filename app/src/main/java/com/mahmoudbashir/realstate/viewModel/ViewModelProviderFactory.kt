@file:Suppress("UNCHECKED_CAST")

package com.mahmoudbashir.realstate.viewModel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mahmoudbashir.realstate.repository.Repository


class ViewModelProviderFactory constructor(
    private val app: Application,
    private val repos: Repository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RealStateviewModel(app, repos) as T
    }
}