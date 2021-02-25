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
import com.teraculus.lingojournalandroid.model.Activity

class HomeFragment : Fragment() {

    private lateinit var activityListViewModel: ActivityListViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        activityListViewModel =
                ViewModelProvider(this, ActivityListViewModelFactory(this.requireActivity())).get(ActivityListViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val activitiesAdapter = ActivitiesAdapter { note -> adapterOnClick(note)}
        activitiesAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
//        val headerAdapter = HeaderAdapter()
//        val concatAdapter = ConcatAdapter(headerAdapter, notesAdapter)
        val recyclerView: RecyclerView = root.findViewById(R.id.entry_list)
        recyclerView.adapter = activitiesAdapter
        recyclerView.adapter?.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        activityListViewModel.activities.observe(this.requireActivity(), {
            it?.let {
                activitiesAdapter.submitList(it as MutableList<Activity>)
                activitiesAdapter.notifyDataSetChanged()
            }
        })

        return root
    }

    private fun adapterOnClick(activity: Activity) {
        val action = HomeFragmentDirections.actionNavigationHomeToEntryDetails(activity.id.toString())
        view?.findNavController()?.navigate(action)
    }
}