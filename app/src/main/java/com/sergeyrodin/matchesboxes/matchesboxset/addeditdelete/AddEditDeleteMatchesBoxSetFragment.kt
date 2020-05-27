package com.sergeyrodin.matchesboxes.matchesboxset.addeditdelete

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.sergeyrodin.matchesboxes.R

/**
 * A simple [Fragment] subclass.
 */
class AddEditDeleteMatchesBoxSetFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_edit_delete_matches_box_set, container, false)
    }

}
