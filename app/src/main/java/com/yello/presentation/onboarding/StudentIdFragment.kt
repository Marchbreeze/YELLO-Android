package com.yello.presentation.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.ui.base.BindingFragment
import com.yello.R
import com.yello.databinding.FragmentStudentidBinding

class StudentIdFragment : BindingFragment<FragmentStudentidBinding>(R.layout.fragment_studentid) {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_studentid, container, false)
    }
}
