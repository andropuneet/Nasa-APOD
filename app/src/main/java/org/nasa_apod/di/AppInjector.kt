/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.nasa_apod.di

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import dagger.android.AndroidInjection
import org.nasa_apod.MainApplication

/**
 * Helper class to automatically inject fragments if they implement [].
 */
object AppInjector {
    @JvmStatic
    fun init(mainApplication: MainApplication) {
        DaggerAppComponent.builder().application(mainApplication)
            .build().inject(mainApplication)
        mainApplication
            .registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
                override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                    handleActivity(activity)
                }

                override fun onActivityStarted(activity: Activity) {}
                override fun onActivityResumed(activity: Activity) {}
                override fun onActivityPaused(activity: Activity) {}
                override fun onActivityStopped(activity: Activity) {}
                override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
                override fun onActivityDestroyed(activity: Activity) {}
            })
    }

    private fun handleActivity(activity: Activity) {
        AndroidInjection.inject(activity)
        //        if (activity instanceof FragmentActivity) {
//            ((FragmentActivity) activity).getSupportFragmentManager()
//                    .registerFragmentLifecycleCallbacks(
//                            new FragmentManager.FragmentLifecycleCallbacks() {
//                                @Override
//                                public void onFragmentCreated(@NotNull FragmentManager fm, @NotNull Fragment f,
//                                                              Bundle savedInstanceState) {
//                                    if (f instanceof Injectable) {
//                                        AndroidSupportInjection.inject(f);
//                                    }
//                                }
//                            }, true);
//        }
    }
}