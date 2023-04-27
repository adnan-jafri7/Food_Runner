package com.adnan.foodrunner.fragment


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.adnan.foodrunner.R


class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val preferences = (activity as FragmentActivity).getSharedPreferences(getString(R.string.preference_file), Context.MODE_PRIVATE)
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_profile, container, false)
        var txtUserName:TextView=view.findViewById(R.id.txtUserName)
        val txtPhone:TextView=view.findViewById(R.id.txtPhone)
        val txtEmail:TextView=view.findViewById(R.id.txtEmail)
        val txtAddress:TextView=view.findViewById(R.id.txtAddress)

        txtUserName.text=preferences.getString("Name","Name")
        txtPhone.text=preferences.getString("Mobile","Mobile")
        txtEmail.text=preferences.getString("Email","Email")
        txtAddress.text=preferences.getString("Address","Address")
        return view
    }


}
