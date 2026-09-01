package com.example.sieve_of_wisdom.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sieve_of_wisdom.data.model.Package
import com.example.sieve_of_wisdom.data.repository.PackageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val packageRepository: PackageRepository
) : ViewModel() {

    private val _packages = MutableLiveData<List<Package>>(emptyList())
    val packages: LiveData<List<Package>>
        get() = _packages

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?>
        get() = _error

    private val _selectedPackage = MutableLiveData<Package?>()
    val selectedPackage: LiveData<Package?>
        get() = _selectedPackage

    private var allPackages: List<Package> = emptyList()
    private var currentQuery = ""
    private var selectedTopic: String? = null

    init {
        loadPackages()
    }

    fun loadPackages() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            packageRepository.getPackage()
                .onSuccess {
                    allPackages = it
                    applyFilters()
                }
                .onFailure {
                    _error.value =
                        it.message ?: "Không thể tải danh sách gói câu hỏi."
                }

            _isLoading.value = false
        }
    }

    fun search(query: String) {
        currentQuery = query.trim()
        applyFilters()
    }

    fun filterByTopic(topic: String?) {
        selectedTopic = topic
        applyFilters()
    }

    fun unlockPackage(pkg: Package) {
        viewModelScope.launch {
            _isLoading.value = true

            packageRepository.unlockPackage(pkg)
                .onSuccess {
                    allPackages = allPackages.map {
                        if (it.categoryId == pkg.categoryId) {
                            it.copy(isUnlocked = true)
                        } else {
                            it
                        }
                    }

                    applyFilters()
                }
                .onFailure {
                    _error.value =
                        it.message ?: "Không thể mở khóa gói câu hỏi."
                }

            _isLoading.value = false
        }
    }

    fun selectPackage(pkg: Package) {
        _selectedPackage.value = pkg
    }

    fun clearSelectedPackage() {
        _selectedPackage.value = null
    }

    private fun applyFilters() {
        var result = allPackages

        selectedTopic?.let { topic ->
            result = result.filter {
                it.classification.equals(topic, ignoreCase = true)
            }
        }

        if (currentQuery.isNotBlank()) {
            result = result.filter {
                it.classification.contains(
                    currentQuery,
                    ignoreCase = true
                )
            }
        }

        _packages.value = result
    }
}