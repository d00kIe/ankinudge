package com.teraculus.lingojournalandroid.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.teraculus.lingojournalandroid.R
import com.teraculus.lingojournalandroid.model.Note

class HomeFragment : Fragment() {

    private lateinit var noteListViewModel: NoteListViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        noteListViewModel =
                ViewModelProvider(this, NoteListViewModelFactory(this.requireActivity())).get(NoteListViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val notesAdapter = NotesAdapter { note -> adapterOnClick(note)}
        notesAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
//        val headerAdapter = HeaderAdapter()
//        val concatAdapter = ConcatAdapter(headerAdapter, notesAdapter)
        val recyclerView: RecyclerView = root.findViewById(R.id.entry_list)
        recyclerView.adapter = notesAdapter
        recyclerView.adapter?.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        noteListViewModel.notesLiveData.observe(this.requireActivity(), {
            it?.let {
                notesAdapter.submitList(it as MutableList<Note>)
                notesAdapter.notifyDataSetChanged()
            }
        })

        return root
    }

    private fun adapterOnClick(note: Note) {
        val action = HomeFragmentDirections.actionNavigationHomeToEntryDetails(note.id.toString())
        view?.findNavController()?.navigate(action)
    }
}