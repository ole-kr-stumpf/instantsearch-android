package com.algolia.instantsearch.demo.filter.numeric.comparison

import android.view.View
import com.algolia.instantsearch.core.number.NumberView
import com.algolia.instantsearch.core.number.decrement
import com.algolia.instantsearch.core.number.increment
import com.algolia.instantsearch.core.selectable.number.Computation
import com.algolia.search.model.Attribute
import com.algolia.search.model.filter.NumericOperator
import kotlinx.android.synthetic.main.demo_filter_comparison.view.*


class FilterPriceView(
    val view: View,
    val price: Attribute,
    val operator: NumericOperator
): NumberView<Int> {

    init {
        view.stepperText.text = "$price ${operator.raw}"
    }

    override fun setComputation(computation: Computation<Int>) {
        view.arrowUp.setOnClickListener {
            computation.increment()
        }
        view.arrowDown.setOnClickListener {
            computation.decrement()
        }
    }

    override fun setItem(item: Int?) {
        view.stepperNumber.text = item?.toString() ?: "-"
    }
}