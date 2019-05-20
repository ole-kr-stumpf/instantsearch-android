package com.algolia.instantsearch.demo.filter.facet

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.algolia.instantsearch.core.selectable.list.SelectionMode
import com.algolia.instantsearch.demo.*
import com.algolia.instantsearch.helper.filter.facet.*
import com.algolia.instantsearch.helper.filter.facet.FacetSortCriterion.*
import com.algolia.instantsearch.helper.filter.state.FilterGroupID
import com.algolia.instantsearch.helper.filter.state.FilterState
import com.algolia.instantsearch.helper.searcher.SearcherSingleIndex
import com.algolia.search.model.Attribute
import com.algolia.search.model.IndexName
import com.algolia.search.model.filter.Filter
import kotlinx.android.synthetic.main.demo_facet_list.*
import kotlinx.android.synthetic.main.header_filter.*


class FacetListDemo : DemoActivity() {

    private val color = Attribute("color")
    private val promotions = Attribute("promotions")
    private val category = Attribute("category")
    private val colors
        get() = mapOf(
            color.raw to ContextCompat.getColor(this, android.R.color.holo_red_dark),
            promotions.raw to ContextCompat.getColor(this, android.R.color.holo_blue_dark),
            category.raw to ContextCompat.getColor(this, android.R.color.holo_green_dark)
        )

    private val groupIDColor = FilterGroupID.And(color)
    private val groupIDPromotions = FilterGroupID.And(promotions)
    private val groupIDCategory = FilterGroupID.Or(category)
    private val filterState = FilterState(
        facetGroups = mutableMapOf(
            FilterGroupID.And(color.raw) to setOf(Filter.Facet(color, "green"))
        )
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.demo_facet_list)

        val colorAViewModel = FacetListViewModel(selectionMode = SelectionMode.Single)
        val colorAPresenter = FacetListPresenter(listOf(IsRefined, AlphabeticalAscending), limit = 3)
        val colorAAdapter = FacetListAdapter()

        colorAViewModel.connectFilterState(color, filterState, groupIDColor)
        colorAViewModel.connectSearcher(color, searcher)
        colorAViewModel.connectView(colorAAdapter, colorAPresenter)

        val promotionViewModel = FacetListViewModel()
        val promotionPresenter = FacetListPresenter(listOf(CountDescending))
        val promotionAdapter = FacetListAdapter()

        promotionViewModel.connectFilterState(promotions, filterState, groupIDPromotions)
        promotionViewModel.connectSearcher(promotions, searcher)
        promotionViewModel.connectView(promotionAdapter, promotionPresenter)

        val categoryViewModel = FacetListViewModel()
        val categoryPresenter = FacetListPresenter(listOf(CountDescending, AlphabeticalAscending))
        val categoryAdapter = FacetListAdapter()

        categoryViewModel.connectFilterState(category, filterState, groupIDCategory)
        categoryViewModel.connectSearcher(category, searcher)
        categoryViewModel.connectView(categoryAdapter, categoryPresenter)

        configureRecyclerView(listTopLeft, colorAAdapter)
        configureRecyclerView(listTopRight, categoryAdapter)
        configureRecyclerView(listBottomLeft, promotionAdapter)

        titleTopLeft.text = formatTitle(colorAPresenter, groupIDColor)
        titleTopRight.text = formatTitle(categoryPresenter, groupIDCategory)
        titleBottomLeft.text = formatTitle(promotionPresenter, groupIDPromotions)

        onFilterChangedThenUpdateFiltersText(filterState, colors, filtersTextView)
        onClearAllThenClearFilters(filterState, filtersClearAll)
        onErrorThenUpdateFiltersText(searcher, filtersTextView)
        onResponseChangedThenUpdateNbHits(searcher, nbHits)
        configureToolbar()

        searcher.search()
    }

    override fun onDestroy() {
        super.onDestroy()
        searcher.cancel()
    }

    private fun FacetSortCriterion.format(): String {
        return when (this) {
            IsRefined -> name
            CountAscending -> "CountAsc"
            CountDescending -> "CountDesc"
            AlphabeticalAscending -> "AlphaAsc"
            AlphabeticalDescending -> "AlphaDesc"
        }
    }

    private fun formatTitle(presenter: FacetListPresenter, filterGroupID: FilterGroupID): String {

        val criteria = presenter.sortBy.joinToString("-") { it.format() }
        val operator = when (filterGroupID) {
            is FilterGroupID.And -> "And"
            is FilterGroupID.Or -> "Or"
        }

        return "$operator, $criteria, l=${presenter.limit}"
    }
}