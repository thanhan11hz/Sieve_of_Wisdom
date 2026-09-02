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

        setupRecyclerView()
        setupTopics()
        setupBottomNavigation()
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

    private fun observeViewModel() {

        viewModel.packages.observe(viewLifecycleOwner) { packages ->
            packageAdapter.submitList(packages)
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
    }

    private fun setupTopics() {

        viewModel.topics.observe(viewLifecycleOwner) { topics ->
    
            binding.topicContainer.removeAllViews()
    
            val allTopics = listOf("Tất cả") + topics
    
            allTopics.forEach { topic ->
    
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
    
                topicName.text = topic
    
                topicName.setOnClickListener {
    
                    viewModel.filterByTopic(
                        if (topic == "Tất cả") null
                        else topic
                    )
                }
    
                binding.topicContainer.addView(topicView)
            }
        }
    }

    private fun setupBottomNavigation() {

        binding.btnNavHome.setOnClickListener {
            // Đang ở Home nên không cần navigate.
        }

        binding.btnNavShop.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Cửa hàng",
                Toast.LENGTH_SHORT
            ).show()

            // TODO:
            // navigate tới StoreFragment
        }

        binding.btnNavPvp.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "PvP",
                Toast.LENGTH_SHORT
            ).show()

            // TODO:
            // navigate tới PvP
        }
    }

    private fun handlePackageClick(pkg: Package) {

        if (pkg.isUnlocked) {

            viewModel.selectPackage(pkg)

            /*
             * TODO:
             * Navigate tới QuizActivity/QuizFragment.
             * findNavController().navigate(
             *     HomeFragmentDirections
             *         .actionHomeFragmentToQuizFragment(pkg.categoryId)
             * )
             */

            Toast.makeText(
                requireContext(),
                "Bắt đầu: ${pkg.classification}",
                Toast.LENGTH_SHORT
            ).show()

        } else {

            showUnlockDialog(pkg)
        }
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