package com.yml.chatapp.ui.home.chats.message

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.yml.chatapp.R
import com.yml.chatapp.common.CONFIRM_IMAGE_REQUEST_CODE
import com.yml.chatapp.common.PICK_IMAGE_REQUEST_CODE
import com.yml.chatapp.common.SET_IMAGE
import com.yml.chatapp.common.STORAGE_PERMISSION_REQUEST_CODE
import com.yml.chatapp.databinding.ActivityChatMessageBinding
import com.yml.chatapp.ui.home.preview.PreviewImage
import com.yml.chatapp.ui.wrapper.User
import com.yml.chatapp.viewmodelfactory.ViewModelFactory
import java.io.ByteArrayOutputStream

class ChatMessageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatMessageBinding
    private lateinit var chatMessageViewModel: ChatMessageViewModel
    private var currentUser: User? = null
    private var foreignUser: User? = null
    private lateinit var dialog: Dialog
    private lateinit var chatMessageAdapter: ChatMessageAdapter
    private lateinit var recyclerView: RecyclerView
    private var offset:Long = 0L
    private var isLoading:Boolean = false
    var visibleItems: Int = 0
    var totalItems: Int = 0
    var firstVisibleItem: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dialog = Dialog(this)
        dialog.setContentView(R.layout.loading_screen)
        dialog.show()
        currentUser = intent.getSerializableExtra("currentUser") as User
        foreignUser = intent.getSerializableExtra("sendUser") as User
        chatMessageViewModel =
            ViewModelProvider(this@ChatMessageActivity, ViewModelFactory(
                ChatMessageViewModel(currentUser!!.fUid, foreignUser!!.fUid)
            ))[ChatMessageViewModel::class.java]
        binding.sendUserName.text = foreignUser!!.name
        setProfilePic()
        sendMessage()
        initRecyclerView()
        allListeners()
        allObservers()
    }

    private fun setProfilePic() {
        if(foreignUser!!.image.isNotEmpty()) {
            Glide.with(this)
                .load(foreignUser!!.image)
                .centerCrop()
                .into(binding.foreignUserImage)
        }
    }

    private fun initRecyclerView() {
        chatMessageAdapter = ChatMessageAdapter(this, chatMessageViewModel.messageList,
            currentUser!!.fUid)
        recyclerView = binding.messageRecyclerView
        val layoutManager =  LinearLayoutManager(this@ChatMessageActivity)
        layoutManager.reverseLayout = true
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = chatMessageAdapter

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                visibleItems = (recyclerView.layoutManager as LinearLayoutManager).childCount
                totalItems = (recyclerView.layoutManager as LinearLayoutManager).itemCount
                firstVisibleItem = (recyclerView.layoutManager as LinearLayoutManager)
                    .findFirstVisibleItemPosition()

                if (!isLoading) {
                    if ((visibleItems + firstVisibleItem) >= totalItems && firstVisibleItem >= 0) {
                        isLoading = true
                        showProgressBar()
                        if (offset != 0L) {
                            Log.d("pagination", "scrolled")
                            getPagedMessage()
                        }
                    }
                }
            }
        })
    }

    private fun showProgressBar() {
        if(isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        }else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun getPagedMessage() {
        chatMessageViewModel.getPagedMessages(foreignUser, currentUser, offset)
    }

    private fun sendMessage() {
        val message = binding.typeMessageText.text.toString()
        if (message.isNotEmpty()) {
            chatMessageViewModel.sendTextMessage(currentUser!!.fUid, foreignUser!!.fUid, message)
            binding.typeMessageText.setText("")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == PICK_IMAGE_REQUEST_CODE && data != null) {
            data.data?.let {
                manageImageData(it)
            }
        }

        if(requestCode == CONFIRM_IMAGE_REQUEST_CODE && data != null) {
            val imageUriString = data.extras?.let {
                it.getString(SET_IMAGE)
            }
            val imageUri = Uri.parse(imageUriString)
            val inputStream = contentResolver.openInputStream(imageUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.WEBP, 100, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            chatMessageViewModel.sendImageMessage(currentUser!!.fUid, foreignUser!!.fUid, byteArray)
        }
    }

    private fun manageImageData(imageUri : Uri) {
        val image = imageUri.toString()
        val intent = Intent(this@ChatMessageActivity, PreviewImage::class.java)
        intent.putExtra(SET_IMAGE, image)
        startActivityForResult(intent, CONFIRM_IMAGE_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == STORAGE_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty()) {
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Log.i("Storage Permission","Permission denied")
            } else {
                fetchImage()
            }
        }
    }

    private fun allListeners() {
        binding.backButton.setOnClickListener {
            finish()
        }

        binding.sendButton.setOnClickListener {
            sendMessage()
        }

        binding.sendImage.setOnClickListener {
            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                fetchImage()
            }else {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), STORAGE_PERMISSION_REQUEST_CODE)
            }
        }

        binding.foreignUserImage.setOnClickListener {
            val intent = Intent(this, ViewProfileActivity::class.java)
            intent.putExtra("foreignUser", foreignUser)
            startActivity(intent)
        }
    }

    private fun fetchImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE)
    }

    private fun allObservers() {
        chatMessageViewModel.getChatStatus.observe(this@ChatMessageActivity) {
            if(it != null) {
                if(chatMessageViewModel.messageList.size != 0 ) {
                    isLoading = false
                    showProgressBar()
                    offset = chatMessageViewModel.messageList[chatMessageViewModel.messageList.size - 1].dateCreated
                    chatMessageAdapter.notifyDataSetChanged()
                    dialog.dismiss()
                }
            }
        }

        chatMessageViewModel.sendTextMessageStatus.observe(this@ChatMessageActivity) {
            Log.i("ChatMessageNotification","${it.content}")
            chatMessageViewModel.sendNotification(foreignUser!!.firebaseToken, currentUser!!.name, it.content,it.content, it.contentType)
        }

        chatMessageViewModel.getPagedMessageStatus.observe(this@ChatMessageActivity) { list ->
            isLoading = false
            showProgressBar()
            for(i in list) {
                chatMessageViewModel.messageList.add(i)
                offset = i.dateCreated
            }
            chatMessageAdapter.notifyDataSetChanged()
        }
    }
}