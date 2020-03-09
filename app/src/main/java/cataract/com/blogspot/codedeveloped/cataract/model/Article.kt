package cataract.com.blogspot.codedeveloped.cataract

import android.text.Html
import android.text.Spanned

class Article(var title: String, var content: Spanned, var alertHasBeenSent: Boolean){
    constructor(): this("",Html.fromHtml(""), false)
}