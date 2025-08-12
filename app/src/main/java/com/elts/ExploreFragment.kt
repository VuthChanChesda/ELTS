package com.elts

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.elts.models.Article
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ExploreFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ExploreFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var recyclerView: RecyclerView
    private lateinit var articleList: MutableList<Article>
    private lateinit var articleAdapter: ArticleAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_explore, container, false)
        // RecyclerView setup
        recyclerView = view.findViewById(R.id.recyclerArticles) // Make sure your XML has this ID
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        articleList = mutableListOf()
        articleAdapter = ArticleAdapter(articleList) { article ->
            // Launch detail screen when item is clicked
            val intent = Intent(requireContext(), ArticleDetailActivity::class.java)
            intent.putExtra("title", article.title)
            intent.putExtra("content", article.content)
            intent.putExtra("date", article.date)
            intent.putExtra("description", article.description)
            startActivity(intent)
        }

        recyclerView.adapter = articleAdapter

        // Load from Firebase
        val db = FirebaseFirestore.getInstance()
        db.collection("articles")
            .get()
            .addOnSuccessListener { snapshot ->
                articleList.clear()
                for (document in snapshot) {
                    val article = document.toObject(Article::class.java)
                    articleList.add(article)
                }
                articleAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error loading articles", exception)
            }

        return view
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ExploreFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ExploreFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}