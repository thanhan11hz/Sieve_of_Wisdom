package com.example.sieve_of_wisdom.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sieve_of_wisdom.R
import com.example.sieve_of_wisdom.data.model.Package
import com.example.sieve_of_wisdom.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import androidx.navigation.fragment.findNavController
import android.util.Log
@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private val viewModel: HomeViewModel by viewModels()

    private var _binding: FragmentHomeBinding? = null
    private val binding
        get() = _binding!!

    private lateinit var packageAdapter: PackageAdapter

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentHomeBinding.bind(view)
        Log.d(
            "HOME_DEBUG",
            "btnGoToStore = ${binding.btnGoToStore}, " +
                    "visible=${binding.btnGoToStore.visibility}, " +
                    "enabled=${binding.btnGoToStore.isEnabled}, " +
                    "clickable=${binding.btnGoToStore.isClickable}"
        )
        binding.btnGoToStore.setOnClickListener {
            findNavController().navigate(
                R.id.action_homeFragment_to_storeFragment
            )
        }
        
        setupRecyclerView()
        setupTopics()
        setupLogout()
        observeViewModel()  
    }

    private fun setupRecyclerView() {
        packageAdapter = PackageAdapter(
            onPackageClick = { pkg ->
                handlePackageClick(pkg)
            }
        )

        binding.rvQuizPackages.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = packageAdapter
            setHasFixedSize(false)
        }
    }
    private fun setupLogout() {
        binding.btnLogout.setOnClickListener {

            viewModel.logout(
                onComplete = {
                    findNavController().navigate(
                        R.id.signInFragment,
                        null,
                        androidx.navigation.navOptions {
                            popUpTo(R.id.registerFragment) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    )
                }
            )
        }
    }
    private fun observeViewModel() {

        viewModel.packages.observe(viewLifecycleOwner) { packages ->

            packageAdapter.submitList(packages)
        
            if (packages.isEmpty()) {
        
                binding.rvQuizPackages.visibility =
                    View.GONE
        
                binding.emptyPackageContainer.visibility =
                    View.VISIBLE
        
            } else {
        
                binding.rvQuizPackages.visibility =
                    View.VISIBLE
        
                binding.emptyPackageContainer.visibility =
                    View.GONE
            }
        }

        viewModel.username.observe(viewLifecycleOwner) { username ->
            binding.tvHomeGreeting.text = "Chào, $username"
        }

        viewModel.coin.observe(viewLifecycleOwner) { coin ->
            binding.tvCoin.text = coin.toString()
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.rvQuizPackages.isEnabled = !isLoading
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error ?: return@observe

            Toast.makeText(
                requireContext(),
                error,
                Toast.LENGTH_SHORT
            ).show()
        }

        viewModel.completedPackages.observe(viewLifecycleOwner) {
            updateProgress()
        }
        
        viewModel.totalPackages.observe(viewLifecycleOwner) {
            updateProgress()
        }
    }

    private fun setupTopics() {

        viewModel.topics.observe(viewLifecycleOwner) { topics ->
    
            binding.topicContainer.removeAllViews()
    
            val allTopics =
                listOf<String?>(null) + topics
    
            allTopics.forEach { classification ->
    
                val topicView =
                    LayoutInflater.from(requireContext())
                        .inflate(
                            R.layout.item_quiz_topic,
                            binding.topicContainer,
                            false
                        )
    
                val topicName =
                    topicView.findViewById<TextView>(
                        R.id.tv_topic_name
                    )
    
                topicName.text =
                    if (classification == null) {
                        "Tất cả"
                    } else {
                        classificationToVietnamese(
                            classification
                        )
                    }
    
                topicName.setOnClickListener {
    
                    viewModel.filterByTopic(
                        classification
                    )
                }
    
                binding.topicContainer.addView(topicView)
            }
        }
    }

    private fun classificationToVietnamese(classification: String): String {
        return when (classification) {
            "Common" -> "Phổ thông"
            "Social" -> "Xã hội"
            "Science" -> "Khoa học"
            else -> classification
        }
    }

    private fun updateProgress() {
        val completed =
            viewModel.completedPackages.value ?: 0

        val total =
            viewModel.totalPackages.value ?: 0

        binding.tvProgressCount.text =
            "$completed/$total"

        binding.progressCurrent.max =
            total.coerceAtLeast(1)

        binding.progressCurrent.progress =
            completed.coerceIn(
                0,
                total.coerceAtLeast(1)
            )
    }
//
//    private fun setupBottomNavigation() {
//
//        binding.btnNavHome.setOnClickListener {
//            // Đang ở Home nên không cần navigate.
//        }
//
//        binding.btnNavShop.setOnClickListener {
//            Toast.makeText(
//                requireContext(),
//                "Cửa hàng",
//                Toast.LENGTH_SHORT
//            ).show()
//
//            // TODO:
//            // navigate tới StoreFragment
//        }
//        binding.btnGoToStore.setOnClickListener {
//            // TODO navigate StoreFragment
//        }
//
//        binding.btnNavPvp.setOnClickListener {
//            Toast.makeText(
//                requireContext(),
//                "PvP",
//                Toast.LENGTH_SHORT
//            ).show()
//
//            // TODO:
//            // navigate tới PvP
//        }
//    }

    private fun handlePackageClick(pkg: Package) {
        viewModel.selectPackage(pkg)

        Toast.makeText(
            requireContext(),
            "Bắt đầu: ${pkg.name}",
            Toast.LENGTH_SHORT
        ).show()

        // TODO:
        // Navigate tới QuizActivity với categoryId
    }

    private fun showUnlockDialog(pkg: Package) {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Mở khóa gói câu hỏi")
            .setMessage(
                "Bạn có muốn dùng ${pkg.price} Xu " +
                        "để mở khóa gói \"${pkg.classification}\"?"
            )
            .setNegativeButton("Hủy", null)
            .setPositiveButton("Xác nhận") { _, _ ->
                viewModel.unlockPackage(pkg)
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}