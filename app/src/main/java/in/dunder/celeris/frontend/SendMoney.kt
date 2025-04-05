package `in`.dunder.celeris.frontend

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import `in`.dunder.celeris.db.AuthDatabaseHelper
import `in`.dunder.celeris.db.DatabaseHelper
import `in`.dunder.celeris.frontend.databinding.FragmentProfilePageBinding
import `in`.dunder.celeris.frontend.databinding.FragmentSendMoneyBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SendMoney : Fragment() {
    private lateinit var binding: FragmentSendMoneyBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSendMoneyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val idInputField = view.findViewById<EditText>(R.id.input_id)

        lifecycleScope.launch {
            val clientId = getClientIdFromDataStore()
            idInputField.setText(clientId)
        }

        AuthDatabaseHelper(requireContext()).apply {
            Toast.makeText(requireContext(), user.id.toString(), Toast.LENGTH_LONG).show()
            binding.balance.text = user.balance.toString()
        }
    }

    private suspend fun getClientIdFromDataStore(): String {
        val clientIdKey = stringPreferencesKey("client_id")
        val preferences = requireContext().dataStore.data.first()
        return preferences[clientIdKey] ?: ""
    }
}
