package salim.margustin.homebakeryapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog
import com.rengwuxian.materialedittext.MaterialEditText
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import salim.margustin.homebakeryapp.Retrofit.INodeJS
import salim.margustin.homebakeryapp.Retrofit.RetrofitClient

class MainActivity : AppCompatActivity() {

    lateinit var myAPI: INodeJS
    var compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val retrofit = RetrofitClient.instance
        myAPI = retrofit.create(INodeJS::class.java)

        login_button.setOnClickListener {
            login(edt_email.text.toString(), edt_password.text.toString())
        }

        register_button.setOnClickListener {
            register(edt_email.text.toString(), edt_password.text.toString())
        }
    }

    private fun register(email: String, password: String) {

        val enter_name_view = LayoutInflater.from(this@MainActivity)
            .inflate(R.layout.enter_name_layout, null)

        MaterialStyledDialog.Builder(this@MainActivity)
            .setTitle("Register")
            .setDescription("Input Your Name")
            .setCustomView(enter_name_view)
            .setIcon(R.drawable.ic_user)
            .setNegativeText("Cancel")
            .onNegative { dialog, _ -> dialog.dismiss() }
            .setPositiveText("Register")
            .onPositive { _, _ ->

                val edt_name = enter_name_view.findViewById<View>(R.id.edt_name) as MaterialEditText

                compositeDisposable.add(myAPI.registerUser(email, edt_name.text.toString(), password)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { message ->
                        Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
                    })
            }.show()
    }

    private fun login(email: String, password: String) {
        compositeDisposable.add(myAPI.loginUser(email, password)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { message ->
                if (email == "admin" && password == "root") {
                    Toast.makeText(this@MainActivity, "Login Success", Toast.LENGTH_LONG).show()
                    val intent = Intent(this@MainActivity, ItemActivity::class.java)
                    startActivity(intent)
                }

                if  (message.contains("encrypted_password")) {
                     Toast.makeText(this@MainActivity, "Login Success", Toast.LENGTH_LONG).show()
                    val intent = Intent(this@MainActivity, ItemList::class.java)
                    startActivity(intent)


                }
                    else
                     Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
            })
    }

}

