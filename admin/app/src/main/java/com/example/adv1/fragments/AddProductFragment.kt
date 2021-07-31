package com.example.adv1.fragments

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.MimeTypeMap
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.adv1.R
import com.example.adv1.models.Product
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_add_product.*


private const val PICK_IMAGE_REQUEST = 71


class AddProductFragment : Fragment() {
    private var filePath: Uri? = null

    private var mStorageRef: StorageReference? = null
    private var mCloudDatabase: CollectionReference? = null
    private val db = FirebaseFirestore.getInstance()
            .collection("products")

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_add_product, container,
                false)
        mCloudDatabase = FirebaseFirestore.getInstance().collection("images")
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnAddProduct.setOnClickListener {
            startProgressing()
            saveProduct()
        }

        btnChooseProductImage.setOnClickListener {
            openFileChooser()
        }

        setUpCategoriesSpinner()
    }

    private fun setUpCategoriesSpinner() {
        val adapter = context?.let {
            ArrayAdapter.createFromResource(
                    it,
                    R.array.product_categories,
                    R.layout.support_simple_spinner_dropdown_item)
        }
        spinnerAddCategories.adapter = adapter
    }

    @SuppressLint("ShowToast")
    private fun saveProduct() {
        val categoryName = spinnerAddCategories.selectedItem.toString()
                .split("-")[0].trim()

        val categoryRef = db
                .document(categoryName)
                .collection(categoryName).document()

        if (inputProductName.text.toString().isNotEmpty()
                && inputProductPrice.text.toString().isNotEmpty()
                && inputProductQuantity.text.toString().isNotEmpty()
        ) {

            val product = Product()
            product.name = inputProductName.text.toString()
            product.price = inputProductPrice.text.toString().toInt()
            product.quantity = inputProductQuantity.text.toString().toInt()
            product.category = categoryName

            categoryRef.set(product)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            categoryRef.update("id", categoryRef.id)
                            product.id = categoryRef.id
                            if (filePath != null) {
                                uploadProductImage(categoryRef.id, product, categoryRef)
                            }
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                                context,
                                "Thêm sản phẩm không thành công",
                                Toast.LENGTH_LONG
                        ).show()
                    }
        } else {
            Toast.makeText(
                    context,
                    "Bạn phải điền đầy đủ thông tin sản phẩm",
                    Toast.LENGTH_LONG
            ).show()
            stopProgressing()
        }
    }

    private fun startProgressing() {
        progressBar3.visibility = View.VISIBLE
        activity?.window?.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    private fun stopProgressing() {
        if (progressBar3.visibility == View.VISIBLE) {
            progressBar3.visibility = View.INVISIBLE
        }
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    private fun clearFields() {
        inputProductName.text?.clear()
        inputProductPrice.text?.clear()
        inputProductQuantity.text?.clear()
        inputImageName.text?.clear()
        ivProductFromDevice.setImageResource(R.drawable.placeholder_image)
    }

    private fun openFileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.data != null) {
            filePath = data.data;
            Picasso.get().load(filePath).into(ivProductFromDevice);
        }
    }

    private fun getFileExtension(uri: Uri): String? {
        val cR: ContentResolver = requireContext().contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cR.getType(uri))
    }

    private fun uploadProductImage(productId: String, product: Product,
                                   categoryRef: DocumentReference) {
        //Check whether if image's path is null
        if (filePath != null) {
            //Refer to folder
            mStorageRef = FirebaseStorage.getInstance()
                    .getReference("images/products/${productId}")

            // Create an image object
            mStorageRef!!.child(
                    inputImageName.text.toString().trim() +
                            "." + getFileExtension(filePath!!))
                    .putFile(filePath!!)
                    .addOnSuccessListener {
                        // Get the image's download
                        mStorageRef!!.child(inputImageName.text.toString().trim() +
                                "." + getFileExtension(filePath!!))
                                .downloadUrl
                                .addOnSuccessListener {
                                    product.imageURL = it.toString()
                                    // Update image URL for product
                                    categoryRef.update("imageURL", product.imageURL)
                                }
                                .addOnFailureListener {
                                    Log.d("TAG", "uploadProductImage: $it")
                                }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(
                                context,
                                "Lỗi khi tải ảnh, vui lòng thử lại!: " + e.message,
                                Toast.LENGTH_SHORT
                        ).show()
                    }
                    .addOnCompleteListener {
                        stopProgressing()
                        Toast.makeText(
                                context,
                                "Đã thêm sản phẩm thành công", Toast.LENGTH_LONG
                        ).show()
                        clearFields()
                    }
        }
    }
}
