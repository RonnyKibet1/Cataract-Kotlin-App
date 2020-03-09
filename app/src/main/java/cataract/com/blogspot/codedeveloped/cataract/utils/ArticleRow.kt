package cataract.com.blogspot.codedeveloped.cataract.utils

import cataract.com.blogspot.codedeveloped.cataract.R
import cataract.com.blogspot.codedeveloped.cataract.Article
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.article_row.view.*

class ArticleRow(val article: Article): Item<ViewHolder>(){

    override fun getLayout(): Int {
        return R.layout.article_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {

        viewHolder.itemView.articleTitleRowTextView.text = article.title
        viewHolder.itemView.articleContentRowTextView.text = article.content
    }

}