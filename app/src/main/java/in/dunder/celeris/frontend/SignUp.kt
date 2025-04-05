package `in`.dunder.celeris.frontend

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import `in`.dunder.celeris.db.AuthDatabaseHelper
import `in`.dunder.celeris.frontend.databinding.FragmentSignUpBinding
import `in`.dunder.celeris.utils.RetrofitClient
import retrofit2.Call
import retrofit2.Response

data class SignupRequest(
    val email: String,
    val password: String,
    val name: String,
    val phoneNumber: String
)

data class SignupResponse(
    val message: String,
    val user_id: Int
)

class SignUp : Fragment() {
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)

        binding.signup.setOnClickListener {
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()
            val name = binding.name.text.toString()
            val phoneNumber = binding.phoneNumber.text.toString()

            if (email.isEmpty() || password.isEmpty() || name.isEmpty() || phoneNumber.isEmpty()) {
                Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding.signup.isEnabled = false

            val signupRequest = SignupRequest(email, password, name, phoneNumber)

            RetrofitClient.instance.signup(signupRequest).enqueue(object : retrofit2.Callback<SignupResponse> {
                override fun onResponse(call: Call<SignupResponse>, response: Response<SignupResponse>) {
                    binding.signup.isEnabled = true

                    if (response.isSuccessful) {
                        val res = response.body()
                        val context = requireContext()

                        if (res != null) {
                            val dbHelper = AuthDatabaseHelper(context)
                            dbHelper.insertUser(
                                res.user_id,
                                binding.name.text.toString(),
                                0,
                                binding.email.text.toString(),
                                binding.phoneNumber.text.toString()
                            )

                            val intent = Intent(requireContext(), SecureActivity::class.java)
                            startActivity(intent)
                            requireActivity().finish()
                        }
                    } else {
                        Toast.makeText(context, "Signup failed: ${response.code()}", Toast.LENGTH_LONG).show()
                    }
                }


                override fun onFailure(call: Call<SignupResponse>, t: Throwable) {
                    binding.signup.isEnabled = true
                    Toast.makeText(context, "Network error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        }

        binding.signInText.setOnClickListener {
            val loginFragment = Login()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.auth_main, loginFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        return binding.root
    }
}
