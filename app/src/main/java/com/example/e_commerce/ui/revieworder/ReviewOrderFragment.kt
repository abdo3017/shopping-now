package com.example.e_commerce.ui.revieworder

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.creativityapps.gmailbackgroundlibrary.BackgroundMail
import com.example.e_commerce.R
import com.example.e_commerce.databinding.FragmentReviewOrderBinding
import com.example.e_commerce.datasource.models.Categories
import com.example.e_commerce.datasource.models.OrderDetails
import com.example.e_commerce.datasource.models.Orders
import com.example.e_commerce.datasource.models.Products
import com.example.e_commerce.ui.base.BaseFragment
import com.example.e_commerce.ui.base.ItemClickListener
import com.example.e_commerce.utils.Constants
import com.example.e_commerce.utils.PrefManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch


@InternalCoroutinesApi
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ReviewOrderFragment :
    BaseFragment<FragmentReviewOrderBinding, ReviewOrderViewModel>(false),
    com.google.android.gms.maps.OnMapReadyCallback {
    private val homeViewModel: ReviewOrderViewModel by viewModels()
    private var categories: MutableList<Categories> = mutableListOf()
    private var products: MutableList<Products> = mutableListOf()
    lateinit var orders: Orders
    private var ordersDetails: MutableList<OrderDetails> = mutableListOf()
    var lastProducts: Int = 0
    var latitude: Double? = 0.0
    var longitude: Double? = 0.0
    private var price: Double = 0.0
    lateinit var marker: Marker
    var changeLocation = false
    private lateinit var adapter: ReviewOrderAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        getViewDataBinding().mapView.onCreate(savedInstanceState)
        getViewDataBinding().mapView.getMapAsync(this)
        onClick()
        getCategoriesData()
        getCurrentOrder()
        observeData()
        setViews()
        return getMRootView()
    }


    override fun getViewModel() = homeViewModel

    private fun getCategoriesData() {
        lifecycleScope.launch {
            getViewModel().getAllCategories()
        }
    }

    private fun getAllShoppingProducts(categories: List<Categories>) {
        for (category in categories) {
            getProductsShoppingCartDataByCategory(category)
        }

    }

    private fun getProductsShoppingCartDataByCategory(category: Categories) {
        lifecycleScope.launch {
            getViewModel().getProductsShoppingCartByCategory(category)
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

    private fun setViews() {
        getViewDataBinding().lifecycleOwner = this
        adapter = ReviewOrderAdapter(products, ordersDetails, clickListener())
        getViewDataBinding().rvProducts.adapter = adapter
    }

    private fun clickListener() = ItemClickListener { position: Int, view: View ->

    }

    @SuppressLint("SetTextI18n")
    private fun observeData() {
        getViewModel().dataStateCategories.observe(viewLifecycleOwner, {
            categories = it.toMutableList()
            getAllShoppingProducts(categories)
        })

        getViewModel().dataStateProducts.observe(viewLifecycleOwner, {
            lastProducts++
            ordersDetails = it.map { it.value }.toMutableList()
            products = it.map { it.key }.toMutableList()
            price += products.zip(ordersDetails).toMap().map { it.key.price * it.value.quantity }
                .sum()
            if (lastProducts == 3)
                getViewDataBinding().byButton.text =
                    getViewDataBinding().byButton.text.toString() + " / $price"
            adapter.addItems(products)
            adapter.addOrderDetails(ordersDetails)
        })

        getViewModel().dataStateOrder.observe(viewLifecycleOwner, {
            orders = it
        })

    }

    override val layoutId: Int
        get() = R.layout.fragment_review_order
    override val bindingVariableId: Int
        get() = 0
    override val bindingVariableValue: Any
        get() = getViewModel()


    private fun onClick() {
        getViewDataBinding().btnSetLocation.setOnClickListener {
            getViewDataBinding().showMap = true
        }
        getViewDataBinding().btnLocation.setOnClickListener {
            getViewDataBinding().showMap = false
        }
        getViewDataBinding().byButton.setOnClickListener {
            orders.submitted = true
            orders.orderDate = Constants.getCurrentDate()
            orders.address = "$latitude,$longitude"
            updateOrder(orders)
            kotlin.runCatching {
                sendMail(
                    orders.id!!
                )
                findNavController().navigate(ReviewOrderFragmentDirections.actionReviewOrderFragmentToHomeFragment())
            }.getOrElse {
                Toast.makeText(requireContext(), "Please try Again !!", Toast.LENGTH_LONG).show()
            }
        }

    }

    var maps: GoogleMap? = null
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
            maps!!.isMyLocationEnabled = true
        } else
            maps!!.isMyLocationEnabled = true
        maps!!.setOnMapClickListener {
            changeLocation = true
            updateLocation(it)
        }
        maps!!.setOnMyLocationButtonClickListener {
            changeLocation = false
            true
        }
        maps!!.setOnMyLocationChangeListener {
            if (!changeLocation) {
                latitude = it.latitude
                longitude = it.longitude
                updateLocation(LatLng(latitude!!, longitude!!))
            }
        }
    }

    private fun updateLocation(location: LatLng) {
        if (this::marker.isInitialized)
            marker.remove()
        marker = maps!!.addMarker(MarkerOptions().position(location))
        marker.title = "deliver location"
        marker.showInfoWindow()
        val cameraPosition: CameraPosition =
            CameraPosition.builder().target(location).zoom(20F).build()
        maps!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    private fun sendMail(orderId: String) {
        val recipientList = "abdogazy14@gmail.com"
        val recipients = recipientList.split(",".toRegex()).toTypedArray()
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_EMAIL, recipients)
        intent.putExtra(Intent.EXTRA_SUBJECT, "Order Confirmation")
        intent.putExtra(
            Intent.EXTRA_TEXT,
            "dear " + PrefManager.getCustomer()!!.userName + ",\n" + "Your order $orderId confirmed and you will git it soon"
        )
        intent.type = "message/rfc822"
        //startActivity(Intent.createChooser(intent, PrefManager.getCustomer()!!.userName))

        val username = "myname@gmail.com"
        val password = "mypassword"
        BackgroundMail.newBuilder(requireContext())
            .withUsername("abdogazy14@gmail.com")
            .withPassword("01002585111")
            .withMailto("abdogazy15@gmail.com")
            .withBody("this is the body")
            .withOnSuccessCallback {
                Log.d("heeeeereeeeee", "hahahahhahahaa")
            }
/*

        Thread {
            try {
                val props = Properties()
                props.put("mail.smtp.auth", "true")
                props.put("mail.smtp.starttls.enable", "true")
                props.put("mail.smtp.host", "smtp.gmail.com")
                props.put("mail.smtp.port", "587")
                val session: Session = Session.getInstance(props,
                    object : Authenticator() {
                        protected val passwordAuthentication: javax.mail.PasswordAuthentication?
                            protected get() = PasswordAuthentication(
                                username, password
                            )
                    })
                // TODO Auto-generated method stub
                var message: ConversationActions.Message?=null //= MimeMessage(session)
                message.setFrom(InternetAddress("from-email@gmail.com"))
                message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse("youremail@gmail.com")
                )
                message.setSubject("email")
                message.setText(
                    """HI,

 great"""
                )
                Transport.send(message)
                println("Done")
            } catch (e: MessagingException) {
                throw RuntimeException(e)
            }
        }.start()*/
    }


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

}