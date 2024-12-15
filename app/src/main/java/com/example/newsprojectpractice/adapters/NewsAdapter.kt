package com.example.newsprojectpractice.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsprojectpractice.R
import com.example.newsprojectpractice.models.Article

// Adapter untuk RecyclerView yang menampilkan daftar artikel berita
class NewsAdapter: RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {

    // ViewHolder untuk setiap item dalam RecyclerView
    inner class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    // Deklarasi variabel untuk komponen-komponen UI pada item berita
    lateinit var articleImage: ImageView
    lateinit var articleSource: TextView
    lateinit var articleTitle: TextView
    lateinit var articleDescription: TextView
    lateinit var articleDateTime: TextView

    // DiffUtil callback untuk membandingkan item lama dan baru
    private val differCallback = object : DiffUtil.ItemCallback<Article>() {
        // Menentukan apakah dua artikel sama berdasarkan URL
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        // Menentukan apakah isi dua artikel sama berdasarkan URL
        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }
    }

    // AsyncListDiffer digunakan untuk memperbarui daftar secara efisien
    var differ = AsyncListDiffer(this, differCallback)

    // Membuat ViewHolder dan meng-inflate layout item_news.xml
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return ArticleViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
        )
    }

    // Mengembalikan jumlah item dalam daftar
    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    // Menyimpan listener untuk menangani klik pada item berita
    private var onItemClickListener: ((Article) -> Unit)? = null

    // Mengikat data artikel ke dalam tampilan
    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = differ.currentList[position]

        // Menyambungkan komponen UI dengan elemen di layout
        articleImage = holder.itemView.findViewById(R.id.articleImage)
        articleSource = holder.itemView.findViewById(R.id.articleSource)
        articleTitle = holder.itemView.findViewById(R.id.articleTitle)
        articleDescription = holder.itemView.findViewById(R.id.articleDescription)
        articleDateTime = holder.itemView.findViewById(R.id.articleDateTime)

        // Mengisi data ke dalam tampilan artikel
        holder.itemView.apply {
            Glide.with(this).load(article.urlToImage).into(articleImage) // Memuat gambar menggunakan Glide
            articleSource.text = article.source?.name // Menampilkan sumber artikel
            articleTitle.text = article.title // Menampilkan judul artikel
            articleDescription.text = article.description // Menampilkan deskripsi artikel
            articleDateTime.text = article.publishedAt // Menampilkan waktu publikasi artikel

            // Menangani klik item
            setOnClickListener {
                onItemClickListener?.let {
                    it(article) // Memanggil listener saat item diklik
                }
            }
        }
    }

    // Fungsi untuk menetapkan listener klik pada item berita
    fun setOnItemClickListener(listener: (Article) -> Unit){
        onItemClickListener = listener
    }
}
