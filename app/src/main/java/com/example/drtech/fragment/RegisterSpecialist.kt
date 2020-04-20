package com.example.drtech.fragment

import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.drtech.R
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.android.synthetic.main.fragment_register_specialist.*

/**
 * A simple [Fragment] subclass.
 */
class RegisterSpecialist : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register_specialist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        specialistSkill.setOnKeyListener(object : View.OnKeyListener{
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                if(event?.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER){
                    if(specialistSkill.text.trim().isNotEmpty()){
                        inputChip(chipGroupSkill, specialistSkill)
                    }
                    specialistSkill.setText("")
                    return true
                }
                return false
            }
        })
    }

    private fun inputChip(chipGroup: ChipGroup, editText: EditText){
        if(chipGroup.childCount == 3){
            showAlert("Maksimal 3 keahlian")
            return
        }

        val text = editText.text.toString().substring(0, 1).toUpperCase() + editText.text.toString().substring(1)
        val chipValue = text.toString().replace(" ", "")
        for(i in 0 until chipGroup.childCount){
            val data = chipGroup.getChildAt(i) as Chip
            if(data.text.toString() == chipValue){
                return
            }
        }

        val chip = Chip(context)
        chip.text = chipValue
        chip.isCloseIconVisible = true
        chip.isCheckable = false
        chip.isClickable = false
        chip.chipBackgroundColor = resources.getColorStateList(R.color.purpleDark)
        chip.setTextColor(resources.getColor(android.R.color.white))
        chip.closeIconTint = resources.getColorStateList(android.R.color.white)
        chip.setOnCloseIconClickListener {
            chipGroup.removeView(it)
        }
        chipGroup.addView(chip)
        chipGroup.visibility = View.VISIBLE
    }

    private fun showAlert(title: String){
        val dialog = SweetAlertDialog(context, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
        dialog.setCustomImage(R.drawable.ic_dr_tech)
        dialog.titleText = title
        dialog.setCancelable(false)
        dialog.show()
    }

}
