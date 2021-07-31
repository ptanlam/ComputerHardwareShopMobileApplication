package com.example.android.v2.fragments.personal

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.example.android.v2.R
import com.example.android.v2.activities.personal.LoginActivity
import com.example.android.v2.activities.personal.RegisterActivity
import kotlinx.android.synthetic.main.fragment_account.*

class AccountFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(false)

        btnSignIn.setOnClickListener(View.OnClickListener {
            startActivity(Intent(context, LoginActivity::class.java))
        })

        btnSignUp.setOnClickListener(View.OnClickListener {
            startActivity(Intent(context, RegisterActivity::class.java))
        })

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.option_menu_customer, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

}