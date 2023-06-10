package fr.epf.mm.filmenstreaming

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class film(
    var adult: Boolean?,
    var overview: String?,
    var release_date: String?,
    var id: Int?,
    var original_language: String?,
    var title: String?,
    var popularity: Double?,
    var vote_count: Int?,
    var vote_average: Double?,
    var poster_path: String?
) : Parcelable {
    companion object{

    }
    class SearchMoviesResponse(
        val page: Int,
        val results: List<film>,
        val total_results: Int,
        val total_pages: Int
    )

    override fun toString(): String {
        return "{" +
                "adult='$adult', " +
                "overview='$overview', " +
                "release_date='$release_date', " +
                "id='$id', " +
                "original_language=$original_language, " +
                "title=$title, " +
                "popularity=$popularity, " +
                "vote_count=$vote_count, " +
                "vote_average=$vote_average, " +
                "}"
    }
}

