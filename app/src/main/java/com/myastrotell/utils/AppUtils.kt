package com.myastrotell.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.*
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.provider.Settings
import android.text.InputFilter
import android.util.DisplayMetrics
import android.util.Patterns
import android.view.Window
import android.view.WindowManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.myastrotell.BuildConfig
import com.myastrotell.R
import com.myastrotell.data.ApiEndPoints
import kotlinx.android.synthetic.main.dialog_alert.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


/**
 * created by Navjot Singh
 * on 02/02/2020
 * This class contains the helper methods to be used in the application
 */
object AppUtils {

    const val DF_dd_MMMM_yyyy = "dd-MMMM-yyyy"
    const val DF_dd_MM_yyyy = "dd MM yyyy"
    const val DF_dd_MMM_yyyy = "dd MMM yyyy"
    const val DF_dd_MMM_yy_hh_mm_aa = "dd MMM yy, hh:mm aa"
    const val DF_HH_mm = "HH:mm"
    const val DF_hh_mm_aa = "hh:mm aa"


    fun convertDpToPixel(dp: Float, context: Context): Float {
        return dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }


    fun convertPixelsToDp(px: Float, context: Context): Float {
        return px / (context.getResources()
            .getDisplayMetrics().densityDpi as Float / DisplayMetrics.DENSITY_DEFAULT)
    }


    @SuppressLint("HardwareIds")
    fun getDeviceId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun parseDateTimeFormat(
        input: String?,
        inputFormat: String = DF_dd_MM_yyyy,
        outputFormat: String = DF_dd_MMMM_yyyy
    ): String {
        if (input != null) {
            try {
                val inputDateFormat = SimpleDateFormat(inputFormat, Locale.ENGLISH)
                val outputDateFormat = SimpleDateFormat(outputFormat, Locale.ENGLISH)
                val date = inputDateFormat.parse(input)
                return if (date != null)
                    outputDateFormat.format(date)
                else
                    ""
            } catch (e: Exception) {
                return input
            }
        } else {
            return ""
        }
    }


    fun timeStampToDate(timestamp: Long, outputFormat: String = "MMMM yyyy"): String {
        try {
            val outputDateFormat = SimpleDateFormat(outputFormat, Locale.getDefault())
            val date: Date? = Date(timestamp)
            return if (date != null)
                outputDateFormat.format(date)
            else
                ""
        } catch (e: Exception) {
            return ""
        }
    }

    fun dateToTimeStamp(value: String, inputFormat: String): Long {
        try {
            val inputDateFormat = SimpleDateFormat(inputFormat, Locale.getDefault())
            val date = inputDateFormat.parse(value)
            return date?.time ?: 0
        } catch (e: Exception) {
            return 0
        }
    }
    fun getTimestampFromStringDate(stringDate: String, formatOfDate: String): Long {
        return try {
            val formatter: DateFormat = SimpleDateFormat(formatOfDate, Locale.ENGLISH)
            val date = formatter.parse(stringDate) as Date
            date.time

        } catch (e:java.lang.Exception){
            0
        }

    }


    /**
     * method to copy text to clipboard
     */
    fun copyTextToClipboard(context: Context?, text: String?) {
        if (context != null) {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
            val clip = ClipData.newPlainText(context.packageName, text)
            if (clipboard == null || clip == null) return
            clipboard.setPrimaryClip(clip)
        }
    }


    /**
     * method to share application
     */
    fun openAppInGooglePlayStore(context: Context?) {
        if (context != null) {
            val appId: String = context.packageName
            val uri: Uri = Uri.parse("market://details?id=$appId")
            val goToMarket = Intent(Intent.ACTION_VIEW, uri)
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            try {
                context.startActivity(goToMarket)
            } catch (e: ActivityNotFoundException) {
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=$appId")
                    )
                )
            }
        }
    }


    /**
     * method to share application
     */
    fun shareApplication(
        context: Context?,
        subject: String,
        text: String
    ) {
        if (context != null) {
            val appName = context.getString(R.string.app_name)
            val appId = BuildConfig.APPLICATION_ID

            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
            //shareIntent.putExtra(Intent.EXTRA_TEXT, ("$text\n$appName:\nhttp://play.google.com/store/apps/details?id=$appId"))
            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "$text\n$appName:\nhttp://play.google.com/store/apps/details?id=$appId")
            context.startActivity(Intent.createChooser(shareIntent, "Share via:"))
        }
    }


    /**
     * method to share text with Intent chooser
     */
    fun shareTextWithIntentChooser(
        context: Context?,
        subject: String,
        text: String
    ) {
        if (context != null) {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
            shareIntent.putExtra(Intent.EXTRA_TEXT, text)
            val intentChooser = Intent.createChooser(shareIntent, "Share via:")
            context.startActivity(intentChooser)
        }
    }


    /**
     * method to open email client with chooser
     */
    fun sendEmail(
        context: Context?,
        email: String,
        subject: String,
        body: String
    ) {
        if (context != null) {
            val uri = Uri.parse("mailto:$email?&subject=$subject&body=$body")
            val emailIntent = Intent(Intent.ACTION_SENDTO)
            emailIntent.data = uri
            val intentChooser = Intent.createChooser(emailIntent, "Send via:")
            context.startActivity(intentChooser)
        }
    }


    /**
     * method to open dialer
     */
    fun openDialer(
        context: Context?,
        number: String
    ) {
        if (context != null) {
            val share = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
            share.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            val intentChooser = Intent.createChooser(share, "Open via:")
            context.startActivity(intentChooser)
        }
    }


    /**
     * method to create & show Native AlertDialog
     */
    fun showAlertDialog(
        context: Context,
        title: String,
        message: String,
        positiveText: String,
        negativeText: String,
        positiveClickListener: () -> Unit,
        negativeClickListener: () -> Unit
    ): Dialog {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)

        builder.setPositiveButton(positiveText) { dialog, position ->
            dialog.dismiss()
            positiveClickListener()
        }

        builder.setNegativeButton(negativeText) { dialog, position ->
            dialog.dismiss()
            negativeClickListener()
        }

        val alertDialog = builder.create()
        alertDialog.setCancelable(false)

        alertDialog.window?.setBackgroundDrawable(null)
        alertDialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT
        )

        alertDialog.show()
        return alertDialog
    }


    /**
     * method to create & show Native AlertDialog with single action button
     */
    fun showSingleActionAlertDialog(
        context: Context,
        title: String,
        message: String,
        actionText: String,
        clickListener: () -> Unit
    ): Dialog {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)

        builder.setPositiveButton(actionText) { dialog, position ->
            dialog.dismiss()
            clickListener()
        }

        val alertDialog = builder.create()
        alertDialog.setCancelable(false)

        alertDialog.window?.setBackgroundDrawable(null)
        alertDialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT
        )

        alertDialog.show()
        return alertDialog
    }


    fun getOmitEmojiFilter(): Array<InputFilter> {
        val emojiFilter =
            InputFilter { source, start, end, dest, dstart, dend ->
                for (index in start until end) {
                    val type = Character.getType(source[index])
                    if (type == Character.SURROGATE.toInt() || type == Character.NON_SPACING_MARK.toInt() || type == Character.OTHER_SYMBOL.toInt()) {
                        return@InputFilter ""
                    }
                }
                return@InputFilter null
            }
        return arrayOf(emojiFilter)
    }


    fun addToPostParams(paramKey: String, paramValue: String?): String {
        if (paramValue != null)
            return paramKey + "" + (ApiEndPoints.PARAMETER_EQUALS) + "" + paramValue
        "" + (ApiEndPoints.PARAMETER_SEP)
        return ""

    }

    fun randInt(min: Int, max: Int): Int {
        // Usually this should be a field rather than a method variable so
        // that it is not re-seeded every call.
        val rand = Random()

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        return rand.nextInt(max - min + 1) + min
    }


    /**
     * method to check if required permissions are granted or not
     */
    fun checkPermissions(context: Context?, permissionArr: Array<String>): Boolean {
        if (context != null) {
            permissionArr.forEach { permission ->
                if (
                    ContextCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED
                )
                    return false
            }
            return true
        }
        return false
    }


    /**
     * method to check if permissions should be asked again or not
     */
    fun shouldAskPermissionsAgain(activity: Activity?, permissionArr: Array<out String>): Boolean {
        if (activity != null) {
            permissionArr.forEach { permission ->
                if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permission))
                    return false
            }
            return true
        }
        return false
    }


    /**
     * method to check if all permissions result are granted or not
     */
    fun hasPermissions(grantResults: IntArray): Boolean {
        grantResults.forEach { result ->
            if (result != PackageManager.PERMISSION_GRANTED)
                return false
        }
        return true
    }


    fun openAppSettings(context: Context?) {
        if (context != null) {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            val uri = Uri.fromParts("package", context.packageName, null)
            intent.data = uri
            context.startActivity(intent)
        }
    }


    /**
     * method to create/show custom Alert Dialog
     */
    fun appAlertDialog(
        context: Context,
        icon: Int = R.drawable.ic_alert,
        title: String,
        message: String,
        positiveText: String,
        negativeText: String,
        positiveClickListener: () -> Unit,
        negativeClickListener: (() -> Unit)?
    ): Dialog {
        val dialog = Dialog(context, R.style.AlertDialogTheme)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_alert)

        dialog.atvTitle.setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0)

        dialog.atvTitle.text = title
        dialog.atvMessage.text = message
        dialog.atvPositive.text = positiveText
        dialog.atvNegative.text = negativeText

        dialog.atvPositive.setOnClickListener {
            dialog.dismiss()
            positiveClickListener()
        }

        dialog.atvNegative.setOnClickListener {
            dialog.dismiss()
            negativeClickListener?.invoke()
        }

        when (context.resources.configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                dialog.window?.setLayout(
                    context.resources.getDimensionPixelSize(R.dimen.dimen_dialog_width_land),
                    WindowManager.LayoutParams.WRAP_CONTENT
                )
            }

            else -> {
                dialog.window?.setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT
                )
            }
        }

        dialog.show()
        return dialog
    }


}
