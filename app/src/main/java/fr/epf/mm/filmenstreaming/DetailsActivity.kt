package fr.epf.mm.filmenstreaming

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileWriter
import java.io.RandomAccessFile
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import androidx.core.content.ContextCompat

class DetailsActivity  : AppCompatActivity(){

    private val apiKey = "43e979a8332a638db7eb7b77d704c34a"
    lateinit var recyclerView: RecyclerView
    private var listRecos: ArrayList<film> = arrayListOf()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detailsfilmactivity)

        val extras = intent.extras
        val movieExtra = extras?.get("movie") as? film
        Log.d("film : ",movieExtra.toString() )
        recyclerView = findViewById<RecyclerView>(R.id.recommand_movies_recyclerview)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerView.layoutManager =
            LinearLayoutManager(this, GridLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = GridLayoutManager(this, GridLayoutManager.VERTICAL)
        recyclerView.adapter = MovieAdapterVertical(this@DetailsActivity, listRecos)

        val favoributton = findViewById<Button>(R.id.FavButton)
        val title = findViewById<TextView>(R.id.moviedetails_title_textview)
        val resume = findViewById<TextView>(R.id.moviedetails_resume_textview)
        val date = findViewById<TextView>(R.id.moviedetails_date_textview)
        val language = findViewById<TextView>(R.id.moviedetails_language_textview)
        val note = findViewById<TextView>(R.id.moviedetails_note_textview)
        val affiche = findViewById<ImageView>(R.id.detailsmovie_view_imageview)
        val posterPath = movieExtra?.poster_path
        val baseImageUrl = "https://image.tmdb.org/t/p/"
        val posterUrl = baseImageUrl + "w600_and_h900_bestv2" + posterPath

        Picasso.get()
            .load(posterUrl)
            .into(affiche)
        title.text = movieExtra?.title
        resume.text = movieExtra?.overview
        date.text = movieExtra?.release_date
        language.text = movieExtra?.original_language
        note.text = movieExtra?.vote_average.toString()+"/10"

        favoributton.setOnClickListener {
            ajouterFavori(movieExtra!!.id.toString())
            favoributton.isEnabled = false
        }
        Recommandations()
    }

    fun Recommandations() = runBlocking{
        val extras = intent.extras
        val movieExtra = extras?.get("movie") as? film
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
            val moviesResult = service.getRecommandatedMovies(movieExtra?.id,apiKey)

            Log.d("Movies : ", moviesResult.toString())

            if ( moviesResult!=null) {
                moviesResult.results.map {
                    listRecos.add(
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
                Log.d("liste reco: ", listRecos.toString())
            }
        }
        val result = myGlobalVar.await()
        println(result)
    }

    fun ajouterFavori(filmId: String) {
        val dataDir = applicationContext.filesDir
        val directoryName = "myDataDirectory"
        val directory = File(dataDir, directoryName)
        if (!directory.exists()) {
            directory.mkdirs()
        }

        val file = File(directory, "listefavori.txt")

        try {
            // Vérifier si le fichier existe
            if (!file.exists()) {
                file.createNewFile()
                if (file.exists()) {
                    println("Le fichier existe.")
                    val chemin = file.absolutePath.toString()
                    println("Chemin du fichier : $chemin")
                } else {
                    println("Le fichier n'existe pas ou le chemin est incorrect.")
                }
            }
            val contenu = file.readText()
            if (contenu.contains(filmId)) {
                println("Le film est déjà présent dans les favoris.")
                Toast.makeText(applicationContext, "Le film est déjà présent dans les favoris.", Toast.LENGTH_SHORT).show()
                return
            }
            // Ajouter le favori dans le fichier
            val fileWriter = FileWriter(file, true)
            fileWriter.write("$filmId\n")
            fileWriter.close()
            println("Contenu du fichier :")
            println(contenu)
            println("Le fichier existe.")
            val chemin = file.absolutePath.toString()
            println("Chemin du fichier : $chemin")
            println("Favori ajouté avec succès.")
            Toast.makeText(applicationContext, "Favori ajouté avec succès.", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            println("Erreur lors de l'ajout du favori : ${e.message}")
        }
    }

}