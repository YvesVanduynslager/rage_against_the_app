package be.equality.dualpane

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.design.widget.Snackbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import be.equality.dualpane.domain.Comic


import kotlinx.android.synthetic.main.activity_ragecomic_list.*
import kotlinx.android.synthetic.main.ragecomic_list_content.view.*
import kotlinx.android.synthetic.main.ragecomic_list.*

/**
 * An activity representing a list of Comics. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a [RagecomicDetailActivity] representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
class RagecomicListActivity : AppCompatActivity() {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private var twoPane: Boolean = false


    /**
     * Create the Activity and the Recyclerview
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ragecomic_list)

        setSupportActionBar(toolbar)
        toolbar.title = title

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        if (ragecomic_detail_container != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            twoPane = true
        }

        setupRecyclerView(ragecomic_list)
    }


    /**
     * Creates the data source for the Recyclerview.
     */
    private fun setupRecyclerView(recyclerView: RecyclerView) {
        // Get rage face names and descriptions.
        val resources = applicationContext.resources
        val names = resources.getStringArray(R.array.names)
        val descriptions = resources.getStringArray(R.array.descriptions)
        val urls = resources.getStringArray(R.array.urls)

        // Get rage face images.
        val typedArray = resources.obtainTypedArray(R.array.images)
        val imageCount = names.size
        val imageResIds = IntArray(imageCount)
        for (i in 0..imageCount - 1) {
            imageResIds[i] = typedArray.getResourceId(i, 0)
        }
        typedArray.recycle()

        val list = mutableListOf<Comic>()
        for (i in 0..imageCount - 1) {
            val theComic = Comic(imageResIds[i],names[i],descriptions[i],urls[i],names[i])
            list.add(theComic)
        }

        recyclerView.adapter = SimpleItemRecyclerViewAdapter(this, list,imageResIds, twoPane)
    }


    /***********************************************************************************************
     * Recyclerview
     *
     ***********************************************************************************************
     */
    class SimpleItemRecyclerViewAdapter(private val parentActivity: RagecomicListActivity,
                                        private val values: MutableList<Comic>,
                                        private val images : IntArray,
                                        private val twoPane: Boolean) :
            RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder>() {

        private val onClickListener: View.OnClickListener

        init {
            onClickListener = View.OnClickListener { v ->
                val item = v.tag as Comic
                if (twoPane) {
                    val fragment = RagecomicDetailFragment().apply {
                        arguments = Bundle().apply {
                            putSerializable(RagecomicDetailFragment.ARG_COMIC,item)

                        }
                    }
                    parentActivity.supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.ragecomic_detail_container, fragment)
                            .commit()
                } else {
                    val intent = Intent(v.context, RagecomicDetailActivity::class.java).apply {
                        putExtra(RagecomicDetailFragment.ARG_COMIC, item)

                    }
                    v.context.startActivity(intent)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.ragecomic_list_content, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = values[position]
            holder.name.text = item.name
            holder.image.setImageResource(item.imageResId)

            with(holder.itemView) {
                tag = item
                setOnClickListener(onClickListener)
            }
        }

        override fun getItemCount() = values.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val name : TextView = view.listname
            val image :ImageView = view.list_comic_image


        }
    }
}
