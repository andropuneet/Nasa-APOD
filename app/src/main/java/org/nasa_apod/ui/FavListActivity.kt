package org.nasa_apod.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.android.AndroidInjection
import org.nasa_apod.AppExecutors
import org.nasa_apod.R
import org.nasa_apod.binding.ActivityDataBindingComponent
import org.nasa_apod.databinding.ActivityFavListBinding
import org.nasa_apod.viewmodel.FavListViewModel
import timber.log.Timber
import javax.inject.Inject

class FavListActivity : AppCompatActivity() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var appExecutors: AppExecutors
    lateinit var favListViewModel: FavListViewModel
    lateinit var binding: ActivityFavListBinding
    lateinit var favAdapter: FavAdapter
    private val dataBindingComponent: DataBindingComponent = ActivityDataBindingComponent(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)

        favListViewModel = ViewModelProvider(this, viewModelFactory).get(FavListViewModel::class.java)
        val dataBinding: ActivityFavListBinding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_fav_list
        )
        binding = dataBinding
        setContentView(dataBinding.root)
        initRecyclerView()
    }

    private fun initRecyclerView() {
        val favAdapter = FavAdapter(dataBindingComponent, appExecutors)
        this.favAdapter = favAdapter
        binding.imageList.layoutManager = LinearLayoutManager(this)
        binding.imageList.adapter = favAdapter
        favListViewModel.getFavList().observe(this, {
            if (it != null) {
                favAdapter.submitList(it)
                binding.resultCount = it.size
                favAdapter.notifyDataSetChanged()
                binding.executePendingBindings()
                Timber.d("LIST SIZE%s", it.size)
            }
        })
    }
}