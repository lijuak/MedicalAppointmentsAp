package com.medicalapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.medicalapp.R
import com.medicalapp.model.Cita

// 1. Interfaz para comunicar acciones a la Activity
interface CitaActionListener {
    fun onEditarCita(cita: Cita)
    fun onEliminarCita(cita: Cita, position: Int)
}

class CitasAdapter(
    private var citas: MutableList<Cita>, // Convertido a MutableList
    private val listener: CitaActionListener
) : RecyclerView.Adapter<CitasAdapter.CitaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CitaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cita, parent, false)
        return CitaViewHolder(view)
    }

    override fun onBindViewHolder(holder: CitaViewHolder, position: Int) {
        holder.bind(citas[position], listener)
    }

    override fun getItemCount(): Int = citas.size

    fun updateData(newCitas: List<Cita>) {
        citas.clear()
        citas.addAll(newCitas)
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        if (position >= 0 && position < citas.size) {
            citas.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    inner class CitaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitulo: TextView = itemView.findViewById(R.id.tvTituloCita)
        private val tvDescripcion: TextView = itemView.findViewById(R.id.tvDescripcionCita)
        private val tvDinero: TextView = itemView.findViewById(R.id.tvChipDinero)
        private val tvIntensidad: TextView = itemView.findViewById(R.id.tvChipIntensidad)
        private val tvCercania: TextView = itemView.findViewById(R.id.tvChipCercania)
        private val tvTemporada: TextView = itemView.findViewById(R.id.tvChipTemporada)
        private val ivMenu: ImageView = itemView.findViewById(R.id.ivMenuCita)

        fun bind(cita: Cita, listener: CitaActionListener) {
            tvTitulo.text = cita.titulo
            tvDescripcion.text = cita.descripcion
            tvDinero.text = "€ ${mapDinero(cita.dinero)}"
            tvIntensidad.text = mapIntensidad(cita.intensidad)
            tvCercania.text = mapCercania(cita.cercania)
            tvTemporada.text = mapTemporada(cita.temporada)

            // 2. Listener para el icono del menú
            ivMenu.setOnClickListener { view ->
                showPopupMenu(view, cita, listener)
            }
        }

        private fun showPopupMenu(view: View, cita: Cita, listener: CitaActionListener) {
            val popup = PopupMenu(view.context, view)
            popup.inflate(R.menu.menu_cita_item)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_editar_cita -> {
                        listener.onEditarCita(cita)
                        true
                    }
                    R.id.menu_eliminar_cita -> {
                        listener.onEliminarCita(cita, adapterPosition)
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }

        // --- Funciones de mapeo (sin cambios) ---
        private fun mapDinero(valor: Int?): String = when (valor) { 1 -> "Bajo"; 2 -> "Medio"; 3 -> "Alto"; else -> "N/A" }
        private fun mapIntensidad(valor: Int?): String = when (valor) { 1 -> "Tranqui"; 2 -> "Normal"; 3 -> "Intenso"; else -> "N/A" }
        private fun mapCercania(valor: Int?): String = when (valor) { 1 -> "Cerca"; 2 -> "Normal"; 3 -> "Lejos"; else -> "N/A" }
        private fun mapTemporada(valor: Int?): String = when (valor) { 1 -> "Baja"; 2 -> "Media"; 3 -> "Alta"; else -> "N/A" }
    }
}
