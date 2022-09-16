package com.anderson.notepad.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Note(
    var code:Long = 0,
    var title:String = "",
    var content:String = "",
    var time:Long = 0,
    var last:Long = 0
): Parcelable {

    override fun equals(other: Any?): Boolean {

        other?.let {

            if (it is Note) {

                if (
                    code == it.code &&
                    title == it.title &&
                    content == it.content &&
                    time == it.time &&
                    last == it.last &&
                    hashCode() == it.hashCode()
                ) {
                    return true
                }

            }

        }

        return false
    }

    override fun hashCode(): Int {
        var result = code.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + content.hashCode()
        result = 31 * result + time.hashCode()
        result = 31 * result + last.hashCode()
        return result
    }

}