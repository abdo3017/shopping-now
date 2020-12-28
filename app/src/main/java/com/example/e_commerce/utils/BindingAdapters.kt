package com.example.e_commerce.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.e_commerce.R

object BindingAdapters {
    @JvmStatic
    @BindingAdapter("load_backdrop")
    fun loadBackdrop(image: ImageView, url: String?) {
        if (url != null) {
            val context = image.context
            Glide.with(context).load(url)
                .placeholder(R.mipmap.hustle)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .into(image)
        }
    }
}
//
//import android.widget.ImageView
//import android.widget.RatingBar
//import androidx.databinding.BindingAdapter
//import androidx.recyclerview.widget.RecyclerView
//import com.example.e_commerce.utils.Constants
//import com.bumptech.glide.Glide
//import com.bumptech.glide.load.engine.DiskCacheStrategy
//
//@Suppress("UNCHECKED_CAST")
//object BindingAdapters {
//
//    @JvmStatic
//    @BindingAdapter("android:ratingBar")
//    fun setRatingBarView(movieRate: RatingBar, movieVoteAverage: Double?) {
//        val rating = (movieVoteAverage!! * 5 / 9).toFloat()
//        movieRate.numStars = 5
//        movieRate.stepSize = 0.1f
//        movieRate.rating = rating
//        movieRate.setIsIndicator(true)
//    }
//
//    @JvmStatic
//    @BindingAdapter("android:trailerImage")
//    fun setMovieTrailerImage(movieTrailerThumbnail: ImageView, trailerKey: String?) {
//        Glide.with(movieTrailerThumbnail.context)
//            .load(Constants.YOUTUBE_VIDEO_THUMBNAIL + trailerKey + "/0.jpg")
//            .placeholder(R.mipmap.hustle)
//            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
//            .into(movieTrailerThumbnail)
//    }
//
//    @JvmStatic
//    @BindingAdapter("android:recyclerAdapter")
//    fun <T> setRecyclerViewData(recyclerView: RecyclerView, items: MutableList<T>?) {
//        items?.let { (recyclerView.adapter as BaseRecyclerViewAdapter<T>?)?.addItems(it) }
//    }
//
//    @JvmStatic
//    @BindingAdapter("load_backdrop")
//    fun loadBackdrop(image: ImageView, url: String?) {
//        if (url != null) {
//            val context = image.context
//            Glide.with(context).load("https://image.tmdb.org/t/p/original/$url")
//                .placeholder(R.mipmap.hustle)
//                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
//                .into(image)
//        }
//    }
//
//    @JvmStatic
//    @BindingAdapter("load_poster")
//    fun loadPoster(image: ImageView, url: String?) {
//        if (url != null) {
//            val context = image.context
//            Glide.with(context).load("https://image.tmdb.org/t/p/w200/$url")
//                .placeholder(R.mipmap.hustle)
//                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
//                .into(image)
//        }
//    }
//
//    @JvmStatic
//    @BindingAdapter("loadImageUrl")
//    fun loadImage(imageView: ImageView, url: String?) {
//        url?.let {
//            Glide.with(imageView)
//                .load(IMAGE_BASE_URL + url)
//                .placeholder(R.mipmap.hustle)
//                .into(imageView)
//        }
//    }
//}