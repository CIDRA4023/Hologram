package com.cidra.hologram

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView

class WebViewFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_web_view, container, false)

        val licenseView = view.findViewById<WebView>(R.id.webView)
        licenseView.loadUrl("file:///android_asset/licenses.html")
        // Inflate the layout for this fragment
        return view
    }

}