package br.com.digitalhouse.aula2001_firebase_storage

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var alertDialog: AlertDialog
    lateinit var storageReference: StorageReference
    private val CODE_IMG = 1000 // para recuperar a imagem deve utilizar esse codigo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        config()

        fbUpload.setOnClickListener {
            getRec()
        }
    }

    private fun config() {
        alertDialog = SpotsDialog.Builder().setContext(this).build()

        storageReference = FirebaseStorage.getInstance().getReference("img")
    }

    private fun getRec() {
        val intent = Intent()
        intent.type = "image/"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Captura Imagem"), CODE_IMG)
    }

    //Escuta a activity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == CODE_IMG){
            val uploadFile = storageReference.putFile(data!!.data!!)
            val task = uploadFile.continueWithTask{task ->
                if(task.isSuccessful)
                {
                    Toast.makeText(this, "Imagem Carrregada com sucesso!", Toast.LENGTH_SHORT).show()
                }
                storageReference!!.downloadUrl
            }.addOnCompleteListener{task->
                if(task.isSuccessful){
                    val downloadUri = task.result
                    val url = downloadUri!!.toString().substring(0, downloadUri.toString().indexOf("&token"))
                    Log.i("URL da Imagem", url)
                    alertDialog.dismiss()

                    Picasso.get().load(url).into(ivRes)

                }
            }
        }
    }
}