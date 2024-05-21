package com.devapps.questionsoccer.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.load
import com.devapps.questionsoccer.R
import com.devapps.questionsoccer.databinding.ItemCountryBinding
import com.devapps.questionsoccer.items.ItemCountry

class CountryAdapter(var responseCountries: List<ItemCountry>) : RecyclerView.Adapter<CountryAdapter.CountryViewHolder>() {

    class CountryViewHolder(view:View):RecyclerView.ViewHolder(view){
        private val binding = ItemCountryBinding.bind(view)

        fun bind(context: Context, responseCountries: ItemCountry){
            binding.tvCountryName.text = responseCountries.name
            binding.tvCode.text = responseCountries.code

            val imageLoader = ImageLoader.Builder(context)
                .components { add(SvgDecoder.Factory()) }
                .build()
            binding.ivFlag.load(responseCountries.flag, imageLoader) {
                crossfade(true)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return CountryViewHolder(layoutInflater.inflate(R.layout.item_country, parent, false))
    }

    override fun getItemCount(): Int {
        return responseCountries.size
    }

    override fun onBindViewHolder(holder: CountryViewHolder, position: Int) {
        val item = responseCountries[position]
        holder.bind(holder.itemView.context, item)
    }

}