package com.cidra.hologram

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import com.google.android.gms.oss.licenses.OssLicensesActivity
import kotlinx.android.synthetic.main.fragment_about_this_app.view.*
import kotlinx.android.synthetic.main.web_view.*


class AboutThisAppFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_about_this_app, container, false)
        val versionText = view.version_code

        /**
         * version codeを取得して表示
         */
        try {
            val packagename = activity?.packageName
            val pm = activity?.packageManager
            val info = packagename?.let { pm?.getPackageInfo(it, PackageManager.GET_META_DATA) }

            if (info != null) {
                versionText.text = "version ".plus(info.versionName)
            }

        } catch (e: PackageManager.NameNotFoundException) {
            versionText.text = e.toString()
        }

        view.twemoji_license.setOnClickListener {
            val termsUrl = "https://github.com/CIDRA4023/Hologram/blob/master/Terms.md"
            val intentTerms = Intent(Intent.ACTION_VIEW)
            intentTerms.data = Uri.parse(termsUrl)
            startActivity(intentTerms)
        }

        view.terms_app.setOnClickListener {
            val termsUrl = "https://github.com/twitter/twemoji"
            val intentTerms = Intent(Intent.ACTION_VIEW)
            intentTerms.data = Uri.parse(termsUrl)
            startActivity(intentTerms)
        }

        view.prvacy_policy_app.setOnClickListener {
            val policyUrl = "https://github.com/CIDRA4023/Hologram/blob/master/PrivacyPolicy.md"
            val intentPolicy = Intent(Intent.ACTION_VIEW)
            intentPolicy.data = Uri.parse(policyUrl)
            startActivity(intentPolicy)
        }

//        view.license_app.setOnClickListener {
////            val intent = Intent(activity?.applicationContext, OssLicensesActivity::class.java)

////            startActivity(intent)
//            val webView = view.findViewById<WebView>(R.id.webView)
//            webView.loadUrl("file:///android_asset/licenses.html")
//        }

        return view
    }


}