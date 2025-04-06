package `in`.dunder.celeris.frontend

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import `in`.dunder.celeris.db.AuthDatabaseHelper
import java.util.Objects

class SuccessActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_success)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        Toast.makeText(this@SuccessActivity, intent.getStringExtra("type").toString(), Toast.LENGTH_SHORT).show()
        val amount = intent.getIntExtra("amount", 0)
        Toast.makeText(this, amount.toString(), Toast.LENGTH_SHORT).show()

        if (Objects.equals(intent.getStringExtra("type"), "sent")) {
            supportFragmentManager.beginTransaction().replace(R.id.main, SentPage()).commit()
            val db = AuthDatabaseHelper(this@SuccessActivity)

            db.setBalance(db.user.balance - amount)
        } else if (Objects.equals(intent.getStringExtra("type"), "recieved")) {
            supportFragmentManager.beginTransaction().replace(R.id.main, ReceivePage()).commit()
            val db = AuthDatabaseHelper(this@SuccessActivity)

            db.setBalance(db.user.balance + amount)
        }
    }
}