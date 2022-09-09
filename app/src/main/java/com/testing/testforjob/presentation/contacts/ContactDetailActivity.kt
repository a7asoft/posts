package com.testing.testforjob.presentation.contacts

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.util.Log
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.testing.testforjob.databinding.ActivityContactDetailBinding
import com.testing.testforjob.presentation.common.extension.gone
import com.testing.testforjob.utils.ColorGenerator
import com.testing.testforjob.utils.Functions.Companion.drawableToBitmap
import com.testing.testforjob.utils.Functions.Companion.makeStatusBarTransparent
import com.testing.testforjob.utils.TextDrawable
import contacts.core.*
import contacts.core.util.addresses
import contacts.core.util.emails
import contacts.core.util.names
import contacts.core.util.phones
import jp.wasabeef.blurry.Blurry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ContactDetailActivity : AppCompatActivity() {

    private var updated = false
    private lateinit var contacts: Query.Result
    private lateinit var binding: ActivityContactDetailBinding
    private var toggleEdit = false
    private lateinit var context: Context
    private lateinit var phone: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityContactDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        makeStatusBarTransparent()
        context = this

        val photoUri = intent.extras?.getString("EXTRA_PHOTO_URI") as String
        val name = intent.extras?.getString("EXTRA_NAME") as String
        phone = intent.extras?.getString("EXTRA_NUMBER") as String
        val email = intent.extras?.getString("EXTRA_EMAIL") as String
        val address = intent.extras?.getString("EXTRA_ADDRESS") as String

        if (phone != "") {
            binding.etPhone.setText(phone)

        } else {
            binding.llPhone.gone()
            binding.btnCall.gone()
            binding.btnMessage.gone()
        }

        if (email != "") {
            binding.etEmail.setText(email)
        } else {
            binding.llEmail.gone()
        }

        if (address != "") {
            binding.etAddress.setText(address)
        } else {
            binding.llAddress.gone()
        }

        binding.tvName.setText(name)

        if (photoUri != "") {
            Glide.with(this).load(Uri.parse(photoUri)).circleCrop().into(binding.ivProfile)

            val bitmap =
                MediaStore.Images.Media.getBitmap(this.contentResolver, Uri.parse(photoUri))
            Blurry.with(this).from(bitmap).into(binding.blurrImage)

        } else {
            val generator = ColorGenerator.MATERIAL
            val color = generator!!.randomColor

            val drawable = TextDrawable.builder()
                .beginConfig()
                .textColor(Color.WHITE)
                .bold()
                .toUpperCase()
                .endConfig()
                .buildRound(name.first().toString(), color)

            val image = drawableToBitmap(drawable)
            Blurry.with(this).from(image).into(binding.blurrImage)

            binding.ivProfile.setImageDrawable(drawable)
        }

        searchContact(phone)

        listeners()
    }

    private fun listeners() {
        binding.ibBack.setOnClickListener {
            if (updated) {
                val returnIntent = Intent()
                returnIntent.putExtra("UPDATED", updated)
                setResult(RESULT_OK, returnIntent)
                finish()
            } else {
                finish()
            }
        }

        binding.btnCall.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$phone")
            startActivity(intent)
        }

        binding.btnMessage.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", phone, null)))
        }

        binding.btnShare.setOnClickListener {
            val shareUri = Uri.withAppendedPath(
                ContactsContract.Contacts.CONTENT_VCARD_URI,
                contacts.first().lookupKey
            )

            val intent = Intent(Intent.ACTION_SEND)
            intent.type = ContactsContract.Contacts.CONTENT_VCARD_URI.toString()
            intent.putExtra(Intent.EXTRA_STREAM, shareUri)
            intent.putExtra(Intent.EXTRA_SUBJECT, "Share contact")
            startActivity(intent)
        }

        binding.btnDelete.setOnClickListener {
            showDialogConfirm()
        }

        binding.ibEdit.setOnClickListener {
            toggleEdit = !toggleEdit
            if (toggleEdit) {
                binding.ibEdit.setImageResource(com.testing.testforjob.R.drawable.ic_outline_save_24)
                binding.tvName.isEnabled = true
                binding.etPhone.isEnabled = true
                binding.etEmail.isEnabled = true
                binding.etAddress.isEnabled = true
            } else {
                binding.ibEdit.setImageResource(com.testing.testforjob.R.drawable.ic_outline_edit_24)
                binding.tvName.isEnabled = false
                binding.etPhone.isEnabled = false
                binding.etEmail.isEnabled = false
                binding.etAddress.isEnabled = false
                //do save
                if (contacts.size > 0) {
                    val name = binding.tvName.text.toString()
                    val phone = binding.etPhone.text.toString()
                    val email = binding.etEmail.text.toString()
                    val address = binding.etAddress.text.toString()

                    val contact = contacts.first()

                    CoroutineScope(Dispatchers.Default).launch {
                        val result = Contacts(context)
                            .update()
                            .contacts((contact).mutableCopy {
                                if (name != "") names().first().displayName = name
                                if (phone != "") phones().first().number = phone
                                if (email != "") emails().first().address = email
                                if (address != "") addresses().first().formattedAddress = address
                            })
                            .commit()

                        withContext(Dispatchers.Main) {
                            if (result.isSuccessful) {
                                updated = true
                                Toast.makeText(
                                    context,
                                    "Contact updated",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Error updating contact",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showDialogConfirm() {
        val dialogDelete = Dialog(this@ContactDetailActivity)
        dialogDelete.requestWindowFeature(Window.FEATURE_NO_TITLE) // before

        dialogDelete.setContentView(com.testing.testforjob.R.layout.dialog_comfirm)
        dialogDelete.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogDelete.setCancelable(true)
        dialogDelete.setCanceledOnTouchOutside(true)

        val btnSubmitDelete: MaterialButton =
            dialogDelete.findViewById(com.testing.testforjob.R.id.btn_submit)
        val btnCancelDelete: MaterialButton =
            dialogDelete.findViewById(com.testing.testforjob.R.id.btn_cancel)

        btnCancelDelete.setOnClickListener {
            dialogDelete.dismiss()
        }

        btnSubmitDelete.setOnClickListener {
            CoroutineScope(Dispatchers.Default).launch {
                val contact = contacts.first()
                val result = Contacts(context)
                    .delete()
                    .contacts(contact)
                    .commit()

                withContext(Dispatchers.Main) {
                    dialogDelete.dismiss()
                    if (result.isSuccessful) {
                        updated = true
                        val returnIntent = Intent()
                        returnIntent.putExtra("UPDATED", updated)
                        setResult(RESULT_OK, returnIntent)
                        finish()
                    } else {
                        dialogDelete.dismiss()
                        Toast.makeText(
                            context,
                            "Error deleting contact",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            dialogDelete.dismiss()
        }
        dialogDelete.show()
    }

    private fun searchContact(phone: String) {
        contacts = Contacts(this)
            .query()
            .where {
                Phone.Number contains phone
            }
            .orderBy(ContactsFields.DisplayNamePrimary.desc())
            .offset(0)
            .find()

        Log.wtf("RESULT", "${contacts.size}")
    }



    override fun onBackPressed() {
        if (updated) {
            val returnIntent = Intent()
            returnIntent.putExtra("UPDATED", updated)
            setResult(RESULT_OK, returnIntent)
            finish()
        } else {
            finish()
        }

    }
}
