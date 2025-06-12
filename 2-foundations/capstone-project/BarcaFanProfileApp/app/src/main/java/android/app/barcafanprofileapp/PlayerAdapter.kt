package android.app.barcafanprofileapp

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PlayerAdapter(private val playerList: ArrayList<PlayerModel>):
    RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder>(){

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlayerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.player_item, parent, false)
        return PlayerViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        val currentPlayer = playerList[position]
        holder.bind(currentPlayer)
    }

    override fun getItemCount(): Int {
        return playerList.size
    }

    inner class PlayerViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.usernameTextView)
        private val numberTextView: TextView = itemView.findViewById(R.id.favPlayerTextView)

        @SuppressLint("SetTextI18n")
        fun bind(player: PlayerModel) {
            nameTextView.text = "Username: ${player.username}"
            numberTextView.text = "Favorite Player: ${player.favoritePlayerName}"
        }
    }
}