package org.nasa_apod.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.nasa_apod.ui.FavListActivity
import org.nasa_apod.ui.MainActivity

@Module
internal abstract class MainActivityModule {
    @ContributesAndroidInjector
    abstract fun contributeMainActivity(): MainActivity
    @ContributesAndroidInjector()
    abstract fun contributeFavListActivity(): FavListActivity
}