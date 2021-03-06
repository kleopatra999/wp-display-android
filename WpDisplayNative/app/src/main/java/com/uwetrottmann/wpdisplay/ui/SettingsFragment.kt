/*
 * Copyright 2015 Uwe Trottmann
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

package com.uwetrottmann.wpdisplay.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.uwetrottmann.wpdisplay.R
import com.uwetrottmann.wpdisplay.settings.ConnectionSettings
import kotlinx.android.synthetic.main.fragment_settings.*

/**
 * App settings.
 */
class SettingsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        buttonSettingsStore.setOnClickListener { openWebPage(getString(R.string.store_page_url)) }

        var version: String
        try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            version = packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            version = ""
        }

        textViewSettingsVersion.text = getString(R.string.version, version)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val actionBar = (activity as AppCompatActivity).supportActionBar
        if (actionBar != null) {
            actionBar.setTitle(R.string.title_settings)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onResume() {
        super.onResume()

        populateViews()
    }

    override fun onPause() {
        super.onPause()

        saveSettings()
    }

    private fun populateViews() {
        editTextSettingsHost.setText(ConnectionSettings.getHost(activity))
        editTextSettingsPort.setText(ConnectionSettings.getPort(activity).toString())
    }

    private fun saveSettings() {
        val host = editTextSettingsHost.text.toString()
        val port = Integer.valueOf(editTextSettingsPort.text.toString())!!
        ConnectionSettings.saveConnectionSettings(activity, host, port)
    }

    private fun openWebPage(url: String) {
        val webpage = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, webpage)
        if (intent.resolveActivity(context.packageManager) != null) {
            startActivity(intent)
        }
    }
}
