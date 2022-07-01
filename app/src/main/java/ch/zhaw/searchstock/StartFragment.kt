package ch.zhaw.searchstock

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import ch.zhaw.searchstock.databinding.FragmentStartBinding
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.beust.klaxon.Klaxon
import kotlin.random.Random

class StartFragment : Fragment() {

    private var _binding: FragmentStartBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var adapter: ImageAdapter? = null
    private var data : MutableList<ImageEntry> = mutableListOf()

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


                adapter = ImageAdapter(data, requireContext())
                val requestQueue = Volley.newRequestQueue(requireContext())

                val request = StringRequest(
                    Request.Method.GET, urlSearch,
                    Response.Listener<String> { response ->
                        val results = Klaxon().parse<Results>(response)
                        for (i in 0..9) {
                            data.add(results!!.results.get(i))
                        }
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

        binding.buttonShow.setOnClickListener {
            val id = Random.nextInt(0,9)
            val bundle = bundleOf("PHOTO_ID" to data.get(id).urls.small)
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment, bundle)
        }
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