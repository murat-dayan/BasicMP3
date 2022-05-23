package com.example.kotlinbasitmuzikcalar

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.MenuItem
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import com.example.kotlinbasitmuzikcalar.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mp:MediaPlayer
    private var totalTime:Int=0
    private lateinit var toggle: ActionBarDrawerToggle



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        initalize()

        toggle=ActionBarDrawerToggle(this,binding.root,R.string.open,R.string.close)
        binding.root.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mp= MediaPlayer.create(this,R.raw.senikaybettigimde)


        mp.isLooping=true
        mp.setVolume(0.5f,0.5f)
        totalTime=mp.duration

        // volume Bar
        binding.volumeBar.setOnSeekBarChangeListener(
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
        binding.positionBar.max=totalTime
        binding.positionBar.setOnSeekBarChangeListener(
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

        binding.songLists.setOnClickListener {
            startActivity(Intent(this@MainActivity,Songs::class.java))
        }


    }

    @SuppressLint("HandlerLeak")
    var handler=object :Handler(){
        override fun handleMessage(msg: Message) {
            var currentPosition=msg.what

            // update positionbar
            binding.positionBar.progress=currentPosition

            // update labels
            var elapsedTime=createTimeLabel(currentPosition)
            binding.elapsedTimeLabel.text=elapsedTime

            var remainingTime=createTimeLabel(totalTime-currentPosition)
            binding.remainingTimeLabel.text="-$remainingTime"
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
            binding.playBtn.setBackgroundResource(R.drawable.play)
        }else{
            // başlatma
            mp.start()
            binding.playBtn.setBackgroundResource(R.drawable.stop)
        }
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

    private fun initalize(){
        izinİste()
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (toggle.onOptionsItemSelected(item)){
            return true
        }

        return super.onOptionsItemSelected(item)
    }


}