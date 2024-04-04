package com.example.jokeapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.jokeapp.databinding.FragmentJokeListBinding


class JokeListFragment : Fragment() {

    private lateinit var binding: FragmentJokeListBinding
    // ignore the error with the JokeListFragmentArgs if it is shown -
    // try to invalidate the cache and restart the IDE
    private val args: JokeListFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentJokeListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

}