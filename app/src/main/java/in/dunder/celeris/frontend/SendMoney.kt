package `in`.dunder.celeris.frontend

import android.os.Bundle
import android.telephony.SmsManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import `in`.dunder.celeris.db.AuthDatabaseHelper
import `in`.dunder.celeris.db.DatabaseHelper
import `in`.dunder.celeris.frontend.databinding.FragmentProfilePageBinding
import `in`.dunder.celeris.frontend.databinding.FragmentSendMoneyBinding

class SendMoney : Fragment() {
    private lateinit var binding: FragmentSendMoneyBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSendMoneyBinding.inflate(layoutInflater, container, false)
        val smsManager = requireContext().getSystemService(SmsManager::class.java)

        AuthDatabaseHelper(requireContext()).apply {
            binding.pay.setOnClickListener {
                val msg = user.id.toString() + "|" + binding.merchantid.text.toString() + "|" + binding.amount.text.toString()

                smsManager.sendTextMessage("+917050610065",null,msg,null,null)
            }
        }

        return binding.root
    }
}