package filter.numeric.comparison

import com.algolia.instantsearch.core.number.NumberView
import com.algolia.instantsearch.core.number.NumberViewModel
import com.algolia.instantsearch.core.number.decrement
import com.algolia.instantsearch.core.number.increment
import com.algolia.instantsearch.helper.filter.numeric.comparison.connectView
import shouldEqual
import kotlin.test.Test


class TestFilterComparisonConnectView {

    private class MockNumberView : NumberView<Int> {

        var onClickIncrement: (() -> Unit)? = null
        var onClickDecrement: (() -> Unit)? = null

        var int: Int? = null

        override fun setComputation(computation: ((Int?) -> Int?) -> Unit) {
            onClickIncrement = {
                computation.increment()
            }
            onClickDecrement = {
                computation.decrement()
            }
        }

        override fun setItem(item: Int?) {
            int = item
        }
    }

    @Test
    fun connectShouldCallSetSelectedAndSetItems() {
        val view = MockNumberView()
        val viewModel = NumberViewModel.Int(0 .. 10)

        viewModel.item = 5
        viewModel.connectView(view)
        view.int shouldEqual 5
    }

    @Test
    fun onClickShouldCallOnSelectionsComputed() {
        val view = MockNumberView()
        val viewModel = NumberViewModel.Int(0 .. 10)

        viewModel.onNumberComputed += { viewModel.item = it }
        viewModel.connectView(view)
        view.onClickIncrement!!()
        view.int shouldEqual 0
        view.onClickIncrement!!()
        view.int shouldEqual 1
        view.onClickDecrement!!()
        view.int shouldEqual 0
    }

    @Test
    fun onSelectedChangedShouldCallSetSelected() {
        val view = MockNumberView()
        val viewModel = NumberViewModel.Int(0 .. 10)

        viewModel.connectView(view)
        viewModel.item = 5
        view.int shouldEqual 5
    }
}