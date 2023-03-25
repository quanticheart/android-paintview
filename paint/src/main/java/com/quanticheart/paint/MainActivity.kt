package com.quanticheart.paint

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.ViewTreeObserver
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.slider.RangeSlider
import com.quanticheart.paint.databinding.PaintViewBinding
import java.io.ByteArrayOutputStream

class MainActivity : AppCompatActivity() {

    lateinit var binding: PaintViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PaintViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val startActivityForResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    if (it.data != null && it.data!!.data != null) {
                        val bmp = binding.drawView.save()
                        val uri: Uri = it.data!!.data!!
                        val op = contentResolver.openOutputStream(uri)
                        bmp?.compress(Bitmap.CompressFormat.PNG, 100, op)
                    } else {
                        Toast.makeText(this, "Some error ocured", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        //the undo button will remove the most recent stroke from the canvas
        binding.btnUndo.setOnClickListener { binding.drawView.undo() }
        binding.btnShare.setOnClickListener { share(binding.drawView.save()) }

        //the save button will save the current canvas which is actually a bitmap
        //in form of PNG, in the storage
        binding.btnSave.setOnClickListener {
            createFile("sample.png", startActivityForResult)
        }
        //the color button will allow the user to select the color of his brush
        val adapter = ColorAdapter(this, initList())
        binding.btnColor.adapter = adapter
        binding.btnColor.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                // It returns the clicked item.
                val clickedItem = parent?.getItemAtPosition(position) as ColorItem?
                val color = clickedItem?.color ?: 0
                binding.drawView.setColor(color)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        // the button will toggle the visibility of the RangeBar/RangeSlider
        binding.btnStroke.setOnClickListener {
            if (binding.rangebar.visibility == View.VISIBLE)
                binding.rangebar.visibility = View.GONE
            else binding.rangebar.visibility = View.VISIBLE
        }

        //set the range of the RangeSlider
        binding.rangebar.valueFrom = 0.0f
        binding.rangebar.valueTo = 100.0f
        //adding a OnChangeListener which will change the stroke width
        //as soon as the user slides the slider
        binding.rangebar.addOnChangeListener(RangeSlider.OnChangeListener { slider, value, fromUser ->
            binding.drawView.setStrokeWidth(
                value.toInt()
            )
        })

        //pass the height and width of the custom view to the init method of the DrawView object
        val vto: ViewTreeObserver = binding.drawView.viewTreeObserver
        vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.drawView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val width = binding.drawView.measuredWidth
                val height = binding.drawView.measuredHeight
                binding.drawView.init(height, width)
            }
        })
    }

    private fun createFile(fileName: String, launcher: ActivityResultLauncher<Intent>) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        // file type
        intent.type = "image/*"
        // file name
        intent.putExtra(Intent.EXTRA_TITLE, fileName)
        intent.addFlags(
            Intent.FLAG_GRANT_READ_URI_PERMISSION
                    or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                    or Intent.FLAG_GRANT_PREFIX_URI_PERMISSION
        )
        launcher.launch(intent)
    }

    private fun initList(): ArrayList<ColorItem> {
        val colors = ArrayList<ColorItem>()
        colors.add(ColorItem(Color.GREEN, "GREEN"))
        colors.add(ColorItem(Color.BLUE, "BLUE"))
        colors.add(ColorItem(Color.BLACK, "BLACK"))
        return colors
    }

    private fun share(bitmap: Bitmap?) {
        val i = Intent(Intent.ACTION_SEND)
        i.type = "image/*"
        i.putExtra(Intent.EXTRA_STREAM, getImageUri(this, bitmap))
        try {
            startActivity(Intent.createChooser(i, "My Profile ..."))
        } catch (ex: ActivityNotFoundException) {
            ex.printStackTrace()
        }
    }

    private fun getImageUri(inContext: Context, inImage: Bitmap?): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage?.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            inContext.contentResolver,
            inImage,
            "Title",
            null
        )
        return Uri.parse(path)
    }
}