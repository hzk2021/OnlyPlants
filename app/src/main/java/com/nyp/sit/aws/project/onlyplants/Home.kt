package com.nyp.sit.aws.project.onlyplants

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.view.LayoutInflater;
import android.view.MenuItem
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import com.nyp.sit.aws.project.onlyplants.Model.Social.Post
import com.nyp.sit.aws.project.onlyplants.Model.Social.SocialMediaService
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_post.*
import kotlinx.android.synthetic.main.activity_post.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException

class Home : AppCompatActivity() {
    private lateinit var addbutton: ImageView
    var test=""

    private fun setupRecyclerView(posts: List<Post>) {
        val adapter = PostAdapter(posts)
        adapter.notifyDataSetChanged()
        recycler_view_story.layoutManager = LinearLayoutManager(this)
        recycler_view_story.adapter = adapter
    }
    private fun parseJson(jsonString: String): List<Post> {
        val jsonArray = JSONArray(jsonString)
        val posts = mutableListOf<Post>()
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val postID = jsonObject.getInt("PostID")
            val caption = jsonObject.getString("Caption")
            val imageURL = jsonObject.getString("ImageURl")
            val post = Post(postID, caption, imageURL)
            posts.add(post)
        }
        return posts
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_home)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        addbutton = findViewById<ImageView>(R.id.addbutton)
        addbutton.setOnClickListener {
            val intent = Intent(this, AddPost::class.java)
            startActivity(intent)
        }
        GlobalScope.launch(Dispatchers.IO) {
            test = SocialMediaService().GetAllPost()
            val posts = parseJson(test)
            runOnUiThread {
                setupRecyclerView(posts)
            }
        }
//        bottom_navigation.setOnNavigationItemSelectedListener { item ->
//            when (item.itemId) {
//                R.id.menu_item_1 -> {
//                    // handle menu item 1 click
//                    true
//                }
//                R.id.menu_item_2 -> {
//                    // handle menu item 2 click
//                    true
//                }
//                R.id.menu_item_3 -> {
//                    val intent = Intent(this, MonitorPlants::class.java)
//                    startActivity(intent)
//                    true
//                }
//                else -> false
//            }
//        }

    }
    class PostAdapter(private var posts: List<Post>) : RecyclerView.Adapter<PostAdapter.ViewHolder>() {

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(post: Post) {
                itemView.CaptionText.text = post.caption
                Picasso.get().load(post.imageUrl).into(itemView.post_image_home)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_post, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(posts[position])
        }

        override fun getItemCount(): Int {
            return posts.size
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }
}