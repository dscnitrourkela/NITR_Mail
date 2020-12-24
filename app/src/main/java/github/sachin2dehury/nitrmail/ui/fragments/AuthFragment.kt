package github.sachin2dehury.nitrmail.ui.fragments

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import github.sachin2dehury.nitrmail.R
import github.sachin2dehury.nitrmail.api.calls.BasicAuthInterceptor
import github.sachin2dehury.nitrmail.databinding.FragmentAuthBinding
import github.sachin2dehury.nitrmail.others.Constants
import github.sachin2dehury.nitrmail.others.DataStoreExt
import github.sachin2dehury.nitrmail.others.Status
import github.sachin2dehury.nitrmail.ui.viewmodels.AuthViewModel
import kotlinx.coroutines.launch
import okhttp3.Credentials
import javax.inject.Inject

@AndroidEntryPoint
class AuthFragment : Fragment(R.layout.fragment_auth) {

    private val viewModel: AuthViewModel by viewModels()

    @Inject
    lateinit var basicAuthInterceptor: BasicAuthInterceptor

    @Inject
    lateinit var dataStore: DataStoreExt

    private var credential = Constants.NO_CREDENTIAL

    private var _binding: FragmentAuthBinding? = null
    private val binding: FragmentAuthBinding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isLoggedIn()) {
            authenticate()
            findNavController().navigate(R.id.action_authFragment_to_mailBoxFragment)
        }

        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        subscribeToObservers()

        _binding = FragmentAuthBinding.bind(view)

        binding.buttonLogin.setOnClickListener {
            getCredential()
            authenticate()
        }
    }

    private fun getCredential() {
        val roll = binding.editTextUserRoll.text.toString()
        val password = binding.editTextUserPassword.text.toString()
        credential = Credentials.basic(roll, password)
    }

    private fun isLoggedIn(): Boolean {
        lifecycleScope.launch {
            credential =
                dataStore.readCredential(Constants.KEY_CREDENTIAL) ?: Constants.NO_CREDENTIAL
        }
        return credential != Constants.NO_CREDENTIAL
    }

    private fun authenticate() {
        basicAuthInterceptor.credential = credential
        viewModel.login(credential)
    }

    private fun subscribeToObservers() {
        viewModel.loginStatus.observe(viewLifecycleOwner, { result ->
            result?.let {
                when (result.status) {
                    Status.SUCCESS -> {
                        lifecycleScope.launch {
                            dataStore.saveCredential(Constants.KEY_CREDENTIAL, credential)
                        }
                        binding.progressBar.visibility = View.GONE
                        showSnackbar("Successfully logged in")
                        findNavController().navigate(R.id.action_authFragment_to_mailBoxFragment)
                    }
                    Status.ERROR -> {
                        binding.progressBar.visibility = View.GONE
                        showSnackbar("An unknown error occured")
                    }
                    Status.LOADING -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    private fun showSnackbar(text: String) {
        Snackbar.make(binding.root, text, Snackbar.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}