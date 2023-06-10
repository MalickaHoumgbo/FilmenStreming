package fr.epf.mm.filmenstreaming

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import kotlinx.serialization.Serializable
import java.io.File

class filmfavoriActivity: AppCompatActivity() {
    private val apiKey = "43e979a8332a638db7eb7b77d704c34a"
    lateinit var recyclerView: RecyclerView
    private var  favoris = mutableListOf<String>()
    private var  listefav : ArrayList<film> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.filmfavorisactivity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        recyclerView = findViewById<RecyclerView>(R.id.list_favorites_recyclerview)
        recyclerView.layoutManager =
            LinearLayoutManager(this, GridLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = GridLayoutManager(this, GridLayoutManager.VERTICAL)
        recyclerView.adapter = MovieAdapterVertical(this@filmfavoriActivity, listefav)
        recupererFavoris()
        Log.d("Movie search main: ", listefav.toString())
        recyclerView.adapter?.notifyDataSetChanged()
    }

    fun recupererFavoris(): List<String> {
        val file = File("C:/Users/whatever/Documents/filmenstreaming/listeFavori.txt")
        if (file.exists() && file.canRead()) {
            file.bufferedReader().useLines { lines ->
                lines.forEach { line ->
                    val id = line.trim()
                    favoris.add(id)
                }
            }
        } else {
            println("Probleme")

        }
        println("Liste favoris $favoris")
        Recupererinfos(favoris)
        return favoris
    }

    fun Recupererinfos(favoris:  List<String>)= runBlocking{
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
            for (id in favoris) {
                val movieResult = service.finById(Integer.parseInt(id), apiKey) as film
                listefav.add(movieResult)
            }
            Log.d("Movie search fonction: ", listefav.toString())
        }
        val result = myGlobalVar.await()
        println(result)
    }

}