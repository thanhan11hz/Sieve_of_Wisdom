package com.example.sieve_of_wisdom.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sieve_of_wisdom.data.model.Package
import com.example.sieve_of_wisdom.data.repository.PackageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PackageViewModel @Inject constructor(
    private val packageRepository: PackageRepository
): ViewModel() {
    private val _packageState = MutableStateFlow<List<Package>>(emptyList())
    val packageState: StateFlow<List<Package>> = _packageState.asStateFlow()

    init {
        loadAllPackages()
    }

    fun loadAllPackages() {
        viewModelScope.launch {
            packageRepository.getPackage()
                .onSuccess { list ->
                    _packageState.value = list
                }
        }
    }

    fun filterPackageByAccess(isUnlocked: Boolean) {
        viewModelScope.launch {
            packageRepository.filterPackageByAccess(isUnlocked)
                .onSuccess { list ->
                    _packageState.value = list
                }
        }
    }

    fun searchPackage(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                loadAllPackages()
            } else {
                packageRepository.searchPackage(query)
                    .onSuccess { list ->
                        _packageState.value = list
                    }
            }
        }
    }

    fun filterPackageByClassification(classification: String) {
        viewModelScope.launch {
            packageRepository.filterPackageByClassfication(classification)
                .onSuccess { list ->
                    _packageState.value = list
                }
        }
    }

    fun unlockPackage(pkg: Package, onSuccess: () -> Unit, onError: (Throwable) -> Unit) {
        viewModelScope.launch {
            packageRepository.unlockPackage(pkg)
                .onSuccess {
                    loadAllPackages()
                    onSuccess()
                }
                .onFailure { error ->
                    onError(error)
                }
        }
    }
}