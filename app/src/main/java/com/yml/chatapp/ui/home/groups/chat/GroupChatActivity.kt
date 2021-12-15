package com.yml.chatapp.ui.home.groups.chat

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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yml.chatapp.R
import com.yml.chatapp.common.*
import com.yml.chatapp.databinding.ActivityGroupChatBinding
import com.yml.chatapp.ui.home.preview.PreviewImage
import com.yml.chatapp.ui.wrapper.Group
import com.yml.chatapp.viewmodelfactory.ViewModelFactory
import java.io.ByteArrayOutputStream


class GroupChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGroupChatBinding
    private lateinit var dialog: Dialog
    private lateinit var groupChatViewModel: GroupChatViewModel
    private lateinit var groupChatAdapter: GroupChatAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var senderId: String
    private var group: Group? = null
    private var offset:Long = 0L
    private var isLoading:Boolean = false
    var visibleItems: Int = 0
    var totalItems: Int = 0
    var firstVisibleItem: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dialog = Dialog(this)
        dialog.setContentView(R.layout.loading_screen)
        dialog.show()
        senderId = SharedPref.getInstance(this).getUserId().toString()
        group = intent.getSerializableExtra(GROUP) as Group
        groupChatViewModel = ViewModelProvider(
            this,
            ViewModelFactory(
                GroupChatViewModel(group!!)
            )
        )[GroupChatViewModel::class.java]
        binding.groupName.text = group!!.groupName
        initRecyclerView()
        allListeners()
        allObservers()
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
            group?.let { groupChatViewModel.setGroupImageToDb(senderId, byteArray, it) }
        }
    }

    private fun manageImageData(imageUri : Uri) {
        val image = imageUri.toString()
        val intent = Intent(this@GroupChatActivity, PreviewImage::class.java)
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
            sendMessageInGroup()
        }

        binding.sendImage.setOnClickListener {
            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                fetchImage()
            }else {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), STORAGE_PERMISSION_REQUEST_CODE)
            }
        }
    }

    private fun fetchImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE)
    }

    private fun allObservers() {
        groupChatViewModel.getSenderNameStatus.observe(this) {
            initRecyclerView()
            dialog.dismiss()
        }

        groupChatViewModel.getGroupChatStatus.observe(this) {
            if(this::groupChatAdapter.isInitialized) {
                if(groupChatViewModel.messageList.size != 0) {
                    isLoading = false
                    showProgressBar()
                    offset = groupChatViewModel.messageList[groupChatViewModel.messageList.size - 1].dateCreated
                    groupChatAdapter.notifyDataSetChanged()
                }
            }
        }

        groupChatViewModel.sendGroupMessageStatus.observe(this) {
            groupChatViewModel.sendGroupNotification(group!!.groupName, it)
        }

        groupChatViewModel.getGroupPagedStatus.observe(this) { list ->
            isLoading = false
            showProgressBar()
            Log.i("Group Message", "${list.size}")
            for(i in list) {
                groupChatViewModel.messageList.add(i)
                offset = i.dateCreated
            }
            groupChatAdapter.notifyDataSetChanged()
        }
    }

    private fun showProgressBar() {
        if(isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        }else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun initRecyclerView() {
        groupChatAdapter = GroupChatAdapter(
            this,
            groupChatViewModel.messageList,
            senderId,
            groupChatViewModel.memberList
        )
        recyclerView = binding.groupMessageRecyclerView
        val layoutManager =  LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = groupChatAdapter

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
                            getPagedGroupMessages()
                        }
                    }
                }
            }
        })
    }

    private fun getPagedGroupMessages() {
        group?.let { groupChatViewModel.getPagedGroupMessage(it, offset) }
    }

    private fun sendMessageInGroup() {
        val message = binding.typeMessageText.text.toString()
        if (message.isNotEmpty()) {
            group?.let { groupChatViewModel.setGroupMessageToDb(senderId, message, it) }
            binding.typeMessageText.setText("")
        } else {
            Toast.makeText(this, "Enter a text", Toast.LENGTH_LONG).show()
        }
    }
}