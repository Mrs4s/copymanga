package top.fumiama.copymanga.ui.vm

import android.widget.Toast
import top.fumiama.copymanga.manga.Reader
import top.fumiama.copymanga.ui.vm.ViewMangaActivity.Companion.comicName
import top.fumiama.copymanga.ui.vm.ViewMangaActivity.Companion.position
import java.lang.ref.WeakReference

class PagesManager(private val w: WeakReference<ViewMangaActivity>) {
    val v get() = w.get()
    private var isEndL = false
    private var isEndR = false
    @ExperimentalStdlibApi
    fun toPreviousPage(){
        toPage(v?.r2l==true)
    }
    @ExperimentalStdlibApi
    fun toNextPage(){
        toPage(v?.r2l!=true)
    }
    private fun judgePrevious() = (v?.pageNum ?: 0) > 1
    private fun judgeNext() = (v?.pageNum ?: 0) < (v?.realCount ?: 0)
    @ExperimentalStdlibApi
    fun toPage(goNext:Boolean) {
        v?.let { v ->
            if (v.clicked) {
                v.hideObjs()
                return
            }
            if (if(goNext)judgeNext() else judgePrevious()) {
                if(goNext) {
                    v.scrollForward()
                    isEndR = false
                } else {
                    v.scrollBack()
                    isEndL = false
                }
                return
            }
            val chapterPosition = position + if(goNext) 1 else -1
            if (v.urlArray.isNotEmpty()) {
                if(chapterPosition >= 0 && chapterPosition < v.urlArray.size) v.urlArray[chapterPosition].let {
                    if (if(goNext) isEndR else isEndL) {
                        //if(v.zipFirst) intent.putExtra("callFrom", "zipFirst")
                        v.tt.canDo = false
                        //ViewMangaActivity.dlhandler = null
                        comicName?.let { it1 -> Reader.viewMangaAt(it1, chapterPosition, v.urlArray, goNext) }
                        v.finish()
                        return
                    }
                    val hint = if(goNext) '下' else '上'
                    Toast.makeText(
                        v.applicationContext,
                        "再次按下加载${hint}一章",
                        Toast.LENGTH_SHORT
                    ).show()
                    if(goNext) isEndR = true
                    else isEndL = true
                } else Toast.makeText(
                    v.applicationContext,
                    "已经到头了~",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    fun manageInfo(){
        if (v?.clicked == false) v?.showObjs() else v?.hideObjs()
    }
}
