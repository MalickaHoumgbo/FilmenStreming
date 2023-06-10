package fr.epf.mm.filmenstreaming

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class MovieViewHolder(view: View) : RecyclerView.ViewHolder(view)

class MovieAdapterVertical(val activity: Context, val movieList: List<film>) :
    RecyclerView.Adapter<MovieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        Log.d("PopularMovie inside Adapter: ", movieList.toString())
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.vue_verticale, parent, false)
        return MovieViewHolder(view)
    }

    override fun getItemCount(): Int = movieList.size

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movieList[position]
        val view = holder.itemView

        val affiche = view.findViewById<ImageView>(R.id.client_view_imageview)
        val posterPath = movie.poster_path
        val baseImageUrl = "https://image.tmdb.org/t/p/"
        val posterUrl = baseImageUrl + "w600_and_h900_bestv2" + posterPath

        val titre = view.findViewById<TextView>(R.id.textViewTitle)
        val release = view.findViewById<TextView>(R.id.textViewReleaseYear)
        val note = view.findViewById<TextView>(R.id.textViewVoteAverage)

        titre.text = movie.title
        release.text = "Date de sortie : " + movie.release_date
        note.text = "Note : " + movie.vote_average.toString() + "/10"
        Picasso.get()
            .load(posterUrl)
            .into(affiche)

        view.setOnClickListener {
            val intent = Intent(activity, DetailsActivity::class.java)
            intent.putExtra("movie", movie)
            Log.d("film movieadapter: ", movie.toString())
            activity.startActivity(intent)
        }
    }

}
class MovieAdapterHorizontal(val activity: Context, val movieList: List<film>) :
    RecyclerView.Adapter<MovieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        Log.d("PopularMovie inside Adapter: ", movieList.toString())
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.vue_horizontale, parent, false)
        return MovieViewHolder(view)
    }

    override fun getItemCount(): Int = movieList.size

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movieList[position]
        val view = holder.itemView

        val affiche = view.findViewById<ImageView>(R.id.client_view_imageview2)
        val posterPath = movie.poster_path
        val baseImageUrl = "https://image.tmdb.org/t/p/"
        val posterUrl = baseImageUrl + "w600_and_h900_bestv2" + posterPath

        Picasso.get()
            .load(posterUrl)
            .into(affiche)

        view.setOnClickListener {
            val intent = Intent(activity, DetailsActivity::class.java)
            intent.putExtra("movie", movie)
            Log.d("film movieadapter: ", movie.toString())
            activity.startActivity(intent)
        }
    }

}
