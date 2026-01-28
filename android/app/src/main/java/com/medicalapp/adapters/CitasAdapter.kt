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
import java.text.SimpleDateFormat
import java.util.*

// Interfaz para comunicar acciones a la Activity
interface CitaActionListener {
    fun onEditarCita(cita: Cita)
    fun onEliminarCita(cita: Cita, position: Int)
}

class CitasAdapter(
    private var citas: MutableList<Cita>,
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
        private val tvEspecialidad: TextView = itemView.findViewById(R.id.tvEspecialidad)
        private val tvFechaHora: TextView = itemView.findViewById(R.id.tvFechaHora)
        private val tvSintomas: TextView = itemView.findViewById(R.id.tvSintomas)
        private val ivMenu: ImageView = itemView.findViewById(R.id.ivMenuCita)
        private val viewUrgencia: View = itemView.findViewById(R.id.viewUrgencia)
        private val tvUrgenciaLabel: TextView = itemView.findViewById(R.id.tvUrgenciaPlaceholder)
        private val tvEstado: TextView = itemView.findViewById(R.id.tvEstadoCita)

        fun bind(cita: Cita, listener: CitaActionListener) {
            tvTitulo.text = cita.nombreDoctor
            tvEspecialidad.text = cita.especialidad ?: "General"
            
            // Urgencia: Corazón de color + Label
            val esUrgente = cita.nivelUrgencia == 3
            val tintColor = if (esUrgente) {
                itemView.context.getColor(android.R.color.holo_red_light)
            } else {
                itemView.context.getColor(android.R.color.holo_green_light)
            }
            viewUrgencia.backgroundTintList = android.content.res.ColorStateList.valueOf(tintColor)
            tvUrgenciaLabel.visibility = if (esUrgente) View.VISIBLE else View.GONE
            
            // Estado
            tvEstado.text = cita.estado ?: "RESERVADA"
            
            // Formatear fecha y hora
            val fechaHoraTexto = formatFechaHora(cita.fechaHoraCita)
            tvFechaHora.text = fechaHoraTexto
            
            // Mostrar síntomas
            tvSintomas.text = cita.sintomas ?: "Sin descripción"

            // Listener para el icono del menú
            ivMenu.setOnClickListener { view ->
                showPopupMenu(view, cita, listener)
            }
        }

        private fun formatFechaHora(fechaHoraISO: String?): String {
            if (fechaHoraISO == null) return "Fecha no disponible"
            
            return try {
                // Parsear formato ISO 8601
                val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val date = isoFormat.parse(fechaHoraISO)
                
                // Formatear para mostrar
                val displayFormat = SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault())
                displayFormat.format(date ?: Date())
            } catch (e: Exception) {
                "Fecha no válida"
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
    }
}
