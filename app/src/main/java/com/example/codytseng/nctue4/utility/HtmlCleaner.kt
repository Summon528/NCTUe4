package com.example.codytseng.nctue4.utility

import android.util.Log

/**
 * Created by s094392 on 3/16/18.
 */
fun HtmlCleaner(body: String): String {
    Log.d("TEXT", body)
    var result = body.replace("<!--[\\S\\s]*?-->".toRegex(), "")
    result = result.replace("[0-9a-zA-Z[\\.]#]+ [\\{][\\s\\S]*?[\\}]".toRegex(), "")
    result = result.replace("(?<=(<([a-z])+ ))(.*?)(?=(?=>))".toRegex(), "")
    return result
}
