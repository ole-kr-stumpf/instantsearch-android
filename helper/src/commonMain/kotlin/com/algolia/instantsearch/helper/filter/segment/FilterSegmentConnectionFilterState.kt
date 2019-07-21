package com.algolia.instantsearch.helper.filter.segment

import com.algolia.instantsearch.core.connection.ConnectionImpl
import com.algolia.instantsearch.core.event.Callback
import com.algolia.instantsearch.helper.filter.state.FilterGroupID
import com.algolia.instantsearch.helper.filter.state.FilterState
import com.algolia.instantsearch.helper.filter.state.MutableFilters


internal class FilterSegmentConnectionFilterState(
    private val viewModel: FilterSegmentViewModel,
    private val filterState: FilterState,
    private val groupID: FilterGroupID
) : ConnectionImpl() {

    private val updateSelected: Callback<MutableFilters> = { filters ->
        viewModel.selected.value = viewModel.map.value.entries
            .find { it.value == filters.getFilters(groupID).firstOrNull() }?.key
    }
    private val updateFilterState: Callback<Int?> = { number ->
        filterState.notify {
            viewModel.map.value[viewModel.selected.value]?.let { remove(groupID, it) }
            if (viewModel.selected.value != number) {
                viewModel.map.value[number]?.let { add(groupID, it) }
            }
        }
    }

    override fun connect() {
        super.connect()
        filterState.filters.subscribePast(updateSelected)
        viewModel.eventSelection.subscribe(updateFilterState)
    }

    override fun disconnect() {
        super.disconnect()
        filterState.filters.unsubscribe(updateSelected)
        viewModel.eventSelection.unsubscribe(updateFilterState)
    }
}