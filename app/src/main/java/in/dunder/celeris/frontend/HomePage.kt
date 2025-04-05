package `in`.dunder.celeris.frontend

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import `in`.dunder.celeris.db.AuthDatabaseHelper
import `in`.dunder.celeris.frontend.databinding.FragmentHomePageBinding

class HomePage : Fragment() {
    private lateinit var binding: FragmentHomePageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomePageBinding.inflate(inflater, container, false)

        AuthDatabaseHelper(requireContext()).apply {
            binding.name.text = user.name
            binding.textView3.text = user.balance.toString()
        }
        binding.button.setOnClickListener {
            findNavController().navigate(R.id.action_homePage_to_sendMoney)
        }

        binding.button3.setOnClickListener {
            findNavController().navigate(R.id.action_homePage_to_scanPayFragment)
        }

        return binding.root
    }
}