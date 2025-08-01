package android.app.barcafanprofileapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    val playerModels: ArrayList<PlayerModel> = ArrayList<PlayerModel>()
    lateinit var username: String
    lateinit var favoritePlayerName: String

    fun setUpPlayerModels(username: String, favoritePlayerName: String){
        val newPlayer = PlayerModel(username, favoritePlayerName)
        playerModels.add(newPlayer)
    }

    @SuppressLint("SetTextI18n", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // persisting data
        

        lateinit var adapter: PlayerAdapter

        val usernameEditText: EditText = findViewById(R.id.usernameEditText)
        val favPlayerEditText: EditText = findViewById(R.id.favPlayerNameEditText)
        val generateCardBtn: Button = findViewById(R.id.generateCardBtn)
        val playerRecyclerView: RecyclerView = findViewById(R.id.playerRecyclerView)

        generateCardBtn.setOnClickListener {
            if (usernameEditText.text.isBlank()) {
                Toast.makeText(this, "Please input your username!", Toast.LENGTH_SHORT).show()
            } else if (favPlayerEditText.text.isBlank()) {
                Toast.makeText(this, "Please input your favorite player!", Toast.LENGTH_SHORT).show()
            } else {
                username = usernameEditText.text.toString()
                favoritePlayerName = favPlayerEditText.text.toString()
                setUpPlayerModels(username, favoritePlayerName)

                adapter.notifyItemInserted(playerModels.size - 1)

                usernameEditText.text.clear()
                favPlayerEditText.text.clear()

                Toast.makeText(this, "Player Card Generated!", Toast.LENGTH_SHORT).show()
            }
        }

        adapter = PlayerAdapter(playerModels)

        playerRecyclerView.adapter = adapter
        playerRecyclerView.layoutManager = LinearLayoutManager(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
