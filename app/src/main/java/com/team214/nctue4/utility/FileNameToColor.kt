package com.team214.nctue4.utility

import com.team214.nctue4.R

class FileNameToColor {

    fun getId(fileName: String): Int {
        val key = fileName.split(".").last()
        return if (key in iconMap) iconMap[key]!! else R.color.md_pink_700
    }

    companion object {
        val iconMap = hashMapOf(
                "png" to R.color.md_deep_orange_700,
                "jpg" to R.color.md_deep_orange_700,
                "jpeg" to R.color.md_deep_orange_700,
                "gif" to R.color.md_deep_orange_700,
                "bmp" to R.color.md_deep_orange_700,
                "tif" to R.color.md_deep_orange_700,
                "tiff" to R.color.md_deep_orange_700,
                "svg" to R.color.md_deep_orange_700,
                "7z" to R.color.md_brown_400,
                "rar" to R.color.md_brown_400,
                "zip" to R.color.md_brown_400,
                "gz" to R.color.md_brown_400,
                "deb" to R.color.md_brown_400,
                "rpm" to R.color.md_brown_400,
                "pkg" to R.color.md_brown_400,
                "mp3" to R.color.md_blue_300,
                "wav" to R.color.md_blue_300,
                "wma" to R.color.md_blue_300,
                "wpa" to R.color.md_blue_300,
                "aif" to R.color.md_blue_300,
                "cda" to R.color.md_blue_300,
                "mid" to R.color.md_blue_300,
                "midi" to R.color.md_blue_300,
                "ogg" to R.color.md_blue_300,
                "cpp" to R.color.md_grey_600,
                "c" to R.color.md_grey_600,
                "py" to R.color.md_grey_600,
                "java" to R.color.md_grey_600,
                "kt" to R.color.md_grey_600,
                "js" to R.color.md_grey_600,
                "ts" to R.color.md_grey_600,
                "json" to R.color.md_grey_600,
                "xml" to R.color.md_grey_600,
                "htm" to R.color.md_grey_600,
                "html" to R.color.md_grey_600,
                "xls" to R.color.md_green_400,
                "xlsx" to R.color.md_green_400,
                "ods" to R.color.md_green_400,
                "pdf" to R.color.md_red_A200,
                "xls" to R.color.md_green_400,
                "ppt" to R.color.md_red_500,
                "pptx" to R.color.md_red_500,
                "pps" to R.color.md_red_500,
                "ppsx" to R.color.md_red_500,
                "odp" to R.color.md_red_500,
                "doc" to R.color.md_blue_400,
                "docx" to R.color.md_blue_400,
                "odt" to R.color.md_blue_400
        )
    }
}