package salim.margustin.homebakeryapp

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.*

import java.io.FileNotFoundException
import java.util.ArrayList

class ItemList : AppCompatActivity() {

    lateinit var list: ArrayList<Item>
    lateinit var listView: ListView
    var adapter: ItemListAdapter? = null

    lateinit var imageViewItem: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        listView = findViewById<View>(R.id.listView) as ListView
        list = ArrayList()
        adapter = ItemListAdapter(this, R.layout.activity_item, list)
        listView.adapter = adapter

        val cursor = ItemActivity.sqLiteHelper.getData("SELECT * FROM ITEM")
        list.clear()
        while (cursor.moveToNext()) {
            val id = cursor.getInt(0)
            val name = cursor.getString(1)
            val ingredient = cursor.getString(2)
            val recipes = cursor.getString(3)
            val image = cursor.getBlob(4)

            list.add(Item(name, ingredient, recipes, image, id))
        }
        adapter!!.notifyDataSetChanged()

        listView.onItemLongClickListener = AdapterView.OnItemLongClickListener { parent, view, position, id ->
            val items = arrayOf<CharSequence>("Update", "Delete")
            val dialog = AlertDialog.Builder(this@ItemList)

            dialog.setTitle("Choose an action")
            dialog.setItems(items) { dialog, item ->
                if (item == 0) {
                    val c = ItemActivity.sqLiteHelper.getData("SELECT id FROM ITEM")
                    val arrID = ArrayList<Int>()
                    while (c.moveToNext()) {
                        arrID.add(c.getInt(0))
                    }
                    showDialogUpdate(this@ItemList, arrID[position])

                } else {
                    val c = ItemActivity.sqLiteHelper.getData("SELECT id FROM ITEM")
                    val arrID = ArrayList<Int>()
                    while (c.moveToNext()) {
                        arrID.add(c.getInt(0))
                    }
                    showDialogDelete(arrID[position])
                }
            }
            dialog.show()
            true
        }
    }

    private fun showDialogUpdate(activity: Activity, position: Int) {

        val dialog = Dialog(activity)
        dialog.setContentView(R.layout.activity_update)
        dialog.setTitle("Update")

        imageViewItem = dialog.findViewById<View>(R.id.imageViewItem) as ImageView
        val edtName = dialog.findViewById<View>(R.id.edtName) as EditText
        val edtIngredient = dialog.findViewById<View>(R.id.edtIngredient) as EditText
        val edtRecipes =dialog.findViewById<View>(R.id.edtRecipes) as EditText
        val updateBtn = dialog.findViewById<View>(R.id.updateBtn) as Button

        val width = (activity.resources.displayMetrics.widthPixels * 0.95).toInt()
        val height = (activity.resources.displayMetrics.heightPixels * 0.7).toInt()
        dialog.window!!.setLayout(width, height)
        dialog.show()

        imageViewItem.setOnClickListener {
            ActivityCompat.requestPermissions(
                this@ItemList,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                888
            )
        }

        updateBtn.setOnClickListener {
            try {
                ItemActivity.sqLiteHelper.updateData(
                    edtName.text.toString().trim { it <= ' ' },
                    edtIngredient.text.toString().trim { it <= ' ' },
                    edtRecipes.text.toString().trim { it <= ' ' },
                    ItemActivity.imageViewToByte(imageViewItem),
                    position
                )
                dialog.dismiss()
                Toast.makeText(applicationContext, "Update successfully!!!", Toast.LENGTH_SHORT).show()
            } catch (error: Exception) {
                Log.e("Update error", error.message)
            }

            updateItemList()
        }
    }

    private fun showDialogDelete(idItem: Int) {
        val dialogDelete = AlertDialog.Builder(this@ItemList)

        dialogDelete.setTitle("Warning!!")
        dialogDelete.setMessage("Are you sure you want to this delete?")
        dialogDelete.setPositiveButton("OK") { dialog, which ->
            try {
                ItemActivity.sqLiteHelper.deleteData(idItem)
                Toast.makeText(applicationContext, "Delete successfully!!!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e("error", e.message)
            }

            updateItemList()
        }

        dialogDelete.setNegativeButton(
            "Cancel"
        ) { dialog, which -> dialog.dismiss() }
        dialogDelete.show()
    }

    private fun updateItemList() {
        val cursor = ItemActivity.sqLiteHelper.getData("SELECT * FROM Item")
        list.clear()
        while (cursor.moveToNext()) {
            val id = cursor.getInt(0)
            val name = cursor.getString(1)
            val ingredient = cursor.getString(2)
            val recipes = cursor.getString(3)
            val image = cursor.getBlob(4)

            list.add(Item(name, ingredient, recipes, image, id))
        }
        adapter!!.notifyDataSetChanged()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

        if (requestCode == 888) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent, 888)
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

        if (requestCode == 888 && resultCode == Activity.RESULT_OK && data != null) {
            val uri = data.data
            try {
                val inputStream = contentResolver.openInputStream(uri!!)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                imageViewItem.setImageBitmap(bitmap)

            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }

        }

        super.onActivityResult(requestCode, resultCode, data)
    }
}