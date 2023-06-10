package fr.epf.mm.filmenstreaming

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor

class RechercheActivity : AppCompatActivity() {
    private val apiKey = "43e979a8332a638db7eb7b77d704c34a"
    lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private var listMovies: ArrayList<film> = arrayListOf()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recherchefilmactivity)
        recyclerView = findViewById<RecyclerView>(R.id.list_movies_recyclerview)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerView.layoutManager =
            LinearLayoutManager(this, GridLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = GridLayoutManager(this, GridLayoutManager.VERTICAL)
        recyclerView.adapter = MovieAdapterVertical(this@RechercheActivity, listMovies)

        searchView = findViewById(R.id.searchView)
        val searchButton = findViewById<Button>(R.id.SearchButton)
        searchButton.setOnClickListener {
            val query = searchView.query.toString()
            performSearch(query)
            recyclerView.adapter?.notifyDataSetChanged()
        }

    }

    fun performSearch(query: String) = runBlocking {
        val myGlobalVar = GlobalScope.async {
            val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/3/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            val service = retrofit.create(filmservice::class.java)
            val moviesResult = service.getMovies(apiKey,query,1)

            Log.d("Movies : ", moviesResult.toString())

            if ( moviesResult!=null) {
                moviesResult.results.map {
                    listMovies.add(
                        film(
                            it.adult,
                            it.overview,
                            it.release_date,
                            it.id,
                            it.original_language,
                            it.title,
                            it.popularity,
                            it.vote_count,
                            it.vote_average,
                            it.poster_path
                        )
                    )
                }
                Log.d("liste film: ", listMovies.toString())
            }
        }
        val result = myGlobalVar.await()
        println(result)
    }
}
