package com.example.sieve_of_wisdom.ui.home

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sieve_of_wisdom.Dialog.PurchaseDialog
import com.example.sieve_of_wisdom.R
import com.example.sieve_of_wisdom.databinding.FragmentStoreBinding
import com.example.sieve_of_wisdom.ui.viewmodel.PackageViewModel
import com.example.sieve_of_wisdom.ui.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.getValue
import com.example.sieve_of_wisdom.data.model.Package

@AndroidEntryPoint
class StoreFragment : Fragment() {

    private lateinit var tabList: List<TextView>
    private var selectedTab: Int = 0
    private val categories = listOf("Science", "Social", "Common")
    private var _binding: FragmentStoreBinding? = null
    private val binding get() = _binding!!
    private val user: UserViewModel by viewModels()
    private val packages: PackageViewModel by viewModels()
    private lateinit var adapter: TopicCardAdapter
    private var pendingPackage: Package? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStoreBinding.inflate(
            inflater,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tabList = listOf(
            binding.tabNatural,
            binding.tabSocial,
            binding.tabGeneral
        )

        tabList.forEachIndexed { index, tab ->
            tab.setOnClickListener {
                if (selectedTab != index) { // Prevent redundant calls on already selected tab
                    selectTab(index)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                user.userState.collect { state ->
                    state?.let {
                        binding.starText.text = it.coin.toString()
                    }
                }
            }
        }

        parentFragmentManager.setFragmentResultListener(
            PurchaseDialog.REQUEST_KEY,
            viewLifecycleOwner
        ) { _, _ ->

            pendingPackage?.let { pkg ->

                packages.unlockPackage(
                    pkg = pkg,
                    onSuccess = {
                        Toast.makeText(
                            requireContext(),
                            "Mở khóa thành công!",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    onError = { error ->
                        Toast.makeText(
                            requireContext(),
                            "Lỗi: ${error.localizedMessage}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )


            }

            pendingPackage = null
        }

        adapter = TopicCardAdapter(
            onUnlockClick = {
                item -> pendingPackage = item
                PurchaseDialog.newInstance(
                    message = "Bạn có muốn dùng ${item.price} xu để\nmở khóa gói",
                    packageTitle = item.name
                ).show(parentFragmentManager, "PurchaseDialog")
            }
        ).apply {
            stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }

        binding.rvTopics.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTopics.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                packages.packageState.collect { newList ->
                    adapter.submitList(newList.toList()) {
                        // Optional: Scroll to top whenever a new category is selected
                        binding.rvTopics.scrollToPosition(0)
                    }
                    if (newList.isEmpty()) {
                        binding.noResult.visibility = View.VISIBLE
                        binding.rvTopics.isEnabled = false
                        binding.tabScrollView.visibility = View.GONE
                        binding.tabScrollView.isEnabled = false

                        val params = binding.listContainer.layoutParams as ConstraintLayout.LayoutParams

                        params.topToTop = binding.tabScrollView.id
                        params.topToBottom = ConstraintLayout.LayoutParams.UNSET
                        params.topMargin = 0

                        binding.listContainer.layoutParams = params

                    } else{

                        binding.noResult.visibility = View.GONE
                        binding.rvTopics.isEnabled = true
                        binding.tabScrollView.visibility = View.VISIBLE
                        binding.tabScrollView.isEnabled = true

                        val params = binding.listContainer.layoutParams as ConstraintLayout.LayoutParams
                        val margin = (-25 * resources.displayMetrics.density).toInt()

                        params.topToTop = ConstraintLayout.LayoutParams.UNSET
                        params.topToBottom = binding.tabScrollView.id
                        params.topMargin = margin

                        binding.listContainer.layoutParams = params

                    }
                }
            }
        }

        selectTab(selectedTab)

        binding.etSearch.doOnTextChanged { text, _, _, _ ->
            if (text.isNullOrEmpty()) {
                binding.btnClearSearch.visibility = View.GONE
                packages.filterPackageByClassification(categories[selectedTab])
            } else {
                binding.btnClearSearch.visibility = View.VISIBLE
                packages.searchPackage(binding.etSearch.text.toString())
            }
        }

        binding.btnClearSearch.setOnClickListener {
            binding.etSearch.text.clear()
            packages.filterPackageByClassification(categories[selectedTab])
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun selectTab(newIndex: Int) {

        val padding12dp = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 12f, resources.displayMetrics
        ).toInt()

        val padding45dp = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 45f, resources.displayMetrics
        ).toInt()

        selectedTab = newIndex

        tabList.forEachIndexed { index, tab ->
            val isSelected = (index == selectedTab)

            // Toggle padding and background dynamically
            val bottomPadding = if (isSelected) padding45dp else padding12dp
            tab.setPadding(tab.paddingLeft, tab.paddingTop, tab.paddingRight, bottomPadding)
            tab.setBackgroundResource(
                if (isSelected) R.drawable.bg_tab_selected else R.drawable.bg_tab_unselected
            )
        }

        // Trigger classification filter
        packages.filterPackageByClassification(categories[selectedTab])
    }

}

