package xyz.junerver.ssktx

import android.graphics.Typeface
import android.os.Bundle
import android.text.Layout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<TextView>(R.id.tv_test).buildSpannableString {
            "你可以这样，".asSpannableString {
                setBackgroundColor("#ff0099")
            }
            "还可以这样用"{
                setAlignment(Layout.Alignment.ALIGN_CENTER)
                setTextSize(44)
                setBackgroundColor("#ffff99")
            }
            addText("\n我已详细阅读并同意") {
                setBackgroundColor("#FF9900")
                setStyle(Typeface.BOLD_ITALIC)
                userUnderLine()
                useStrikethrough()
                setDrawableRight(R.drawable.icon_dingding)
            }
            addText("\n打电话") {
                asURL("tel:10086")
            }
            addText("《隐私政策》") {
                setForegroundColor("#0099FF")
                setDrawableLeft(R.drawable.icon_wechat)
                onClick {

                }
            }
        }

        findViewById<TextView>(R.id.tv_test2).buildSpannableString {
            "我是一个居中的文字内容"{
                setAlignment(Layout.Alignment.ALIGN_CENTER)
                setTextSize(44)
                setBackgroundColor("#ffff99")
            }
        }
    }
}