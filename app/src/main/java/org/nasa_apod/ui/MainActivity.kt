package org.nasa_apod.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import dagger.android.AndroidInjection
import org.nasa_apod.R
import org.nasa_apod.databinding.ActivityMainBinding
import org.nasa_apod.viewmodel.MainViewModel
import org.nasa_apod.vo.Resource
import org.nasa_apod.vo.Status
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var mainViewModel: MainViewModel
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        mainViewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
        var dataBinding : ActivityMainBinding= DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding = dataBinding
        setContentView(dataBinding.root)
//        dataBinding.setCallback { mainViewModel.retry()}
        binding.searchResource = Resource(Status.SUCCESS,"https://apod.nasa.gov/apod/image/2109/M51-SL14-RGB-196-Final-cC_1024.png","")
        binding.image = "https://apod.nasa.gov/apod/image/2109/M51-SL14-RGB-196-Final-cC_1024.png"
    }
}