package com.nyp.sit.aws.project.onlyplants

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView;
import com.nyp.sit.aws.project.onlyplants.Model.Plant.PlantService
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
    private lateinit var emptyposttext: TextView
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
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.addbuttonmenu ,menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_custom -> {
                val intent = Intent(this, AddPost::class.java)
                startActivity(intent)
                return true
            }
            android.R.id.home -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            R.id.translateBtn -> {
                val dialog = Dialog(this)
                dialog.setContentView(R.layout.popup_translate)
                val spinner1 = dialog.findViewById<Spinner>(R.id.pu_FromLang)
                val spinner2 = dialog.findViewById<Spinner>(R.id.pu_ToLang)
                val button = dialog.findViewById<Button>(R.id.pu_Button_Translate)

                val spinnerFromLang = dialog.findViewById<Spinner>(R.id.pu_FromLang)
                val itemsFromLang = arrayOf("en", "fr", "zh")
                val fromAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, itemsFromLang)

                spinner1.adapter = fromAdapter
                spinner2.adapter = fromAdapter

                dialog.show()

                button.setOnClickListener {

                    GlobalScope.launch {
                        dialog.dismiss()

                        val fromLang = spinner1.selectedItem.toString()
                        val toLang = spinner2.selectedItem.toString()

                        val rootView = findViewById<ViewGroup>(android.R.id.content)

                        PlantService().translateViews(rootView, fromLang, toLang)
                    }

                }
            }
        }
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_home)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


//        addbutton = findViewById<ImageView>(R.id.addbutton)
        emptyposttext=findViewById<TextView>(R.id.postnulltext)

//        addbutton.setOnClickListener {
//            val intent = Intent(this, AddPost::class.java)
//            startActivity(intent)
//        }
        GlobalScope.launch(Dispatchers.IO) {
            test = SocialMediaService().GetAllPost()
            val posts = parseJson(test)
            if (posts.isEmpty()){
                emptyposttext.visibility=View.VISIBLE
            }
            else {
                runOnUiThread {
                    emptyposttext.visibility=View.GONE
                    setupRecyclerView(posts)
                }
            }
        }

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
}