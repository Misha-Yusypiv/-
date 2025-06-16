package com.yourappname.petapp.presentation.documents

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.yourappname.petapp.R
import com.yourappname.petapp.domain.model.VeterinaryPassport

class VeterinaryPassportAdapter : ListAdapter<VeterinaryPassport, VeterinaryPassportAdapter.PassportViewHolder>(DiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PassportViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_veterinary_passport, parent, false)
        return PassportViewHolder(view)
    }

    override fun onBindViewHolder(holder: PassportViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PassportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvPassportTitle)
        private val tvPetName: TextView = itemView.findViewById(R.id.tvPetName)
        private val tvOwner: TextView = itemView.findViewById(R.id.tvOwner)
        private val tvBirthDate: TextView = itemView.findViewById(R.id.tvBirthDate)
        private val tvId: TextView = itemView.findViewById(R.id.tvPassportId)
        private val ivPet: ImageView = itemView.findViewById(R.id.ivPet)

        fun bind(passport: VeterinaryPassport) {
            tvTitle.text = "Ветеринарний паспорт України"
            tvPetName.text = passport.petName
            tvOwner.text = "Власник: ${passport.ownerName}"
            tvBirthDate.text = "Дата народження: ${passport.petBirthDate}"
            tvId.text = "ID: ${passport.id}"
            Glide.with(itemView).load(passport.petImageUrl).placeholder(R.drawable.ic_launcher_foreground).into(ivPet)
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<VeterinaryPassport>() {
        override fun areItemsTheSame(oldItem: VeterinaryPassport, newItem: VeterinaryPassport): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: VeterinaryPassport, newItem: VeterinaryPassport): Boolean = oldItem == newItem
    }
} 