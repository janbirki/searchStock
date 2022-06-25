package ch.zhaw.searchstock

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import ch.zhaw.searchstock.databinding.ImagesBoardBinding

class ImageAdapter(var images: List<ImageEntry>, val context: Context) : BaseAdapter() {

    var layoutInflater : LayoutInflater
    private var _binding: ImagesBoardBinding? = null
    private val binding get() = _binding!!
    private var bindings = mutableMapOf<View,ImagesBoardBinding>()
    init {
        layoutInflater = LayoutInflater.from(context)
    }

    override fun getCount(): Int { //number of elements to display
        return images.size}

    override fun getItem(index: Int): ImageEntry { //item at index
        return images.get(index)}

    override fun getItemId(index: Int): Long { //itemId for index
        return index.toLong()
    }

    override fun getView(index: Int, oldView: View?,
                         viewGroup: ViewGroup?): View {

        var view : View
        if (oldView == null) { //check if we get a view to recycle
            _binding = ImagesBoardBinding.inflate(layoutInflater, viewGroup, false)
            view = binding.root;bindings[binding.root] = binding
        } else { //if yes, use the oldview
            view = oldView
            _binding = bindings[view]
        }
        val image = getItem(index) //get the data for this index
        binding.altDescription.text = image.alt_description

        return view
    }
}