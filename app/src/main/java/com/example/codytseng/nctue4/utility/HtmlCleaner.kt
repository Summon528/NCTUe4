package com.example.codytseng.nctue4.utility

fun htmlCleaner(body: String) = body.replace("<!--[\\S\\s]*?-->".toRegex(), "")
