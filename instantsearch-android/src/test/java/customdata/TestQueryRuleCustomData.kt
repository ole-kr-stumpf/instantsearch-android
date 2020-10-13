package customdata

import com.algolia.instantsearch.helper.customdata.QueryRuleCustomDataViewModel
import com.algolia.instantsearch.helper.customdata.connectSearcher
import com.algolia.instantsearch.helper.searcher.SearcherMultipleIndex
import com.algolia.instantsearch.helper.searcher.SearcherSingleIndex
import com.algolia.search.model.IndexName
import com.algolia.search.model.multipleindex.IndexQuery
import com.algolia.search.model.response.ResponseSearch
import com.algolia.search.model.response.ResponseSearches
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import mockClient
import org.junit.Test
import shouldEqual

class TestQueryRuleCustomData {

    @Serializable
    internal data class TestModel(val number: Int, val text: String)

    private val client = mockClient()

    @Test
    fun testSingleIndexSearcherConnection() {
        val index = client.initIndex(IndexName("A"))
        val searcher = SearcherSingleIndex(index)
        val viewModel = QueryRuleCustomDataViewModel(TestModel.serializer())
        viewModel.connectSearcher(searcher).connect()
        val customData = TestModel(number = 10, text = "test")

        val userData = Json.encodeToJsonElement(TestModel.serializer(), customData).jsonObject
        searcher.response.value = ResponseSearch(userDataOrNull = listOf(userData))

        viewModel.item.value shouldEqual customData
    }

    @Test
    fun testMultiIndexSearcherConnection() {
        val indexA = IndexQuery(IndexName("IndexMovie"))
        val indexB = IndexQuery(IndexName("IndexActor"))
        val searcher = SearcherMultipleIndex(client, listOf(indexA, indexB))

        val viewModel = QueryRuleCustomDataViewModel(TestModel.serializer())
        viewModel.connectSearcher(searcher, 1).connect()
        val customData1 = TestModel(number = 10, text = "test1")
        val customData2 = TestModel(number = 20, text = "test2")

        val userData1 = Json.encodeToJsonElement(TestModel.serializer(), customData1).jsonObject
        val userData2 = Json.encodeToJsonElement(TestModel.serializer(), customData2).jsonObject

        searcher.response.value = ResponseSearches(
            listOf(
                ResponseSearch(userDataOrNull = listOf(userData1)),
                ResponseSearch(userDataOrNull = listOf(userData2))
            )
        )

        viewModel.item.value shouldEqual customData2
    }

    @Test
    fun testImplicitSerializer() {
        val explicit = QueryRuleCustomDataViewModel(TestModel.serializer())
        val implicit = QueryRuleCustomDataViewModel<TestModel>()

        explicit.serializer shouldEqual implicit.serializer
    }
}
