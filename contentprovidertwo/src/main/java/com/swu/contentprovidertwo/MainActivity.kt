package com.swu.contentprovidertwo

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentProviderOperation
import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.OperationApplicationException
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.RemoteException
import android.provider.ContactsContract
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity(), View.OnClickListener {
    
    // 视图组件
    private lateinit var btnSimulate: Button
    private lateinit var btnReal: Button
    private lateinit var btnAdd: Button
    private lateinit var btnQuery: Button
    private lateinit var btnUpdate: Button
    private lateinit var btnDelete: Button
    private lateinit var btnQueryAll: Button
    private lateinit var btnClear: Button
    private lateinit var etName: EditText
    private lateinit var etPhone: EditText
    private lateinit var etId: EditText
    private lateinit var tvMode: TextView
    private lateinit var listView: ListView
    
    // 数据
    private lateinit var adapter: ArrayAdapter<String>
    private val simulateContactsList = mutableListOf<ContactItem>()
    private var isSimulateMode = true // true: 模拟数据模式, false: 真实数据模式
    
    companion object {
        private const val REQUEST_CODE_CONTACTS = 1
    }
    
    // 联系人数据类
    data class ContactItem(
        val id: String,
        val name: String,
        val phone: String
    ) {
        override fun toString(): String = "$name\t$phone"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        initViews()
        initData()
    }
    
    /**
     * 初始化视图组件
     */
    private fun initViews() {
        btnSimulate = findViewById(R.id.btn_simulate)
        btnReal = findViewById(R.id.btn_real)
        btnAdd = findViewById(R.id.btn_add)
        btnQuery = findViewById(R.id.btn_query)
        btnUpdate = findViewById(R.id.btn_update)
        btnDelete = findViewById(R.id.btn_delete)
        btnQueryAll = findViewById(R.id.btn_query_all)
        btnClear = findViewById(R.id.btn_clear)
        etName = findViewById(R.id.et_name)
        etPhone = findViewById(R.id.et_phone)
        etId = findViewById(R.id.et_id)
        tvMode = findViewById(R.id.tv_mode)
        listView = findViewById(R.id.list_view)
        
        // 设置点击监听
        btnSimulate.setOnClickListener(this)
        btnReal.setOnClickListener(this)
        btnAdd.setOnClickListener(this)
        btnQuery.setOnClickListener(this)
        btnUpdate.setOnClickListener(this)
        btnDelete.setOnClickListener(this)
        btnQueryAll.setOnClickListener(this)
        btnClear.setOnClickListener(this)
        
        // 初始化列表
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf<String>())
        listView.adapter = adapter
    }
    
    /**
     * 初始化模拟数据
     */
    private fun initData() {
        simulateContactsList.clear()
        simulateContactsList.addAll(listOf(
            ContactItem("1", "吴明涛", "13800138000"),
            ContactItem("2", "李四", "13900139000"),
            ContactItem("3", "孙八", "15000150000"),
            ContactItem("4", "郑十一", "15100151000"),
            ContactItem("5", "钱七", "15200152000"),
            ContactItem("6", "老二", "15300153000"),
            ContactItem("7", "周九", "15400154000"),
            ContactItem("8", "吴十", "15500155000"),
            ContactItem("9", "老七", "15600156000"),
            ContactItem("10", "十四", "15700157000")
        ))
        switchToSimulateMode()
    }
    
    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_simulate -> switchToSimulateMode()
            R.id.btn_real -> switchToRealMode()
            R.id.btn_add -> addContact()
            R.id.btn_query -> queryContact()
            R.id.btn_update -> updateContact()
            R.id.btn_delete -> deleteContact()
            R.id.btn_query_all -> queryAllContacts()
            R.id.btn_clear -> clearInputs()
        }
    }
    
    /**
     * 切换到模拟数据模式
     */
    private fun switchToSimulateMode() {
        isSimulateMode = true
        tvMode.text = "当前模式：模拟数据"
        queryAllContacts()
        Toast.makeText(this, "已切换到模拟数据模式", Toast.LENGTH_SHORT).show()
    }
    
    /**
     * 切换到真实数据模式
     */
    private fun switchToRealMode() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS),
                REQUEST_CODE_CONTACTS
            )
        } else {
            isSimulateMode = false
            tvMode.text = "当前模式：真实数据"
            queryAllContacts()
            Toast.makeText(this, "已切换到真实数据模式", Toast.LENGTH_SHORT).show()
        }
    }
    
    /**
     * 添加联系人
     */
    private fun addContact() {
        val name = etName.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        
        if (name.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "请填写姓名和电话号码", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (isSimulateMode) {
            // 模拟数据：添加到列表
            val newId = (simulateContactsList.maxOfOrNull { it.id.toIntOrNull() ?: 0 } ?: 0) + 1
            simulateContactsList.add(ContactItem(newId.toString(), name, phone))
            queryAllContacts()
            Toast.makeText(this, "添加成功，ID: $newId", Toast.LENGTH_SHORT).show()
        } else {
            // 真实数据：使用 ContentProvider 添加
            addRealContact(name, phone)
        }
        
        clearInputs()
    }
    
    /**
     * 添加真实联系人
     */
    private fun addRealContact(name: String, phone: String) {
        try {
            val ops = arrayListOf<ContentProviderOperation>()
            
            // 插入联系人
            ops.add(
                ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                    .build()
            )
            
            // 插入姓名
            ops.add(
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                    .build()
            )
            
            // 插入电话号码
            ops.add(
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                    .build()
            )
            
            contentResolver.applyBatch(ContactsContract.AUTHORITY, ops)
            queryAllContacts()
            Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "添加失败: ${e.message}", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }
    
    /**
     * 查询联系人
     */
    private fun queryContact() {
        val id = etId.text.toString().trim()
        
        if (id.isEmpty()) {
            Toast.makeText(this, "请输入要查询的联系人ID", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (isSimulateMode) {
            // 模拟数据：从列表查询
            val contact = simulateContactsList.find { it.id == id }
            if (contact != null) {
                etName.setText(contact.name)
                etPhone.setText(contact.phone)
                Toast.makeText(this, "查询成功：${contact.name} - ${contact.phone}", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "未找到ID为 $id 的联系人", Toast.LENGTH_SHORT).show()
            }
        } else {
            // 真实数据：使用 ContentProvider 查询
            queryRealContact(id)
        }
    }
    
    /**
     * 查询真实联系人
     */
    @SuppressLint("Range")
    private fun queryRealContact(id: String) {
        try {
            val contactId = id.toLong()
            val cursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                arrayOf(
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER
                ),
                "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
                arrayOf(contactId.toString()),
                null
            )
            
            cursor?.use {
                if (it.moveToFirst()) {
                    val name = it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                    val phone = it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    etName.setText(name)
                    etPhone.setText(phone)
                    Toast.makeText(this, "查询成功：$name - $phone", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "未找到ID为 $id 的联系人", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, "查询失败: ${e.message}", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }
    
    /**
     * 更新联系人
     */
    private fun updateContact() {
        val id = etId.text.toString().trim()
        val name = etName.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        
        if (id.isEmpty()) {
            Toast.makeText(this, "请输入要更新的联系人ID", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (name.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "请填写姓名和电话号码", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (isSimulateMode) {
            // 模拟数据：更新列表
            val index = simulateContactsList.indexOfFirst { it.id == id }
            if (index != -1) {
                simulateContactsList[index] = ContactItem(id, name, phone)
                queryAllContacts()
                Toast.makeText(this, "更新成功", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "未找到ID为 $id 的联系人", Toast.LENGTH_SHORT).show()
            }
        } else {
            // 真实数据：使用 ContentProvider 更新
            updateRealContact(id, name, phone)
        }
        
        clearInputs()
    }
    
    /**
     * 更新真实联系人
     */
    private fun updateRealContact(id: String, name: String, phone: String) {
        try {
            val contactId = id.toLong()
            val ops = arrayListOf<ContentProviderOperation>()
            
            // 更新姓名
            ops.add(
                ContentProviderOperation.newUpdate(
                    ContactsContract.Data.CONTENT_URI
                )
                    .withSelection(
                        "${ContactsContract.Data.CONTACT_ID} = ? AND ${ContactsContract.Data.MIMETYPE} = ?",
                        arrayOf(contactId.toString(), ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    )
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                    .build()
            )
            
            // 更新电话号码
            ops.add(
                ContentProviderOperation.newUpdate(
                    ContactsContract.Data.CONTENT_URI
                )
                    .withSelection(
                        "${ContactsContract.Data.CONTACT_ID} = ? AND ${ContactsContract.Data.MIMETYPE} = ?",
                        arrayOf(contactId.toString(), ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    )
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
                    .build()
            )
            
            contentResolver.applyBatch(ContactsContract.AUTHORITY, ops)
            queryAllContacts()
            Toast.makeText(this, "更新成功", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "更新失败: ${e.message}", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }
    
    /**
     * 删除联系人
     */
    private fun deleteContact() {
        val id = etId.text.toString().trim()
        
        if (id.isEmpty()) {
            Toast.makeText(this, "请输入要删除的联系人ID", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (isSimulateMode) {
            // 模拟数据：从列表删除
            val removed = simulateContactsList.removeIf { it.id == id }
            if (removed) {
                queryAllContacts()
                Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "未找到ID为 $id 的联系人", Toast.LENGTH_SHORT).show()
            }
        } else {
            // 真实数据：使用 ContentProvider 删除
            deleteRealContact(id)
        }
        
        clearInputs()
    }
    
    /**
     * 删除真实联系人
     */
    private fun deleteRealContact(id: String) {
        try {
            val contactId = id.toLong()
            val uri = ContentUris.withAppendedId(ContactsContract.RawContacts.CONTENT_URI, contactId)
            val deleted = contentResolver.delete(uri, null, null)
            
            if (deleted > 0) {
                queryAllContacts()
                Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "未找到ID为 $id 的联系人", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "删除失败: ${e.message}", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }
    
    /**
     * 查询所有联系人
     */
    private fun queryAllContacts() {
        if (isSimulateMode) {
            // 模拟数据：显示列表
            val displayList = simulateContactsList.map { "ID:${it.id} - ${it.name}\t${it.phone}" }
            adapter.clear()
            adapter.addAll(displayList)
            adapter.notifyDataSetChanged()
        } else {
            // 真实数据：使用 ContentProvider 查询
            queryAllRealContacts()
        }
    }
    
    /**
     * 查询所有真实联系人
     */
    @SuppressLint("Range")
    private fun queryAllRealContacts() {
        val displayList = mutableListOf<String>()
        var cursor: android.database.Cursor? = null
        
        try {
            cursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                arrayOf(
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER
                ),
                null, null,
                "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} DESC"
            )
            
            cursor?.let {
                while (it.moveToNext()) {
                    val id = it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID))
                    val name = it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                    val phone = it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    displayList.add("ID:$id - $name\t$phone")
                }
            }
            
            adapter.clear()
            adapter.addAll(displayList)
            adapter.notifyDataSetChanged()
            
            Toast.makeText(this, "已加载 ${displayList.size} 条数据", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "查询失败: ${e.message}", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        } finally {
            cursor?.close()
        }
    }
    
    /**
     * 清空输入框
     */
    private fun clearInputs() {
        etId.setText("")
        etName.setText("")
        etPhone.setText("")
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_CONTACTS -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    isSimulateMode = false
                    tvMode.text = "当前模式：真实数据"
                    queryAllContacts()
                    Toast.makeText(this, "已切换到真实数据模式", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "需要联系人权限才能使用真实数据功能", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
