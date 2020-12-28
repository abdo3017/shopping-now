//package com.example.e_commerce.utils
//
//import androidx.room.TypeConverter
//import com.app.movie.datasource.network.models.movies.*
//import com.app.movie.datasource.network.models.tv.TVSeriesAiringTodayResult
//import com.app.movie.datasource.network.models.tv.TVSeriesOnTheAirResult
//import com.app.movie.datasource.network.models.tv.TVSeriesPopularResult
//import com.app.movie.datasource.network.models.tv.TVSeriesTopRatedResult
//import com.google.gson.Gson
//import com.google.gson.reflect.TypeToken
//
//
//class ModuleConverter {
//
//    @TypeConverter
//    fun fromMovieNowPlayingDates(value: MovieNowPlayingDates): String = fromModule(value)!!
//
//    @TypeConverter
//    fun toMovieNowPlayingDates(value: String): MovieNowPlayingDates = toModule(value)!!
//
//    @TypeConverter
//    fun fromListOfMovieNowPlayingResultsItem(value: List<MovieNowPlayingResultsItem>): String =
//        fromModule(value)!!
//
//    @TypeConverter
//    fun toListOfMovieNowPlayingResultsItem(value: String): List<MovieNowPlayingResultsItem> =
//        toModule(value)!!
//
//    @TypeConverter
//    fun fromMovieUpComingDates(value: MovieUpComingDates): String = fromModule(value)!!
//
//    @TypeConverter
//    fun toMovieUpComingDates(value: String): MovieUpComingDates = toModule(value)!!
//
//    @TypeConverter
//    fun fromListOfMovieUpComingResult(value: List<MovieUpComingResult>): String =
//        fromModule(value)!!
//
//    @TypeConverter
//    fun toListOfMovieUpComingResult(value: String): List<MovieUpComingResult> =
//        toModule(value)!!
//
//    @TypeConverter
//    fun fromListOfMovieTopRatedResult(value: List<MovieTopRatedResult>): String =
//        fromModule(value)!!
//
//    @TypeConverter
//    fun toListOfMovieTopRatedResult(value: String): List<MovieTopRatedResult> =
//        toModule(value)!!
//
//    @TypeConverter
//    fun fromListOfMoviePopularResult(value: List<MoviePopularResult>): String =
//        fromModule(value)!!
//
//    @TypeConverter
//    fun toListOfMoviePopularResult(value: String): List<MoviePopularResult> =
//        toModule(value)!!
//
//    @TypeConverter
//    fun fromListOfMovieVideosResultsItem(value: List<MovieVideosResultsItem>): String =
//        fromModule(value)!!
//
//    @TypeConverter
//    fun toListOfMovieVideosResultsItem(value: String): List<MovieVideosResultsItem> =
//        toModule(value)!!
//
//    @TypeConverter
//    fun fromListOfTVSeriesTopRatedResultsItem(value: List<TVSeriesTopRatedResult>): String =
//        fromModule(value)!!
//
//    @TypeConverter
//    fun toListOfTVSeriesTopRatedResultsItem(value: String): List<TVSeriesTopRatedResult> =
//        toModule(value)!!
//
//    @TypeConverter
//    fun fromListOfTVSeriesAiringTodayResultsItem(value: List<TVSeriesAiringTodayResult>): String =
//        fromModule(value)!!
//
//    @TypeConverter
//    fun toListOfTVSeriesAiringTodayResultsItem(value: String): List<TVSeriesAiringTodayResult> =
//        toModule(value)!!
//
//    @TypeConverter
//    fun fromListOfTVSeriesOnTheAirResultsItem(value: List<TVSeriesOnTheAirResult>): String =
//        fromModule(value)!!
//
//    @TypeConverter
//    fun toListOfTVSeriesOnTheAirResultsItem(value: String): List<TVSeriesOnTheAirResult> =
//        toModule(value)!!
//
//    @TypeConverter
//    fun fromListOfTVSeriesPopularResultsItem(value: List<TVSeriesPopularResult>): String =
//        fromModule(value)!!
//
//    @TypeConverter
//    fun toListOfTVSeriesPopularResultsItem(value: String): List<TVSeriesPopularResult> =
//        toModule(value)!!
//
//
//    private inline fun <reified T> fromModule(obj: T?): String? {
//        if (obj == null) {
//            return null
//        }
//        val gson = Gson()
//        val type = object : TypeToken<T?>() {}.type
//        return gson.toJson(obj, type)
//    }
//
//    private inline fun <reified T> toModule(objString: String?): T? {
//        if (objString == null) {
//            return null
//        }
//        val gson = Gson()
//        val type = object : TypeToken<T?>() {}.type
//        return gson.fromJson(objString, type)
//    }
//
//}
