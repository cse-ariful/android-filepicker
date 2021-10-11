package com.nightcode.mediapicker.presentation

import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.nightcode.mediapicker.R
import java.text.DecimalFormat


object ViewExtension {
    fun Boolean.asVisibility(): Int {
        return if (this) View.VISIBLE
        else View.GONE
    }

    fun Boolean.showOrInvisible(): Int {
        return if (this) View.VISIBLE
        else View.INVISIBLE
    }

    fun View.show() {
        this.visibility = View.VISIBLE
    }
    fun View.showIf(boolean: Boolean) {
        if(boolean)show()
        else hide()
    }

    fun View.hide() {
        this.visibility = View.GONE
    }

    fun AppCompatActivity.setupToolbar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


    }

    fun ImageView.load(url: String) {
        Glide.with(this)
            .load(url)
            .placeholder(R.drawable.video_placeholder)
            //.transition(DrawableTransitionOptions.withCrossFade())
            .into(this)
    }

    fun View.animatedHide(duration: Long = 1000) {
        this.animate().setDuration(duration).alpha(0.0f)
            .withEndAction { this.visibility = View.GONE }
    }

    fun View.animatedShow(duration: Long = 1000) {
        if (this.alpha == 1f && this.visibility != View.VISIBLE) this.alpha = 0f
        this.visibility = View.VISIBLE
        this.animate().setDuration(duration).setInterpolator(AccelerateDecelerateInterpolator())
            .alpha(1f)
    }

    fun Long?.toReadableSize(): String {
        if (this == null) return "--"
        val df = DecimalFormat("0.00")
        val sizeKb = 1024.0f
        val sizeMb = sizeKb * sizeKb
        val sizeGb = sizeMb * sizeKb
        val sizeTerra = sizeGb * sizeKb
        if (this == 0L) return ""
        return when {
            this < sizeMb -> df.format(this / sizeKb)
                .toString() + " Kb"
            this < sizeGb -> df.format(this / sizeMb)
                .toString() + " Mb"
            this < sizeTerra -> df.format(this / sizeGb)
                .toString() + " Gb"
            else -> "--"
        }
    }

    fun Long?.toReadableTime(): String {
        if (this == null) return "--"
        if (this < 0) return "00:00:00"
        Log.d("timeTest", "toReadableTime: $this")
        val sec = (this / 1000) % 60
        val min = (this / (1000 * 60) % 60)
        val hr = (this / (1000 * 60 * 60) % 24)
        return String.format("%d:%02d:%02d", hr, min, sec)
    }
}