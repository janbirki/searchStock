package ch.zhaw.searchstock

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import ch.zhaw.searchstock.databinding.FragmentStartBinding
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.beust.klaxon.Klaxon

class StartFragment : Fragment() {

    private var _binding: FragmentStartBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentStartBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //TODO: error handling if no pictures are coming back
        //TODO: check for spaces in search input and add "-" in between
        //TODO: create method for url creation

        // Beispiel query with key
        //https://api.unsplash.com/search/photos?query=canada&client_id=hu_yKrF9g21PFsMUQLh7VMwcDoIgh9s_eBn4Szi_xsI

        binding.searchInput.setOnClickListener() {
            binding.searchInput.text.clear()
        }

        binding.buttonSearch.setOnClickListener {
            hideKeyboard()

            val searchText = binding.searchInput.text
            if (searchText.isNotEmpty()) {
                // create url
                val urlSearch = "https://api.unsplash.com/search/photos?query=" + searchText + "&client_id=hu_yKrF9g21PFsMUQLh7VMwcDoIgh9s_eBn4Szi_xsI"

                val requestQueue = Volley.newRequestQueue(requireContext())

                val request = StringRequest(
                    Request.Method.GET, urlSearch,
                    Response.Listener<String> { response ->
                        val results = Klaxon().parse<Results>(response)
                        val adapter = ImageAdapter(results!!.results, requireContext())
                        binding.imagesList.adapter = adapter
                    },
                    Response.ErrorListener {error ->
                        println(error.toString())
                        //Toast.makeText(this@StartFragment, error, Toast.LENGTH_SHORT).show()
                    })
                //add the call to the request queue
                requestQueue.add(request)
            }
        }

/*
        val imageURL = "https://images.unsplash.com/photo-1416339306562-f3d12fefd36f?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=1080&fit=max&s=92f3e02f63678acc8416d044e189f515"
        var image: Bitmap? = null
        try {
            val `in` = java.net.URL(imageURL).openStream()
            image = BitmapFactory.decodeStream(`in`)
            binding.imageView.setImageBitmap(image)
        }
        catch (e: Exception) {
            Log.e("Error Message", e.message.toString())
            e.printStackTrace()
        }

 */
    }

    fun hideKeyboard() {
        val manager: InputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE)
                    as InputMethodManager
        if (manager != null) {
            manager.hideSoftInputFromWindow(
                requireActivity()
                    .findViewById<View>(android.R.id.content).windowToken, 0)
        }
        binding.searchInput.clearFocus()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}



class Results(val results: List<ImageEntry>)

class ImageEntry(val alt_description: String, val urls: UrlEntry)

class UrlEntry(val small: String)