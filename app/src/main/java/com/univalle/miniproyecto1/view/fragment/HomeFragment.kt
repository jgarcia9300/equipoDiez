package com.univalle.miniproyecto1.view.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.univalle.miniproyecto1.R
import com.univalle.miniproyecto1.databinding.FragmentHomeBinding
import com.univalle.miniproyecto1.view.LoginActivity
import com.univalle.miniproyecto1.view.adapter.InventoryAdapter
import com.univalle.miniproyecto1.viewmodel.InventoryViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val inventoryViewModel: InventoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        controladores()
        observadorViewModel()
    }

    override fun onResume() {
        super.onResume()
        // Refrescar la lista cuando el fragment se vuelve visible
        // Esto asegura que se muestren los productos recién agregados
        inventoryViewModel.getListInventory()
    }

    private fun controladores() {
        binding.fbagregar.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_addItemFragment)
        }

        binding.contentToolbar.imageToolbarHome.setOnClickListener {
            // Limpiar SharedPreferences
            val sharedPreferences = requireActivity().getSharedPreferences("shared", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()

            // Cerrar sesión en Firebase
            inventoryViewModel.signOut()

            // Navegar al Login
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }
    }

    private fun observadorViewModel() {
        // Inicializar RecyclerView
        val recycler = binding.recyclerview
        val layoutManager = LinearLayoutManager(context)
        recycler.layoutManager = layoutManager

        // Adapter con lista vacia
        val adapter = InventoryAdapter(emptyList(), findNavController())
        recycler.adapter = adapter

        // Configurar clic en ítem
        adapter.onClickItem = { producto ->
            val bundle = Bundle()
            bundle.putSerializable("dataInventory", producto)
            findNavController().navigate(
                R.id.action_homeFragment_to_itemDetailsFragment,
                bundle
            )
        }

        // Observar datos del ViewModel
        inventoryViewModel.listInventory.observe(viewLifecycleOwner){ listInventory ->
            adapter.updateList(listInventory)
        }

        // Observar barra de carga
        inventoryViewModel.progresState.observe(viewLifecycleOwner) { status ->
            binding.progress.isVisible = status
        }
    }
}