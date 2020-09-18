package com.theshoremedia.utils.extensions

import android.R
import android.widget.ArrayAdapter
import android.widget.Spinner


/**
 * @author- Nitin Khanna
 * @date -
 */

fun <T> Spinner.setAdapter(items: List<T>) {
    val arrayAdapter = ArrayAdapter<T>(this.context, R.layout.simple_spinner_item, items)
    arrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
    this.adapter = arrayAdapter
}