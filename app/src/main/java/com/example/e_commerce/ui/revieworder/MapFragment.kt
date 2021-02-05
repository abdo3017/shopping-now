package com.example.e_commerce.ui.revieworder

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.e_commerce.R
import com.example.e_commerce.databinding.FragmentMapBinding
import com.example.e_commerce.databinding.MapBottomSheetBinding
import com.example.e_commerce.datasource.models.Orders
import com.example.e_commerce.state.DataState
import com.example.e_commerce.ui.base.BaseFragment
import com.example.e_commerce.utils.Constants
import com.example.e_commerce.utils.CustomProgressDialogue
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import java.util.*


@InternalCoroutinesApi
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MapFragment :
    BaseFragment<FragmentMapBinding, ReviewOrderViewModel>(false), SearchView.OnQueryTextListener,
    com.google.android.gms.maps.OnMapReadyCallback {
    private val homeViewModel: ReviewOrderViewModel by viewModels()
    var viewDataBinding: MapBottomSheetBinding? = null
    var latitude: Double? = 0.0
    private lateinit var progress: CustomProgressDialogue
    var longitude: Double? = 0.0
    lateinit var marker: Marker
    lateinit var orders: Orders
    private lateinit var city: String
    private lateinit var address: String
    private var maps: GoogleMap? = null

    private lateinit var geocoder: Geocoder
    var changeLocation = false
    private lateinit var bottom: BottomSheetDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        setViews(inflater, container, savedInstanceState)
        getCurrentOrder()
        observeData()
        onClick()
        return getMRootView()
    }

    override fun getViewModel() = homeViewModel

    private fun setViews(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) {
        viewDataBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.map_bottom_sheet,
            container,
            false
        )
        bottom = BottomSheetDialog(requireContext())
        bottom.setContentView(viewDataBinding!!.root)
        getViewDataBinding().lifecycleOwner = this
        getViewDataBinding().btnSearch.setOnQueryTextListener(this)
        getViewDataBinding().mapView.onCreate(savedInstanceState)
        getViewDataBinding().mapView.getMapAsync(this)
        progress = CustomProgressDialogue(requireContext())
    }

    override
    val layoutId: Int
        get() = R.layout.fragment_map

    override
    val bindingVariableId: Int
        get() = 0

    override
    val bindingVariableValue: Any
        get() = getViewModel()

    private fun onClick() {
        getViewDataBinding().btnLocation.setOnClickListener {
            viewDataBinding!!.addressName = address
            viewDataBinding!!.cityName = city
            bottom.show()
        }
        viewDataBinding!!.byButton.setOnClickListener {
            orders.submitted = true
            orders.orderDate = Constants.getCurrentDate()
            orders.address = "$latitude,$longitude"
            updateOrder(orders)
        }
    }

    private fun getCurrentOrder() {
        lifecycleScope.launch {
            getViewModel().getCurrentOrder()
        }
    }


    private fun updateOrder(orders: Orders) {
        lifecycleScope.launch {
            getViewModel().updateOrder(orders)
        }
    }


    @SuppressLint("SetTextI18n")
    private fun observeData() {
        getViewModel().dataStateOrder.observe(viewLifecycleOwner, {
            when (it) {
                is DataState.Loading -> {

                }
                is DataState.Success<Orders> -> {
                    orders = it.data
                }
                is DataState.Error<*> -> {

                }
            }
        })
        getViewModel().dataStateUpdateOrder.observe(viewLifecycleOwner, {
            when (it) {
                is DataState.Loading -> {
                    progress.show()
                    bottom.dismiss()
                }
                is DataState.Success<Orders> -> {
                    progress.dismiss()
                    findNavController().navigate(MapFragmentDirections.actionMapFragmentToHomeFragment())
                }
                is DataState.Error<*> -> {

                }
            }
        })

    }

    override fun onMapReady(p0: GoogleMap?) {
        maps = p0
        maps!!.uiSettings.isMyLocationButtonEnabled = true
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            setPermission()
        } else
            maps!!.isMyLocationEnabled = true
        maps!!.setOnMapClickListener {
            changeLocation = true
            try {
                geocoder = Geocoder(requireContext(), Locale.getDefault())
                val addresses: List<Address> = geocoder.getFromLocation(
                    it.latitude, it.longitude, 1
                )
                Log.d("rerererer", addresses[0].toString())
                address = addresses[0].getAddressLine(0)
                city = addresses[0].locality
                updateLocation(it)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        maps!!.setOnMyLocationButtonClickListener {
            changeLocation = false
            true
        }
        maps!!.setOnMyLocationChangeListener {
            if (!changeLocation) {
                latitude = it.latitude
                longitude = it.longitude
                try {
                    geocoder = Geocoder(requireContext(), Locale.getDefault())
                    val addresses: List<Address> = geocoder.getFromLocation(
                        latitude!!, longitude!!, 1
                    )
                    address = addresses[0].getAddressLine(0)
                    city = addresses[0].locality
                    updateLocation(LatLng(latitude!!, longitude!!))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun getLocationFromAddress(query: String): LatLng? {
        try {
            geocoder = Geocoder(requireContext())
            val address = geocoder.getFromLocationName(query, 5)
            if (address != null) {
                address.forEach {
                    Log.d("rerererer", it.toString())

                }
                val location = address[0]
                return LatLng(location.latitude, location.longitude)
            }
        } catch (e: Exception) {
        }
        return null
    }

    private fun updateLocation(location: LatLng) {
        if (this::marker.isInitialized)
            marker.remove()
        marker = maps!!.addMarker(MarkerOptions().position(location))
        marker.title = address
        marker.showInfoWindow()
        val cameraPosition: CameraPosition =
            CameraPosition.builder().target(location).zoom(20F).build()
        maps!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. Continue the action or workflow in your
                // app.
            } else {
                // Explain to the user that the feature is unavailable because the
                // features requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
            }
        }

    @SuppressLint("MissingPermission")
    private fun setPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                1
            )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        getViewDataBinding().mapView.onSaveInstanceState(outState)
    }


    override fun onStart() {
        super.onStart()
        getViewDataBinding().mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        getViewDataBinding().mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        getViewDataBinding().mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        getViewDataBinding().mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        getViewDataBinding().mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        getViewDataBinding().mapView.onDestroy()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        Log.d("rerererer", "mmmmmmmmm")
        val location = getLocationFromAddress(query!!)
        if (location != null) {
            marker.remove()
            updateLocation(location)
            return true
        }
        Log.d("rerererer", "heheheh")
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }

    override fun getBackPressed(): Boolean {
        findNavController().popBackStack()
        return true
    }

}