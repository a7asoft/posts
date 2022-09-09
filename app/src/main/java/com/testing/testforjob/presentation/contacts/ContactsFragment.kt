package com.testing.testforjob.presentation.contacts

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.permissionx.guolindev.PermissionX
import com.testing.testforjob.R
import com.testing.testforjob.databinding.FragmentContactsBinding
import contacts.core.entities.Contact
import contacts.core.util.addressList
import contacts.core.util.emailList
import contacts.core.util.phoneList
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


@AndroidEntryPoint
class ContactsFragment : Fragment() {
    private val DETAILS_REQUEST_CODE = 101
    private lateinit var mAdapter: ContactsAdapter
    private lateinit var binding: FragmentContactsBinding

    private val viewModel: ContactsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.wtf("onCreateContacts", "OnCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentContactsBinding.inflate(inflater)
        val root = binding.root
        Log.wtf("onCreateViewContacts", "onCreateView")

        val uiHandler = Handler(Looper.getMainLooper())
        uiHandler.post {
            checkPermissions()
        }
        setupRecyclerView()
        observe()
        listeners()
        return root
    }

    private fun checkPermissions() {
        //TODO edit permission explain dialog
        PermissionX.init(requireActivity())
            .permissions(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS)
            .onExplainRequestReason { scope, deniedList ->
                scope.showRequestReasonDialog(
                    deniedList,
                    "Core fundamental are based on these permissions",
                    "OK",
                    "Cancel"
                )
            }
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    viewModel.fetchAllContacts()
                }
            }
    }

    private fun listeners() {
        //move to a function
        val scale = resources.displayMetrics.density
        val dpAsPixels = (16.0f * scale + 0.5f)

        val cardBottom = requireActivity().findViewById<CardView>(R.id.cv_bottom)
        binding.rvContacts.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                when (newState) {
                    RecyclerView.SCROLL_STATE_DRAGGING -> {
                        cardBottom.clearAnimation()
                        cardBottom.animate()
                            .translationY((cardBottom.height + dpAsPixels)).duration = 100
                    }
                    RecyclerView.SCROLL_STATE_IDLE -> {
                        cardBottom.clearAnimation()
                        cardBottom.animate().translationY(0F).duration = 100
                    }
                }

                super.onScrollStateChanged(recyclerView, newState)
            }
        })
    }

    private fun observe() {
        observeState()
        observeContacts()
    }

    private fun observeState() {
        viewModel.mState
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun observeContacts() {
        viewModel.mContacts
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach { contacts ->
                handleContacts(contacts)
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun handleContacts(contacts: List<Contact>) {
        binding.rvContacts.adapter?.let {
            if (it is ContactsAdapter) {
                it.updateList(contacts)
            }
        }
    }

    private fun setupRecyclerView() {
        mAdapter = ContactsAdapter(mutableListOf(), requireActivity())

        mAdapter.setItemTapListener(object : ContactsAdapter.OnItemTap {
            override fun onTap(contact: Contact, view: View) {
                val intent = Intent(requireContext(), ContactDetailActivity::class.java)
                intent.putExtra(
                    "EXTRA_PHOTO_URI",
                    if (contact.photoUri != null) contact.photoUri.toString() else ""
                )
                intent.putExtra("EXTRA_NAME", contact.displayNamePrimary)
                intent.putExtra(
                    "EXTRA_NUMBER",
                    if (contact.phoneList().isNotEmpty()) contact.phoneList()
                        .first().number.toString() else ""
                )
                intent.putExtra(
                    "EXTRA_EMAIL",
                    if (contact.emailList().isNotEmpty()) contact.emailList()
                        .first().address.toString() else ""
                )
                intent.putExtra(
                    "EXTRA_ADDRESS",
                    if (contact.addressList().isNotEmpty()) contact.addressList()
                        .first().formattedAddress.toString() else ""
                )
                val image = view.findViewById<ImageView>(R.id.profile_picture)
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    requireActivity(),
                    (image as View?)!!, "profile"
                )
                startActivityForResult(intent, DETAILS_REQUEST_CODE, options.toBundle())
            }
        })

        binding.rvContacts.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(requireActivity())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == DETAILS_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data?.extras?.getBoolean("UPDATED") == true)
                viewModel.fetchAllContacts()
        }
    }
}

