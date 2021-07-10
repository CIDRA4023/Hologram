package com.cidra.hologram

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    companion object {
        lateinit var instance: MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        instance = this
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.item_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    /**
     * オプションメニューのタップ後処理
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            // プライバシーポリシー
            R.id.policy -> {
                val policyUrl = "https://github.com/CIDRA4023/Hologram/blob/master/PrivacyPolicy.md"
                val intentPolicy = Intent(Intent.ACTION_VIEW)
                intentPolicy.data = Uri.parse(policyUrl)
                startActivity(intentPolicy)
            }
            // 利用規約
            R.id.terms -> {
                val termsUrl = "https://github.com/CIDRA4023/Hologram/blob/master/Terms.md"
                val intentTerms = Intent(Intent.ACTION_VIEW)
                intentTerms.data = Uri.parse(termsUrl)
                startActivity(intentTerms)
            }
        }

        return super.onOptionsItemSelected(item)
    }
}