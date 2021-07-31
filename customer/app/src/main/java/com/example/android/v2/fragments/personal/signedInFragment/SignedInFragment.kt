package com.example.android.v2.fragments.personal.signedInFragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.webkit.MimeTypeMap
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.android.v2.AUTH
import com.example.android.v2.FIRESTORE
import com.example.android.v2.R
import com.example.android.v2.activities.MainActivity
import com.example.android.v2.activities.personal.OrderRequestsActivity
import com.example.android.v2.fragments.personal.AccountFragment
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.dialog_change_password.*
import kotlinx.android.synthetic.main.fragment_signed_in.*


class SignedInFragment : Fragment() {

    companion object {
        var context: Context? = null

    }

    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_signed_in, container, false)
    }

    @SuppressLint("CutPasteId")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        val username = view.findViewById<TextView>(R.id.tvUsername)
        val phoneNumber = view.findViewById<TextView>(R.id.tvPhone)
        val email = view.findViewById<TextView>(R.id.tvEmail)
        val password = view.findViewById<TextView>(R.id.tvPassword)
        val avatar = view.findViewById<CircleImageView>(R.id.profile_image)

        setUpUserInformation(username, phoneNumber, email, password, avatar)

        profile_image.setOnClickListener {
            chooseImage()
        }

        btnOrderRequests.setOnClickListener {
            startActivity(Intent(context, OrderRequestsActivity::class.java))
        }

        tvChangePassword.setOnClickListener {
            createDialog()
            setUpUserInformation(username, phoneNumber, email, password, avatar)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.option_menu_customer, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.navigation_logout) {
            AUTH.signOut()
            Toast.makeText(context, "Đăng xuất thành công", Toast.LENGTH_SHORT).show()
            val fragmentSignOut = AccountFragment()
            openFragment(fragmentSignOut)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun createDialog() {
        val mDialog = LayoutInflater.from(context).inflate(R.layout.dialog_change_password, null)
        val mBuilder = AlertDialog.Builder(context).setView(mDialog).setTitle("Cập nhật mật khẩu")
        val mAlertDialog = mBuilder.show()
        mAlertDialog.window!!.setLayout(1000, 1000);
        mAlertDialog.btn_change.setOnClickListener(View.OnClickListener {
            if (mAlertDialog.edtCurrentPassword.text.toString().isNotEmpty()
                    && mAlertDialog.edtNewPassword.text.toString().isNotEmpty()
                    && mAlertDialog.edtConfirmNewPassword.text.toString().isNotEmpty()) {
                if (mAlertDialog.edtNewPassword.text.toString() == mAlertDialog.edtConfirmNewPassword.text.toString()) {
                    val user = AUTH.currentUser
                    Log.d("TAG", "createDialog: ${mAlertDialog.edtCurrentPassword.text.toString()} ${user?.email}")
                    if (user != null) {
                        val credential = EmailAuthProvider.getCredential(user.email.toString(), mAlertDialog.edtCurrentPassword.text.toString())
                        user.reauthenticate(credential)
                                .addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        user.updatePassword(mAlertDialog.edtNewPassword.text.toString())
                                                .addOnCompleteListener {
                                                    if (it.isSuccessful) {
                                                        FIRESTORE.collection("users")
                                                                .document(user.uid)
                                                                .update("password", mAlertDialog.edtNewPassword.text.toString())
                                                                .addOnCompleteListener {
                                                                    Toast.makeText(
                                                                            context,
                                                                            "Cập nhật mật khẩu thành công",
                                                                            Toast.LENGTH_SHORT
                                                                    ).show()
                                                                    mAlertDialog.dismiss()
                                                                }
                                                    }
                                                }
                                    }
                                }
                                .addOnFailureListener {
                                    Log.d("TAG", "aks: ")
                                }
                    } else {
                        Toast.makeText(context, "Mật khẩu không trùng khớp", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Chưa điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                }
            }
            mAlertDialog.btn_cancel.setOnClickListener {
                mAlertDialog.dismiss()
                openFragment(SignedInFragment())
            }
        })
    }

    private fun setUpUserInformation(username: TextView, phoneNumber: TextView,
                                     email: TextView, password: TextView, avatar: CircleImageView) {
        val currentUser = AUTH.currentUser
        val db = Firebase.firestore.collection("users")
                .document(currentUser!!.uid)
        db.get().addOnSuccessListener(OnSuccessListener {
            if (it.exists()) {
                if (it.getString("avatar") != "") {
                    Picasso.get().load(it.getString("avatar")).into(avatar)
                    username.text = it.getString("username")
                    phoneNumber.text = it.getString("phone")
                    email.text = it.getString("email")
                    password.text = it.getString("password")
                } else {
                    username.text = it.getString("username")
                    phoneNumber.text = it.getString("phone")
                    email.text = it.getString("email")
                    password.text = it.getString("password")
                }
            } else
                Toast.makeText(context, "Không có dữ liệu về người dùng", Toast.LENGTH_SHORT).show()
        })
    }

    private fun openFragment(fragment: Fragment) =
            activity?.supportFragmentManager?.beginTransaction()?.apply {
                replace(R.id.container, fragment)
                commit()
            }

    private fun chooseImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    private fun dialogConfirmImage() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle("Xác nhận hình ảnh")
        builder.setMessage("Cập nhật hình ảnh đại diện ?")

        builder.setPositiveButton("Đồng ý", DialogInterface.OnClickListener { dialog, which ->
            uploadImage()
            dialog.dismiss()
        })

        builder.setNegativeButton("Hủy", DialogInterface.OnClickListener { dialog, which ->
            dialog.dismiss()
            openFragment(SignedInFragment())
        })

        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
                && data != null && data.data != null) {
            filePath = data.data;
            Picasso.get().load(filePath).into(profile_image)
            dialogConfirmImage()

        }
    }

    private fun uploadImage() {
        if (filePath != null) {
            val storageReference = FirebaseStorage.getInstance().getReference("images/users/${AUTH.currentUser!!.uid}")
            storageReference.child("${getFileExtension(filePath!!)}").putFile(filePath!!)
                    .addOnSuccessListener {
                        storageReference.child("${getFileExtension(filePath!!)}").downloadUrl
                                .addOnSuccessListener { it ->
                                    val userId = AUTH.currentUser!!.uid
                                    val documentRef: DocumentReference = FIRESTORE.collection("users").document(userId)

                                    documentRef.update("avatar", it.toString())
                                            .addOnSuccessListener {
                                                Log.d("TAG", "uploadImage: upload to firestore successfully")
                                            }
                                            .addOnFailureListener {
                                                Log.d("TAG", "uploadImage: failed")
                                            }

                                    Toast.makeText(context, "Tải hình thành công", Toast.LENGTH_SHORT).show()
                                }
                    }
                    .addOnFailureListener {
                        Log.d("TAG", "uploadImage: Failed")
                    }

        }
    }

    private fun getFileExtension(uri: Uri): String? {
        val cR: ContentResolver = requireContext().contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cR.getType(uri))
    }
}
