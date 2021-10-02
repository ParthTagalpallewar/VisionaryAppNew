package com.reselling.visionary.utils

import androidx.lifecycle.LiveData
import com.reselling.visionary.R
import com.reselling.visionary.data.models.category.CategoryModel

//selected_category
const val Choose = "Choose Categories"
const val SCHOOL_BOOKS = "School Books"
const val CLG_BOOKS = "College Books"
const val MPSC = "Mpsc Exam"
const val UPSC = "Upsc Exam"
const val NOVELS = "Novels Books"
const val Biography = "Biographies"
const val AutoBiography = "Auto Biographies"
const val Notes = "Notes"
const val General = "General Books"
const val Other = "Other Books"
const val ROMANTIC = "Romantic"
const val Short_Stories_books = "Short Stories"
const val HORROR = "Horror Books"
const val Thriller = "Thriller"
const val Comedy = "Comedy Books"
const val ClassicLiterature = "Classic Literature"
const val Science_Fiction = "Science Fiction"
const val LifeStories_Books = "LifeStories"
const val FINANCE = "Finance"
const val Historical = "Historical Books"
const val SPIRITUAL = "Spiritual"


val getCategory = arrayListOf<String>(
        Choose,
        SCHOOL_BOOKS,
        CLG_BOOKS,
        MPSC,
        UPSC,
        NOVELS,
        AutoBiography,
        Biography,
        Notes,
        General,
        ROMANTIC,
        Short_Stories_books,
        HORROR,
        Thriller,
        Comedy,
        ClassicLiterature,
        Science_Fiction,
        LifeStories_Books,
        FINANCE,
        Historical,
        SPIRITUAL,
        Other
)

val homeCategories = arrayListOf(
        CategoryModel("1", SCHOOL_BOOKS, R.color.lightTeal),
        CategoryModel("2", CLG_BOOKS, R.color.lightRed),
        CategoryModel("3", NOVELS, R.color.lightBlue),
        CategoryModel("4", AutoBiography, R.color.lightYellow),
        CategoryModel("5", Biography, R.color.lightPurple),
        CategoryModel("6", Notes, R.color.lightBrown))

val allCategories = arrayListOf(
        CategoryModel("1", SCHOOL_BOOKS, R.color.lightTeal),
        CategoryModel("2", CLG_BOOKS, R.color.lightYellow),
        CategoryModel("3", NOVELS, R.color.lightBlue),
        CategoryModel("4", AutoBiography, R.color.lightYellow),
        CategoryModel("5", MPSC, R.color.lightPurple),
        CategoryModel("6", UPSC, R.color.lightTeal),
        CategoryModel("7", Biography, R.color.warning_stroke_color),
        CategoryModel("8", General, R.color.color_sweet),
        CategoryModel("9", Notes, R.color.success_stroke_color),
        CategoryModel("10", ROMANTIC, R.color.color_bitter),
        CategoryModel("11", Short_Stories_books, R.color.color_savory),
        CategoryModel("12", HORROR, R.color.blue_200),
        CategoryModel("13", Thriller, R.color.color_sweet),
        CategoryModel("14", Comedy, R.color.blue_200),
        CategoryModel("15", ClassicLiterature, R.color.purple_200),
        CategoryModel("16", Science_Fiction, R.color.blue_200),
        CategoryModel("17", LifeStories_Books, R.color.lightYellow),
        CategoryModel("18", Historical, R.color.green_light),
        CategoryModel("19", SPIRITUAL, R.color.lightBrown),
        CategoryModel("20", Other, R.color.lightYellow),





)

//₹