package github.sachin2dehury.nitrmail.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import github.sachin2dehury.nitrmail.R
import github.sachin2dehury.nitrmail.adapters.MailBoxAdapter
import github.sachin2dehury.nitrmail.api.calls.AppClient
import github.sachin2dehury.nitrmail.databinding.FragmentMailBoxBinding
import github.sachin2dehury.nitrmail.others.Constants
import github.sachin2dehury.nitrmail.ui.viewmodels.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MailBoxFragment : Fragment(R.layout.fragment_mail_box) {

    lateinit var mainViewModel: MainViewModel

    @Inject
    lateinit var mailBoxAdapter: MailBoxAdapter

    @Inject
    lateinit var appClient: AppClient

    private var _binding: FragmentMailBoxBinding? = null
    private val binding: FragmentMailBoxBinding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentMailBoxBinding.bind(view)

        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        getMails()
        setUpAdapter()
        setupRecyclerView()
        subscribeToObservers()
    }

    private fun getMails() = CoroutineScope(Dispatchers.IO).launch {
        appClient.makeMailRequest(Constants.INBOX_URL)
    }

    private fun setUpAdapter() {
        mailBoxAdapter.apply {
            appClient = this@MailBoxFragment.appClient
            navController = findNavController()
        }
    }

    private fun setupRecyclerView() = binding.recyclerViewMailBox.apply {
        adapter = mailBoxAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }

    private fun subscribeToObservers() {
        mainViewModel.mails.observe(viewLifecycleOwner) { result ->
            result?.let { data ->
                binding.progressBarMailBox.isVisible = false
                mailBoxAdapter.mails = data.mails
            }
        }
    }
}