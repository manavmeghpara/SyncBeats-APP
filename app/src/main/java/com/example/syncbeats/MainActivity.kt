package com.example.syncbeats

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.nio.ByteBuffer
import java.util.UUID


class MainActivity : AppCompatActivity() {

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bluetoothLeScanner : BluetoothLeScanner

    private var scanEnd : Boolean = false
    private var TAG :  String = "MainActivity"

    private lateinit var characteristic: BluetoothGattCharacteristic
    private lateinit var gatt: BluetoothGatt

    private lateinit var bt_play : Button
    private lateinit var bt_stop : Button
    private lateinit var bt_prev : Button
    private lateinit var bt_next : Button
    private lateinit var bt_volUp : Button
    private lateinit var bt_volDown : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bt_play = findViewById(R.id.btnPlay)
        bt_next = findViewById(R.id.btnNext)
        bt_prev = findViewById(R.id.btnPrevious)
        bt_stop = findViewById(R.id.btnStop)
        bt_volUp = findViewById(R.id.btnVolumeUp)
        bt_volDown = findViewById(R.id.btnVolumeDown)

        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION )!= PackageManager.PERMISSION_GRANTED)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                requestPermissions(arrayOf( android.Manifest.permission.ACCESS_FINE_LOCATION), 1)
            }
        }

        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_SCAN )!= PackageManager.PERMISSION_GRANTED)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                requestPermissions(arrayOf( android.Manifest.permission.BLUETOOTH_SCAN), 1)
            }
        }
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT )!= PackageManager.PERMISSION_GRANTED)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                requestPermissions(arrayOf( android.Manifest.permission.BLUETOOTH_CONNECT), 1)
            }
        }

        bt_stop.setOnClickListener{
            val data = ("1\r\n").toByteArray()
            characteristic.setValue(data)
            gatt.writeCharacteristic(characteristic);
        }
        bt_play.setOnClickListener{
            val data = ("0\r\n").toByteArray()
            characteristic.setValue(data)
            gatt.writeCharacteristic(characteristic);
        }
        bt_prev.setOnClickListener {
            val data = ("5\r\n").toByteArray()
            characteristic.setValue(data)
            gatt.writeCharacteristic(characteristic);
        }
        bt_next.setOnClickListener{
            val data = ("4\r\n").toByteArray()
            characteristic.setValue(data)
            gatt.writeCharacteristic(characteristic);
        }
        bt_volUp.setOnClickListener{
            val data = ("2\r\n").toByteArray()
            characteristic.setValue(data)
            gatt.writeCharacteristic(characteristic);
        }
        bt_volDown.setOnClickListener{
            val data = ("3\r\n").toByteArray()
            characteristic.setValue(data)
            gatt.writeCharacteristic(characteristic);
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            scanLeDevice();
        }
    }

    @SuppressLint("MissingPermission")
    private fun scanLeDevice(){
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner

        if (!scanEnd){
            bluetoothLeScanner.startScan(leScanCallback)
        }
        else{
            bluetoothLeScanner.stopScan((leScanCallback))
        }
    }

    private val leScanCallback: ScanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            if(!scanEnd){
                if(result.device.name == "Slave") {
                    Log.i(TAG, "onScanResult: "+result.device.address+":"+result.device.name)
                    gatt= result.device.connectGatt(this@MainActivity, false, gattCallback)
                    Thread.sleep(700)
                }

            }
        }
    }

    private val gattCallback: BluetoothGattCallback = object : BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            // Handle connection state changes here
            if(status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    gatt.discoverServices()
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                val serviceUuid = UUID.fromString("0000FFE0-0000-1000-8000-00805F9B34FB")
                val characteristicUuid = UUID.fromString("0000FFE1-0000-1000-8000-00805F9B34FB")
                val service = gatt?.getService(serviceUuid)
                if (service != null) {
                    characteristic = service.getCharacteristic(characteristicUuid)
                    if (characteristic != null) {
                        // ... send data using the characteristic

                    } else {
                        Log.d("Character ", "$characteristic")

                    }
                } else {
                    Log.d("Services ", "$service")
                }
            }
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            // Handle characteristic reads here
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            // Handle characteristic writes here
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            // Handle characteristic changes here
        }
    }

}
