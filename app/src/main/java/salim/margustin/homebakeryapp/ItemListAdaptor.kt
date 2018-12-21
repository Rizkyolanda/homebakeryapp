package salim.margustin.homebakeryapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

import java.util.ArrayList

class ItemListAdapter(val context: Context, val layout: Int, val itemList: ArrayList<Item>) :
    BaseAdapter() {

    override fun getCount(): Int {
        return itemList.size
    }

    override fun getItem(position: Int): Any {
        return itemList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    private inner class ViewHolder {
        var imageView: ImageView? = null
        var txtName: TextView? = null
        var txtIngredient: TextView? = null
        var txtRecipes: TextView? = null
    }

    override fun getView(position: Int, view: View, viewGroup: ViewGroup): View {

        var row: View? = view
        var holder = ViewHolder()

        if (row == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            row = inflater.inflate(layout, null)

            holder.txtName = row!!.findViewById<View>(R.id.txtName) as TextView
            holder.imageView = row.findViewById<View>(R.id.imgItem) as ImageView
            row.tag = holder

        } else {
            holder = row.tag as ViewHolder
        }

        val item = itemList[position]

        holder.txtName!!.setText(item.name)
        holder.txtIngredient!!.setText(item.ingredient)
        holder.txtRecipes!!.setText(item.recipes)

        val itemImage = item.image
        val bitmap = BitmapFactory.decodeByteArray(itemImage, 0, itemImage.size)
        holder.imageView!!.setImageBitmap(bitmap)

        return row
    }
}