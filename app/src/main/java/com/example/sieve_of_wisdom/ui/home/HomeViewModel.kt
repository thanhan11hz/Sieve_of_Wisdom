package com.example.sieve_of_wisdom.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sieve_of_wisdom.data.model.Package
import com.example.sieve_of_wisdom.data.repository.AuthRepository
import com.example.sieve_of_wisdom.data.repository.PackageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val packageRepository: PackageRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _packages =
        MutableLiveData<List<Package>>(emptyList())

    val packages: LiveData<List<Package>>
        get() = _packages

    private val _isLoading =
        MutableLiveData(false)

    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _error =
        MutableLiveData<String?>()

    val error: LiveData<String?>
        get() = _error

    private val _coin =
        MutableLiveData<Int>(0)

    val coin: LiveData<Int>
        get() = _coin
    private val _selectedPackage =
        MutableLiveData<Package?>()

    val selectedPackage: LiveData<Package?>
        get() = _selectedPackage

    private var allPackages: List<Package> = emptyList()

    private var currentQuery = ""

    private var selectedTopic: String? = null
    private val _topics = MutableLiveData<List<String>>(emptyList())
    val topics: LiveData<List<String>>
        get() = _topics

    private val _username = MutableLiveData<String>("")
    val username: LiveData<String>
        get() = _username
    init {
        loadPackages()
        loadTopics()
        loadUser()
    }

    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            onComplete()
        }
    }
    private fun loadUser() {
        viewModelScope.launch {
            packageRepository
                .getCurrentUser()
                .onSuccess { user ->
                    _coin.value = user.coin
                    _username.value = user.username

                }
                .onFailure {
                    _error.value =
                        it.message ?: "Không thể tải thông tin người dùng."
                }
        }
    }


    fun loadPackages() {
        viewModelScope.launch {

            _isLoading.value = true
            _error.value = null

            packageRepository
                .getPackage()
                .onSuccess {

                    allPackages = it

                    applyFilters()
                }
                .onFailure {

                    _error.value =
                        it.message
                            ?: "Không thể tải danh sách gói câu hỏi."
                }

            _isLoading.value = false
        }
    }

//    fun loadPackages() {
//        viewModelScope.launch {
//            val hardcodedPackages = listOf(
//                Package(
//                    categoryId  = 1,
//                    name = "General Knowledge",
//                    classification = "Common",
//                    price = 100,
//                    isUnlocked = false
//                ),
//                Package(
//                    categoryId  = 2,
//                    name = "Social Science",
//                    classification = "Social",
//                    price = 200,
//                    isUnlocked = true
//                ),
//                Package(
//                    categoryId  = 3,
//                    name = "Science Challenge",
//                    classification = "Science",
//                    price = 300,
//                    isUnlocked = false
//                )
//            )
//
//            allPackages = hardcodedPackages
//            applyFilters()
//        }
//    }

    private fun loadTopics() {
        viewModelScope.launch {
            packageRepository
                .getClassifications()
                .onSuccess { classifications ->
                    _topics.value = classifications
                }
                .onFailure {
                    _error.value =
                        it.message ?: "Không thể tải chủ đề."
                }
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

    fun selectPackage(pkg: Package) {

        _selectedPackage.value = pkg
    }

    fun clearSelectedPackage() {

        _selectedPackage.value = null
    }

    private fun applyFilters() {

        var result = allPackages
    
        selectedTopic?.let { classification ->
    
            result = result.filter {
    
                it.classification.equals(
                    classification,
                    ignoreCase = true
                )
            }
        }
    
        if (currentQuery.isNotBlank()) {
    
            result = result.filter {
    
                it.name.contains(
                    currentQuery,
                    ignoreCase = true
                )
            }
        }
    
        _packages.value = result
    }

    fun unlockPackage(pkg: Package) {
        viewModelScope.launch {
    
            _isLoading.value = true
            _error.value = null
    
            packageRepository
                .unlockPackage(pkg)
                .onSuccess { newCoin ->
    
                    allPackages = allPackages.map {
                        if (it.categoryId == pkg.categoryId) {
                            it.copy(isUnlocked = true)
                        } else {
                            it
                        }
                    }
    
                    applyFilters()
                    _coin.value = newCoin
                }
                .onFailure {
                    _error.value =
                        it.message
                            ?: "Không thể mở khóa gói câu hỏi."
                }
    
            _isLoading.value = false
        }
    }
    
//    val topics: LiveData<List<String>>
//    get() = Transformations.map(packages) { packages ->
//        listOf("Tất cả") +
//            packages
//                .map { it.classification }
//                .distinct()
//    }
}