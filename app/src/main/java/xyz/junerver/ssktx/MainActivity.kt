package xyz.junerver.ssktx

import android.Manifest
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.text.style.DynamicDrawableSpan
import android.util.Log
import android.widget.TextView


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
                setBackgroundColor("#ffff99")
            }
            addText("我已详细阅读并同意") {
                setBackgroundColor("#FF9900")
                setStyle(Typeface.BOLD_ITALIC)
                userUnderLine()
                useStrikethrough()
                setDrawableRight(R.drawable.icon_dingding, DynamicDrawableSpan.ALIGN_CENTER)
            }
            addText("打电话") {
                asURL("tel:10086")
            }
            addText("《隐私政策》") {
                setForegroundColor("#0099FF")
                setDrawableLeft(R.drawable.icon_wechat)
                onClick() {

                }
            }
        }
    }
}