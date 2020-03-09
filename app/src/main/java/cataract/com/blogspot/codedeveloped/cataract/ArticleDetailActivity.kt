package cataract.com.blogspot.codedeveloped.cataract

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.support.v7.app.AppCompatActivity
import android.text.Html
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.ads.*
import java.util.*

class ArticleDetailActivity : AppCompatActivity(), TextToSpeech.OnInitListener {


    lateinit var articleTitleDetailTextView: TextView
    lateinit var articleContentDetailTextView: TextView
    lateinit var imageButtonRead: ImageButton

    var title = ""
    var content = ""

    var tts : TextToSpeech? = null
    lateinit var mAdView : AdView
    private lateinit var mInterstitialAd: InterstitialAd

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article_detail)

        // Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
        MobileAds.initialize(this, getString(R.string.ADMOB_APP_ID_BANNER))

        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = getString(R.string.AD_UNIT_ID_INTERSTITIAL)
        mInterstitialAd.loadAd(AdRequest.Builder().build())
        mInterstitialAd.adListener = object : AdListener() {
            override fun onAdClosed() {
                mInterstitialAd.loadAd(AdRequest.Builder().build())
            }
        }

        //assign components
        articleTitleDetailTextView = findViewById(R.id.articleTitleDetailTextView)
        articleContentDetailTextView = findViewById(R.id.articleContentDetailTextView)
        imageButtonRead = findViewById(R.id.imageButtonRead)
        imageButtonRead.visibility = View.INVISIBLE
        //get data from intent
        title = intent.getStringExtra(MainActivity.ARTICLE_TITLE)
        //content = intent.getStringExtra(MainActivity.ARTICLE_CONTENT)

        if(title == ""){
            //empty article
        }else{
            //intent with content
            var articleTitle = title
            val inputStream = assets.open("Content/"+ articleTitle + ".txt")
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer, 0, buffer.size)
            var articleContent = String(buffer, Charsets.UTF_8)
            articleTitle =  articleTitle.replace(".txt", "") //remove the .txt after
            content = Html.fromHtml(articleContent).toString()
            articleTitleDetailTextView.setText(articleTitle)
            articleContentDetailTextView.setText(Html.fromHtml(articleContent))
        }

        //text to speech init
        tts = TextToSpeech(this, this)

        imageButtonRead.setOnClickListener {
            //read for user
            if(tts != null){
                if(tts!!.isSpeaking){
                    tts!!.stop()
                    imageButtonRead.setImageResource(R.mipmap.ic_play)
                    //stop then show ad during this break
                    if (mInterstitialAd.isLoaded) {
                        mInterstitialAd.show()
                    } else {
                        Log.d("TAG", "The interstitial wasn't loaded yet.")
                    }
                }else{
                    speakOut(title, content)
                    imageButtonRead.setImageResource(R.mipmap.ic_stop)
                }
            }else{
                tts = TextToSpeech(this, this)
                imageButtonRead.setImageResource(R.mipmap.ic_play)
            }


        }

    }

    override fun onInit(status: Int) {

        if (status == TextToSpeech.SUCCESS) {
            // set US English as language for tts
            val result = tts!!.setLanguage(Locale.US)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS","The Language specified is not supported!")
                Toast.makeText(this, "The Language specified is not supported!", Toast.LENGTH_LONG).show()
            } else {
                imageButtonRead.visibility = View.VISIBLE
            }


        } else {
            Log.e("TTS", "Initilization Failed!")
        }
    }

    private fun speakOut(title: String, content: String) {
        tts!!.speak("Topic.. " + title + ". \n " + content, TextToSpeech.QUEUE_FLUSH, null,"")
    }

    override fun onDestroy() {
        shutDownTts()
        super.onDestroy()
    }

    override fun onPause() {
        shutDownTts()
        super.onPause()
    }

    private fun shutDownTts(){
        // Shutdown TTS
        if (tts != null) {
            tts!!.stop()
            imageButtonRead.setImageResource(R.mipmap.ic_play)
            //tts!!.shutdown()
        }
    }

    // Initializing menu options
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.article_detail_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item!!.getItemId()

        //noinspection SimplifiableIfStatement
        if (id == R.id.share) {
            share()
            return true
        } else if (id == R.id.rate) {
            // rate intent
            val appLink = "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID
            openNewTabWindow(appLink , this)
            return true
        } else if (id == R.id.home) {
            // opining browser intent
            shutDownTts()
            finish()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    fun share(){
       //share
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type="text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this "+ getString(R.string.app_name) + " app. => \n https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)
        startActivity(Intent.createChooser(shareIntent, getString(R.string.send_to)))
    }

    fun openNewTabWindow(urls: String, context : Context) {
        val uris = Uri.parse(urls)
        val intents = Intent(Intent.ACTION_VIEW, uris)
        val b = Bundle()
        b.putBoolean("new_window", true)
        intents.putExtras(b)
        context.startActivity(intents)
    }


}
