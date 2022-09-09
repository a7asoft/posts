package com.testing.testforjob.presentation.info

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.testing.testforjob.R
import com.testing.testforjob.databinding.FragmentInfoBinding
import com.testing.testforjob.utils.Functions
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.blurry.Blurry

@AndroidEntryPoint
class InfoFragment : Fragment() {
    private lateinit var binding : FragmentInfoBinding
    private val phone = "+5358929076"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.wtf("onCreateContacts", "OnCreate")

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentInfoBinding.inflate(inflater)
        val root = binding.root
        Log.wtf("onCreateViewContacts", "onCreateView")

        val bitmap = Functions.drawableToBitmap(requireContext().resources.getDrawable(R.drawable.perfil))
        Blurry.with(requireContext()).from(bitmap).into(binding.blurrImage)

        listeners()
        return root
    }

    private fun listeners() {
        binding.btnCall.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$phone")
            startActivity(intent)
        }

        binding.btnMessage.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", phone, null)))
        }

        binding.etLinked.setOnClickListener {
            val url = "https://www.linkedin.com/in/apzaldivar/"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            InfoFragment().apply {

            }
    }
}