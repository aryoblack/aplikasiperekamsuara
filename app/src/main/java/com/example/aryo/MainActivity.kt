package com.example.aryo

import android.Manifest.permission
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.util.Log.d
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {
    var buttonStart: Button? = null;
    var buttonStop:android.widget.Button? = null;
    var buttonPlayLastRecordAudio:android.widget.Button? = null;
    var buttonStopPlayingRecording: Button? = null
    var AudioSavePathInDevice: String? = null
    var mediaRecorder: MediaRecorder? = null
    var random: Random? = null
    var RandomAudioFileName = "ABCDEFGHIJKLMNOP"
//    val RequestPermissionCode = 1
    private val RequestPermissionCode = 1
    private val RECORD_REQUEST_CODE = 101
    var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        buttonStart = findViewById(R.id.button) as Button
        buttonStop = findViewById(R.id.button2) as Button
        buttonPlayLastRecordAudio = findViewById(R.id.button3) as Button
        buttonStopPlayingRecording = findViewById(R.id.button4) as Button

        buttonStop!!.setEnabled(false)
        buttonPlayLastRecordAudio!!.setEnabled(false)
        buttonStopPlayingRecording!!.setEnabled(false)

        random = Random()


        buttonStart!!.setOnClickListener(View.OnClickListener {
            if (checkPermission()) {
//                val file = File(Environment.getExternalStorageDirectory().toString() + "/Perekam Suara/")
//                val folder = filesDir
                val f = File(Environment.getExternalStorageDirectory().toString() + "/Perekam Suara/")
                if (f.exists()){
                    d("folder", "exists")
                }else{
                    f.mkdir()
                }

                AudioSavePathInDevice = f.toString() + "/" +
                CreateRandomAudioFileName(5) + "AudioRecording.mp3"
                Log.d("asaa", AudioSavePathInDevice!!)
                MediaRecorderReady()
                try {
                    mediaRecorder?.prepare()
                    mediaRecorder?.start()
                } catch (e: IllegalStateException) {
                    // TODO Auto-generated catch block
                    e.printStackTrace()
                } catch (e: IOException) {
                    // TODO Auto-generated catch block
                    e.printStackTrace()
                }
                buttonStart!!.setEnabled(false)
                buttonStop!!.setEnabled(true)
                Toast.makeText(this@MainActivity, "Recording started",
                        Toast.LENGTH_LONG).show()
            } else {
                requestPermission()
            }
        })

        buttonStop!!.setOnClickListener(View.OnClickListener {
            mediaRecorder?.stop()
            buttonStop!!.setEnabled(false)
            buttonPlayLastRecordAudio!!.setEnabled(true)
            buttonStart!!.setEnabled(true)
            buttonStopPlayingRecording!!.setEnabled(false)
            Toast.makeText(this@MainActivity, "Recording Completed",
                    Toast.LENGTH_LONG).show()
        })

        buttonPlayLastRecordAudio!!.setOnClickListener(View.OnClickListener {
            buttonStop!!.setEnabled(false)
            buttonStart!!.setEnabled(false)
            buttonStopPlayingRecording!!.setEnabled(true)
            mediaPlayer = MediaPlayer()
            try {
                mediaPlayer!!.setDataSource(AudioSavePathInDevice)
                mediaPlayer!!.prepare()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            mediaPlayer!!.start()
            Toast.makeText(this@MainActivity, "Recording Playing",
                    Toast.LENGTH_LONG).show()
        })

        buttonStopPlayingRecording!!.setOnClickListener(View.OnClickListener {
            buttonStop!!.setEnabled(false)
            buttonStart!!.setEnabled(true)
            buttonStopPlayingRecording!!.setEnabled(false)
            buttonPlayLastRecordAudio!!.setEnabled(true)
            if (mediaPlayer != null) {
                mediaPlayer!!.stop()
                mediaPlayer!!.release()
                MediaRecorderReady()
            }
        })
    }

    fun MediaRecorderReady() {
        mediaRecorder = MediaRecorder()
        mediaRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        mediaRecorder!!.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB)
        mediaRecorder!!.setOutputFile(AudioSavePathInDevice)
    }

    fun CreateRandomAudioFileName(string: Int): String {
        val stringBuilder = StringBuilder(string)
        var i = 0
        while (i < string) {
            stringBuilder.append(random?.nextInt(RandomAudioFileName.length)?.let { RandomAudioFileName.get(it) })
            i++
        }
        return stringBuilder.toString()
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this@MainActivity, arrayOf(permission.WRITE_EXTERNAL_STORAGE, permission.RECORD_AUDIO), RequestPermissionCode)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            RequestPermissionCode -> if (grantResults.isNotEmpty()) {
                val StoragePermission = grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED
                val RecordPermission = grantResults[1] ==
                        PackageManager.PERMISSION_GRANTED
                if (StoragePermission && RecordPermission) {
                    Toast.makeText(this@MainActivity, "Permission Granted",
                            Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this@MainActivity, "Permission", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

  fun checkPermission(): Boolean {
        val result: Int = ContextCompat.checkSelfPermission(applicationContext,
                permission.WRITE_EXTERNAL_STORAGE)
        val result1: Int = ContextCompat.checkSelfPermission(applicationContext,
                permission.RECORD_AUDIO)
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED
    }
}
