package com.example.kotlinbasitmuzikcalar

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var mp:MediaPlayer
    private var totalTime:Int=0


    override fun onCreate(savedInstanceState: Bundle?) {
        izinİste()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        mp= MediaPlayer.create(this,R.raw.senikaybettigimde)


        mp.isLooping=true
        mp.setVolume(0.5f,0.5f)
        totalTime=mp.duration

        // volume Bar
        volumeBar.setOnSeekBarChangeListener(
            object :SeekBar.OnSeekBarChangeListener{
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser){
                        var volumeNum=progress / 100.0f
                        mp.setVolume(volumeNum,volumeNum)
                    }

                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {

                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {

                }

            }
        )

        // position Bar
        positionBar.max=totalTime
        positionBar.setOnSeekBarChangeListener(
            object :SeekBar.OnSeekBarChangeListener{
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser){
                        mp.seekTo(progress)
                    }

                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {

                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {

                }

            }
        )
        // Thread
        Thread(Runnable {
            while (mp != null){
                try {
                    var msg=Message()
                    msg.what=mp.currentPosition
                    handler.sendMessage(msg)
                    Thread.sleep(1000)
                }catch (e:InterruptedException){

                }

            }

        }).start()
    }

    @SuppressLint("HandlerLeak")
    var handler=object :Handler(){
        override fun handleMessage(msg: Message) {
            var currentPosition=msg.what

            // update positionbar
            positionBar.progress=currentPosition

            // update labels
            var elapsedTime=createTimeLabel(currentPosition)
            elapsedTimeLabel.text=elapsedTime

            var remainingTime=createTimeLabel(totalTime-currentPosition)
            remainingTimeLabel.text="-$remainingTime"
        }

    }

    fun createTimeLabel(time:Int):String{
        var timeLabel=""
        var min=time / 1000 / 60
        var sec=time /1000 % 60

        timeLabel="$min:"
        if (sec<10) timeLabel+=0
        timeLabel+=sec

        return timeLabel
    }

    fun playBtnClick(v: View){
        if (mp.isPlaying){
            // durdurma
            mp.pause()
            playBtn.setBackgroundResource(R.drawable.play)
        }else{
            // başlatma
            mp.start()
            playBtn.setBackgroundResource(R.drawable.stop)
        }
    }

    fun showSongList(v:View){
        val intent=Intent(this,SongList::class.java)
        startActivity(intent)

    }

    private fun izinİste(){
        if (ActivityCompat.checkSelfPermission(this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),1)
        }else{

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        if (requestCode==1){
            if (grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Erişim İzni Verildi",Toast.LENGTH_LONG).show()
            }else{
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),1)
            }
        }


        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}