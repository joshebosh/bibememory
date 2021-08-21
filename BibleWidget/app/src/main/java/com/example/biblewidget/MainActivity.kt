package com.example.biblewidget

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.widget.addTextChangedListener
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.example.biblewidget.ext.gone
import com.example.biblewidget.ext.show
import com.example.biblewidget.helper.SpUtils
import com.example.biblewidget.receiver.MyBroadcastReceiver
import com.obsez.android.lib.filechooser.ChooserDialog
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.input_dialogs.view.*
import java.io.File


class MainActivity : AppCompatActivity() {

    var isUrlDetected = false;
    var url: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        progressBar2.gone()

        val fileName = SpUtils.getFileName(this)
        if (fileName.isNotEmpty()) {
            val txt = "Selected file: $fileName"
            textView.text = txt
            val btnTxt = "Update file"
            chooseFile.text = btnTxt
        } else {
            textView.gone()
        }

        url_et.addTextChangedListener {
            if (it?.toString()?.isNotEmpty() == true) {
                isUrlDetected = true
                chooseFile.text = "Download"
            } else {
                isUrlDetected = false
                chooseFile.text = "Select file"
            }
        }

        chooseFile.setOnClickListener {
            if (isUrlDetected) {
                //download
                url = url_et.text.toString()
                checkPermission(url)

            } else {
                showDialogs(this)
                progressBar2.show()
            }
        }

        bindsRotationSwitch()
        bindsOrderSwitch()

        fab.setOnClickListener { _ ->
            showTimeInputDialogs()
        }

        registerReceiver()
    }

    private fun checkPermission(url: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                val requiredPermissions =
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

                ActivityCompat.requestPermissions(this, requiredPermissions, 121)
            } else {
                downloadFile(url)
            }
        } else {
            downloadFile(url)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 121 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            downloadFile(url)
        } else {
            Toast.makeText(this, "Please provide access", Toast.LENGTH_LONG).show()
        }

    }

    //http://joshua.tel/kjv.txt
    private fun downloadFile(url: String) {
        val li = url.trim().split('/')
        val name = li[li.size - 1]

        PRDownloader.initialize(applicationContext)
        val downloadId = PRDownloader.download(url.trim(), filesDir.absolutePath, name)
            .build()
            .setOnStartOrResumeListener { }
            .setOnPauseListener { }
            .setOnCancelListener() {}
            .setOnProgressListener {
                progressBar2.show()
            }
            .start(object : OnDownloadListener {
                override fun onDownloadComplete() {
                    progressBar2.gone()
                    textView.show()
                    val path = filesDir.absolutePath +"/"+name
                    val txt = "Selected file: $path"
                    textView.text = txt

                    statusTV.show()
                    statusTV.text = "Processing file, wait a while"
                    statusTV.setTextColor(Color.RED)
                    SpUtils.setFileName(this@MainActivity, path)

                    processFile(path)
                }

                override fun onError(error: com.downloader.Error?) {
                    Toast.makeText(
                        this@MainActivity,
                        error?.connectionException?.message ?: "Something went wrong",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun bindsRotationSwitch() {
        val status = SpUtils.getRotationStatus(this)
        stop_rotation.isChecked = status

        val rotTime = SpUtils.getRotationTime(this)

        stop_rotation.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                //cancel rotation
                MyBroadcastReceiver.cancelAlarm(this)
            } else {
                if (rotTime > 0) {
                    MyBroadcastReceiver.setAlarm(this, rotTime)
                }

            }
            SpUtils.setRotationStatus(this, isChecked)
        }

        if (rotTime <= 0) {
            stop_rotation.gone()
        } else {
            stop_rotation.show()
        }

    }

    private fun bindsOrderSwitch() {
        val offText = "Quotes will be selected sequentially"
        val onText = "Quotes will be selected randomly"

        val orderStatus = SpUtils.getOrderStatus(this)
        select_order.isChecked = orderStatus

        if (orderStatus) {
            select_order_hit.text = onText
        } else {
            select_order_hit.text = offText
        }

        select_order.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                select_order_hit.text = onText
            } else {
                select_order_hit.text = offText
            }
            SpUtils.setOrderStatus(this, isChecked)
        }

    }

    private fun showTimeInputDialogs() {
        val builder = AlertDialog.Builder(this)
        val view = LayoutInflater.from(this).inflate(R.layout.input_dialogs, null, false)
        builder.setView(view)

        val dialog = builder.create();

        view.button.setOnClickListener {
            val text = view.editText.text?.toString()
            if (text.isNullOrEmpty()) {
                Toast.makeText(this, "Please input a valid number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val number = text.trim().toInt()
            SpUtils.setRotationTime(this, number)
            Log.i(MyBroadcastReceiver.TAG, "Total rotation number: $number")

            if (number != 0) {
                MyBroadcastReceiver.setAlarm(this, number)
                stop_rotation.show()
                stop_rotation.isChecked = false
                SpUtils.setRotationStatus(this, false)
//                MyBroadcastReceiver.setWork(this, number)
            } else {
                MyBroadcastReceiver.cancelAlarm(this)
                stop_rotation.gone()
                stop_rotation.isChecked = true
                SpUtils.setRotationStatus(this, true)
//                MyBroadcastReceiver.cancelWork(this)
            }
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun registerReceiver() {
        val receiver = ComponentName(this, MyBroadcastReceiver::class.java)

        this.packageManager.setComponentEnabledSetting(
            receiver,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
    }

    private fun showDialogs(context: Context) {
        ChooserDialog(context)
            .withFilterRegex(false, true, ".*\\.(txt)")
            .withResources(
                R.string.title_choose_file,
                R.string.title_choose,
                R.string.dialog_cancel
            )
            .withChosenListener { path, _ ->
                //save to sp
                statusTV.show()
                statusTV.text = "Processing file, wait a while"
                statusTV.setTextColor(Color.RED)
                SpUtils.setFileName(this, path)
                textView.show()
                val txt = "Selected file: $path"
                textView.text = txt
                val btnTxt = "Update file"
                chooseFile.text = btnTxt
                processFile(path)
            }
            .withNavigateUpTo { true }
            .withNavigateTo { true }
            .build()
            .show()
    }

    private fun resetAllData() {
        SpUtils.setCurrentID(this, 0)
        SpUtils.setCurrentTime(this, 0)
    }

    private fun processFile(path: String) {
        //reset previous data
        resetAllData()

        val file = File(path)
        val list = file.readLines()
        val data = ArrayList<String>()
        //filter data
        for (i in list) {
            if (i.startsWith("*")) {
                data.add(i)
            }
        }
        //save total count
        SpUtils.saveTotalData(this, data.size)

        Log.i(MyBroadcastReceiver.TAG, "Total found${data.size}")

        //now save data
        for ((j, str) in data.withIndex()) {
            SpUtils.saveDataLine(this, j, str)
        }

        progressBar2.gone()
        statusTV.text = "File processing complete"
        statusTV.setTextColor(Color.GREEN)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}

//todo
//if widget already enabled and current id -1
// then show update

//when enabled widget then only set alarm
