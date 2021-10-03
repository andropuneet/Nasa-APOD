package org.nasa_apod.ui

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import dagger.android.AndroidInjection
import org.nasa_apod.R
import org.nasa_apod.common.RetryCallback
import org.nasa_apod.databinding.ActivityMainBinding
import org.nasa_apod.utils.NetworkMonitor
import org.nasa_apod.viewmodel.MainViewModel
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var networkMonitor: NetworkMonitor
    lateinit var mainViewModel: MainViewModel
    lateinit var binding: ActivityMainBinding
    private val dateCalendar: Calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        mainViewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
        var dataBinding: ActivityMainBinding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_main
        )
        dataBinding.callback = object : RetryCallback {
            override fun retry() {
                Timber.d("retry")
                mainViewModel.retry()
            }

        }

        binding = dataBinding
        setContentView(dataBinding.root)
        mainViewModel.date.value = dateFormat.format(dateCalendar.time)
        val date = OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            dateCalendar.set(Calendar.YEAR, year)
            dateCalendar.set(Calendar.MONTH, monthOfYear)
            dateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            mainViewModel.date.value = dateFormat.format(dateCalendar.time)
        }
        mainViewModel.data.observe(this, Observer {
            binding.searchResource = it
            binding.imageInfo = it.data
            binding.executePendingBindings()
        })
        binding.markFav.setOnClickListener {
            mainViewModel.saveToFav(binding.imageInfo!!).observe(this, Observer {
                binding.searchResource = it
                binding.imageInfo = it.data
                binding.executePendingBindings()
            })
        }
        binding.imageView.setOnClickListener {
            if (networkMonitor.isConnected) {
                if (binding.imageInfo?.url!!.contains("youtube")) {
                    val intentBrowser = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(binding.imageInfo?.url!!)
                    )
                    try {
                        this.startActivity(intentBrowser)
                    } catch (ex: ActivityNotFoundException) {
                        this.startActivity(intentBrowser)
                    }
                }
            } else {
                Toast.makeText(this, R.string.network_connection, Toast.LENGTH_SHORT).show()
            }
        }
        binding.calendar.setOnClickListener {
            var dialog = DatePickerDialog(
                this, date, dateCalendar
                    .get(Calendar.YEAR), dateCalendar.get(Calendar.MONTH),
                dateCalendar.get(Calendar.DAY_OF_MONTH)
            )
            dialog.datePicker.maxDate = Calendar.getInstance().timeInMillis
            dialog.show()
        }
        binding.fav.setOnClickListener {
            val intent = Intent(this, FavListActivity::class.java)
            startActivity(intent)
        }

    }
}