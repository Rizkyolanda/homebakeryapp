package salim.margustin.homebakeryapp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast

import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException

class ItemActivity: AppCompatActivity() {

    internal lateinit var inputName: EditText
    internal lateinit var inputIngredient: EditText
    internal lateinit var inputRecipes: EditText
    internal lateinit var btnChoose: Button
    internal lateinit var btnAdd: Button
    internal lateinit var btnHome: Button
    internal lateinit var imageView: ImageView

    internal val REQUEST_CODE_GALLERY = 999

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input)

        init()

        sqLiteHelper = SQLiteHelper(this, "FoodDB.sqlite", null, 1)

        sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS FOOD(Id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, price VARCHAR, image BLOB)")

        btnChoose.setOnClickListener {
            ActivityCompat.requestPermissions(
                this@ItemActivity,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_CODE_GALLERY
            )
        }

        btnAdd.setOnClickListener {
            try {
                sqLiteHelper.insertData(
                    inputName.text.toString().trim { it <= ' ' },
                    inputIngredient.text.toString().trim { it <= ' ' },
                    inputRecipes.text.toString().trim { it <= ' ' },
                    imageViewToByte(imageView)
                )
                Toast.makeText(applicationContext, "Added successfully!", Toast.LENGTH_SHORT).show()
                inputName.setText("")
                inputIngredient.setText("")
                inputRecipes.setText("")
                imageView.setImageResource(R.mipmap.ic_launcher)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        btnHome.setOnClickListener {
            val intent = Intent(this@ItemActivity, ItemList::class.java)
            startActivity(intent)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

        if (requestCode == REQUEST_CODE_GALLERY) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent, REQUEST_CODE_GALLERY)
            } else {
                Toast.makeText(
                    applicationContext,
                    "You don't have permission to access file location!",
                    Toast.LENGTH_SHORT
                ).show()
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == REQUEST_CODE_GALLERY && resultCode == Activity.RESULT_OK && data != null) {
            val uri = data.data
            try {
                val inputStream = contentResolver.openInputStream(uri!!)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                imageView.setImageBitmap(bitmap)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun init() {
        inputName= findViewById<View>(R.id.inputName) as EditText
        inputIngredient= findViewById<View>(R.id.inputIngredient) as EditText
        inputRecipes= findViewById<View>(R.id.inputRecipes) as EditText
        btnChoose = findViewById<View>(R.id.btnChoose) as Button
        btnAdd = findViewById<View>(R.id.btnAdd) as Button
        btnHome = findViewById<View>(R.id.btnHome) as Button
        imageView = findViewById<View>(R.id.imageView) as ImageView
    }

    companion object {

        lateinit var sqLiteHelper: SQLiteHelper

        fun imageViewToByte(image: ImageView): ByteArray {
            val bitmap = (image.drawable as BitmapDrawable).bitmap
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            return stream.toByteArray()
        }
    }


}

