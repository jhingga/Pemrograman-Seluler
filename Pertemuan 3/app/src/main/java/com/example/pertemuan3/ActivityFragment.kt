package com.example.pertemuan3

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment

class ActivityFragment : Fragment() {

    companion object {
        private const val ARG_TEXT = "argText"

        fun newInstance(text: String): ActivityFragment {
            val fragment = ActivityFragment()
            val args = Bundle()
            args.putString(ARG_TEXT, text)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_fragment, container, false)

        val textView = view.findViewById<TextView>(R.id.fragment_text)
        val text = arguments?.getString(ARG_TEXT)
        textView.text = text

        val fragmentButton = view.findViewById<Button>(R.id.fragment_button)
        fragmentButton.setOnClickListener {
            val newFragment = newInstance("Ini diklik dari Fragment")
            fragmentManager?.beginTransaction()
                ?.replace(android.R.id.content, newFragment)
                ?.addToBackStack(null)
                ?.commit()
        }

        val activityButton = view.findViewById<Button>(R.id.activity_button)
        activityButton.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        return view
    }
}