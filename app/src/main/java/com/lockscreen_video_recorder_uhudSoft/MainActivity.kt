package com.lockscreen_video_recorder_uhudSoft

import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import com.lockscreen_video_recorder_uhudSoft.databinding.ActivityMainBinding
import com.simform.custombottomnavigation.Model


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    companion object {
        private const val ID_HOME = 0
        private const val ID_EXPLORE = 1
        private const val ID_MESSAGE = 2
        private const val ID_NOTIFICATION = 3
        private const val ID_ACCOUNT = 4
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        supportActionBar!!.hide()

        //setBottomNavigationInNormalWay(savedInstanceState)
        setBottomNavigationWithNavController(savedInstanceState)
    }

    private fun setBottomNavigationInNormalWay(savedInstanceState: Bundle?) {
        val tvSelected = binding.tvSelected
        //tvSelected.typeface = Typeface.createFromAsset(assets, "fonts/SourceSansPro-Regular.ttf")

        val activeIndex = savedInstanceState?.getInt("activeIndex") ?: 2

        binding.bottomNavigation.apply {

            // If you don't pass activeIndex then by pass 0 here or call setSelectedIndex function only
            // setSelectedIndex()        // It will take 0 by default
            setSelectedIndex(activeIndex)

            add(
                Model(
                    icon = R.drawable.ic_home,
                    id = ID_HOME,
                    text = R.string.title_home,
                )
            )
            add(
                Model(
                    icon = R.drawable.ic_favorite_border_black,
                    id = ID_EXPLORE,
                    text = R.string.title_video,
                    count = R.string.empty_value
                )
            )
            add(
                Model(
                    R.drawable.ic_message,
                    id = ID_MESSAGE,
                    text = R.string.title_alarm,
                    count = R.string.empty_value
                )
            )
            add(
                Model(
                    R.drawable.ic_notification,
                    id = ID_NOTIFICATION,
                    text = R.string.title_notifications,
                    count = R.string.count
                )
            )
            add(
                Model(
                    R.drawable.ic_account,
                    id = ID_ACCOUNT,
                    text = R.string.title_setting,
                    count = R.string.empty_value
                )
            )

            // If you want to change count
            setCount(ID_NOTIFICATION, R.string.count_update)

            setOnShowListener {
                val name = when (it.id) {
                    ID_HOME -> "Home"
                    ID_EXPLORE -> "Explore"
                    ID_MESSAGE -> "Message"
                    ID_NOTIFICATION -> "Notification"
                    ID_ACCOUNT -> "Account"
                    else -> ""
                }

                val bgColor = when (it.id) {
                    ID_HOME -> ContextCompat.getColor(this@MainActivity, R.color.color_home_bg)
                    ID_EXPLORE -> ContextCompat.getColor(
                        this@MainActivity,
                        R.color.color_favorite_bg
                    )
                    ID_MESSAGE -> ContextCompat.getColor(this@MainActivity, R.color.color_chat_bg)
                    ID_NOTIFICATION -> ContextCompat.getColor(
                        this@MainActivity,
                        R.color.color_notification_bg
                    )
                    ID_ACCOUNT -> ContextCompat.getColor(
                        this@MainActivity,
                        R.color.color_profile_bg
                    )
                    else -> ContextCompat.getColor(this@MainActivity, R.color.colorPrimary)
                }

                tvSelected.text = getString(R.string.main_page_selected, name)
                binding.lnrLayout.setBackgroundColor(bgColor)
            }

            setOnClickMenuListener {
                val name = when (it.id) {
                    ID_HOME -> "HOME"
                    ID_EXPLORE -> "EXPLORE"
                    ID_MESSAGE -> "MESSAGE"
                    ID_NOTIFICATION -> "NOTIFICATION"
                    ID_ACCOUNT -> "ACCOUNT"
                    else -> ""
                }
            }

            setOnReselectListener {
                Toast.makeText(context, "item ${it.id} is reselected.", Toast.LENGTH_LONG).show()
            }

        }
    }

    private fun setBottomNavigationWithNavController(savedInstanceState: Bundle?) {

        // If you don't pass activeIndex then by default it will take 0 position
        val activeIndex = savedInstanceState?.getInt("activeIndex") ?: 1

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
               // R.id.navigation_alarm,
                R.id.navigation_video_quality,
                R.id.navigation_home,
              //  R.id.navigation_notifications,
                R.id.navigation_settings
            )
        )
      //  setupActionBarWithNavController(navController, appBarConfiguration)
        val menuItems = arrayOf(
            /*Model(
                R.drawable.ic_stopwatch,
                R.id.navigation_alarm,
                0,
                R.string.title_alarm,
                R.string.empty_value
            ),*/

            Model(
                R.drawable.ic_baseline_high_quality_24,
                R.id.navigation_video_quality,
                id = 0,
                R.string.title_videoQuality,
                R.string.empty_value
            ),
            Model(
                icon = R.drawable.ic_home,
                destinationId = R.id.navigation_home,
                id = 1,
                text = R.string.title_home,
                count = R.string.empty_value
            ),

            /*Model(
                R.drawable.ic_notification,
                R.id.navigation_notifications,
                2,
                R.string.title_notifications,
                R.string.count
            ),*/
            Model(
                R.drawable.ic_setting,
                R.id.navigation_settings,
                2,
                R.string.title_setting,
                R.string.empty_value
            )
        )

        binding.bottomNavigation.apply {
            // If you don't pass activeIndex then by default it will take 0 position
            setMenuItems(menuItems, activeIndex)
            setupWithNavController(navController)

            // manually set the active item, so from which you can control which position item should be active when it is initialized.
            // onMenuItemClick(4)

            // If you want to change notification count
            //setCount(ID_NOTIFICATION, R.string.count_update)
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("activeIndex", binding.bottomNavigation.getSelectedIndex())
        super.onSaveInstanceState(outState)
    }

    override fun onBackPressed() {

       // showResetPasswordDialog(activity: Activity?)

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.bp_custom_dialogue_box)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val dialogBtn_exit = dialog.findViewById<Button>(R.id.btn_yes)
        val dialogBtn_goPlayStoreB = dialog.findViewById<LinearLayout>(R.id.id_Ad_linearLayout)
        val dialogBtn_goPlayStoreH = dialog.findViewById<LinearLayout>(R.id.id_llHiddenDeviceFinder)
        val dialogBtn_no = dialog.findViewById<Button>(R.id.btn_no)
        dialogBtn_exit.setOnClickListener {
            dialog.dismiss()
            this.finish()
        }

        dialogBtn_goPlayStoreB.setOnClickListener {

            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.uhudsofttech.BluetoothAutoConnect")))
            } catch (e: ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id==com.uhudsofttech.BluetoothAutoConnect")))
            }

        }

        dialogBtn_goPlayStoreH.setOnClickListener {

            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.humanbodysystems.organs")))
            } catch (e: ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.humanbodysystems.organs")))
            }

        }

        dialogBtn_no.setOnClickListener {

            dialog.dismiss()

        }
        dialog.show()

    }

}