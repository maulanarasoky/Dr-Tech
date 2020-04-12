package com.example.drtech.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.CompoundButton
import android.widget.Toast
import com.example.drtech.R
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.activity_search.*
import org.jetbrains.anko.toast

class Search : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        setSupportActionBar(toolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        chip("Laptop")
        chip("Komputer")
        chip("HP")
        chip("Programming")
        chip("Web")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    val list: MutableList<Chip> = mutableListOf()

    private fun chip(data: String){
        val chipValue = data.replace(" ", "")
        val chip = Chip(this)
        chip.text = chipValue
        chip.isCloseIconVisible = false
        chip.isCheckable = true
        chip.isClickable = true
        chip.chipBackgroundColor = resources.getColorStateList(R.color.twitterColour)
        chip.setTextColor(resources.getColor(android.R.color.white))
        chip.closeIconTint = resources.getColorStateList(android.R.color.white)
        chip.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener{
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                if(chip.isChecked){
                    if(list.size == 3){
                        showToast("Tag maksimal 3")
                        chip.isChecked = false
                    }else{
                        list.add(chip)
                        showToast("TAMBAH : " + list.size.toString())
                    }
                }else{
                    list.remove(chip)
                    showToast("KURANG : " + list.size.toString())
                }
            }

        })
        chipGroup.addView(chip)
    }

    private fun showToast(text: String){
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }
}
