package `in`.dunder.celeris.frontend

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import `in`.dunder.celeris.db.AuthDatabaseHelper
import `in`.dunder.celeris.frontend.databinding.FragmentLoginBinding
import `in`.dunder.celeris.utils.RetrofitClient
import retrofit2.Call
import retrofit2.Response

data class LoginRequest(
    val email: String,
    val password: String,
)

data class LoginResponse(
    val message: String,
    val user_id: Int,
    val name: String,
    val balance: Int,
    val email: String,
    val phoneNumber: String,
)

class Login : Fragment() {
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater,  container, false)

        binding.login.setOnClickListener {
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding.login.isEnabled = false

            val loginRequest = LoginRequest(email, password)
            RetrofitClient.instance.login(loginRequest).enqueue(object : retrofit2.Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    binding.login.isEnabled = true

                    if (response.isSuccessful) {
                        val res = response.body()
                        val context = requireContext()

                        if (res != null) {
                            Toast.makeText(context, res.message, Toast.LENGTH_SHORT).show()

                            val dbHelper = AuthDatabaseHelper(context)
                            dbHelper.saveUser(res.user_id, res.name, res.balance, res.email, res.phoneNumber)
                            val intent = Intent(context, SecureActivity::class.java)
                            startActivity(intent)
                            activity?.finish()
                        }
                    } else {
                        Toast.makeText(context, "Login failed", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    binding.login.isEnabled = true
                    Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show()
                }
            })
        }

        binding.signUp.setOnClickListener {
            val signUpFragment = SignUp()
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.auth_main, signUpFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        return binding.root
    }
}