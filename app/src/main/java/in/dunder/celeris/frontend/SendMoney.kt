package `in`.dunder.celeris.frontend

import android.content.Context
import android.os.Bundle
import android.telephony.SmsManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.lifecycleScope
import `in`.dunder.celeris.db.AuthDatabaseHelper
import `in`.dunder.celeris.frontend.databinding.FragmentSendMoneyBinding
import `in`.dunder.celeris.utils.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SendMoney : Fragment() {
    private lateinit var binding: FragmentSendMoneyBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSendMoneyBinding.inflate(layoutInflater, container, false)
        val smsManager = requireContext().getSystemService(SmsManager::class.java)

        lifecycleScope.launch {
            val clientId = getClientIdFromDataStore()
            binding.merchantid.setText(clientId)
        }

        AuthDatabaseHelper(requireContext()).apply {
            binding.balance.text = user.balance.toString()

            binding.pay.setOnClickListener {
                val msg = user.id.toString() + "|" + binding.merchantid.text.toString() + "|" + binding.amount.text.toString()
                smsManager.sendTextMessage("+917050610065", null, msg, null, null)
            }
        }

        return binding.root
    }

    private suspend fun getClientIdFromDataStore(): String {
        val clientIdKey = stringPreferencesKey("client_id")
        val preferences = requireContext().dataStore.data.first()
        return preferences[clientIdKey] ?: ""
    }
}