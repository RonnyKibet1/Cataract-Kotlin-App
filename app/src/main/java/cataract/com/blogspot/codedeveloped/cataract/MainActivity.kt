package cataract.com.blogspot.codedeveloped.cataract


import cataract.com.blogspot.codedeveloped.cataract.utils.ArticleRow
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.text.Html
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import org.json.JSONArray
import android.os.Environment.getExternalStorageDirectory
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.lang.StringBuilder



class MainActivity : AppCompatActivity() {

    lateinit var articleRecyclerView: RecyclerView
    val adapter = GroupAdapter<ViewHolder>()

    companion object {
        val ARTICLE_TITLE = "ARTICLE_TITLE"
        val ARTICLE_CONTENT = "ARTICLE_CONTENT"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //assign vars
        articleRecyclerView = findViewById(R.id.articleRecyclerView)
        articleRecyclerView.adapter = adapter

        adapter.setOnItemClickListener { item, view ->
            val articleRow = item as ArticleRow
            val article = articleRow.article
            val title = article.title
            val content = article.content

            //go to article detail
            val goToDetailIntent = Intent(this, ArticleDetailActivity::class.java)
            goToDetailIntent.putExtra(ARTICLE_TITLE, title)
            //goToDetailIntent.putExtra(ARTICLE_CONTENT, content)
            startActivity(goToDetailIntent)

        }

        getArticles()
    }

    private fun getArticles(){
//        val inputStream = assets.open("articles.json")
//        val size = inputStream.available()
//        val buffer = ByteArray(size)
//        inputStream.read(buffer, 0, buffer.size)
//        var articlesJsonString = String(buffer, Charsets.UTF_8)
//        val jsonArray = JSONArray(articlesJsonString)
//
//        for (i in 0..(jsonArray.length() - 1)) {
//            val article = jsonArray.getJSONObject(i)
//
//            var title = article.getString("title")
//            var content = article.getString("content")
//
//            adapter.add(ArticleRow(Article("\t"+title, Html.fromHtml(content).toString(), false)))
//        }

        val assetDir = assets.list("Content")
        for(c in 0..(assetDir!!.size - 1)){
            //val file = File(assets.open("Content/"+ assetDir[c]).toString())
            var articleTitle = assetDir[c]
            val inputStream = assets.open("Content/"+ articleTitle)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer, 0, buffer.size)
            var article = String(buffer, Charsets.UTF_8)

            articleTitle =  articleTitle.replace(".txt", "")
            adapter.add(ArticleRow(Article(articleTitle, Html.fromHtml(article), false)))
        }


    }

    // Initializing menu options
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_activity_main, menu)
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
}
