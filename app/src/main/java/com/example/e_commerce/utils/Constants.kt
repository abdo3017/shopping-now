package com.example.e_commerce.utils

import java.text.SimpleDateFormat
import java.util.*

object Constants {
    const val CUSTOMER: String = "customer"
    const val REMEMBER_ME: String = "remember"

    //maps
    const val SOURCE_ID = "SOURCE_ID"
    const val ICON_ID = "ICON_ID"
    const val LAYER_ID = "LAYER_ID"

    //data base
    const val DATABASE_NAME: String = "blog_db"

    // api
    const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500"
    const val BASE_URL = "https://api.themoviedb.org/3/"
    const val LANGUAGE = "en-US"
    const val API_KEY = "0b05f42616ee89883ecf6adb6af55ae0"

    //movie
    const val NOW_PLAYING_MOVIE = BASE_URL + "movie/now_playing"
    const val UP_COMING_MOVIE = BASE_URL + "movie/upcoming"
    const val TOP_RATED_MOVIE = BASE_URL + "movie/top_rated"
    const val POPULAR_MOVIE = BASE_URL + "movie/popular"
    const val VIDEO = BASE_URL + "movie/{movie_id}/videos"

    //tv
    const val TOP_RATED_TV = BASE_URL + "tv/top_rated"
    const val POPULAR_TV = BASE_URL + "tv/popular"
    const val AIRING_TODAY_TV = BASE_URL + "tv/airing_today"
    const val ON_THE_AIR_TV = BASE_URL + "tv/on_the_air"
    const val YOUTUBE_VIDEO_THUMBNAIL = "https://img.youtube.com/vi/"

    const val YOUTUBE_WEB_LINK = "http://www.youtube.com/watch?v="
    const val YOUTUBE_APP_LINK = "vnd.youtube:"

    const val GITHUB_STARTING_PAGE_INDEX = 1
    const val NETWORK_PAGE_SIZE = 10
    fun getCurrentDate(): String {
        val c: Date = Calendar.getInstance().time
        val df = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
        return df.format(c)
    }
}
