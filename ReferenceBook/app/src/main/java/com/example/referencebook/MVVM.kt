package com.example.referencebook

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class MVVM: ViewModel() {
    private val _newsFeed = MutableStateFlow(
        listOf(
            Data(R.drawable.liquid, "Team liquid одержала победу на The international 2024", 0),
            Data(R.drawable.anton, "Антона Dyrachyo Шкредов, покинул команду GAIMIN GLRADIATORS", 0),
            Data(R.drawable.ramzes, "Игрок Ramzes666, после провального выступления на турнире не будет играть следующий сезон", 0),
            Data(R.drawable.esl, "CEO организатор Esl One анонсировал ближайший турнир в Тайланде", 0),
            Data(R.drawable.coppenhagen, "В мерче Secret Shop была допущена ошибка в слове Copenhagen", 0),
            Data(R.drawable.puppey, "Puppey вернулся назад в Navi, на роль стендин", 0),
            Data(R.drawable.notail, "N0tail выпустит книгу о своей жизни и карьере в Dota 2", 0),
            Data(R.drawable.collapse, "Менеджер BetBoom Team мог намекнуть на подписание Collapse", 0),
            Data(R.drawable.noone, "«Бывший» состав Cloud9 зарегистрировался под тегом BandaNoone на FACEIT", 0),
            Data(R.drawable.fisman, "Fishman потратил более $100 тыс. на токены Hamster Kombat", 0),
        )
    )
    private val _currentNews = MutableStateFlow(_newsFeed.value.take(4))
    val currentNews: StateFlow<List<Data>> = _currentNews

    init {
        CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                try {
                    delay(5000)
                    updateRandomNews()
                } catch (e: Exception) {
                    println(e)
                }
            }
        }
    }

    fun getLikes(i: Int) {
        val updatedCurrentNews = _currentNews.value.toMutableList()
        val updatedItem = updatedCurrentNews[i].copy(likes = updatedCurrentNews[i].likes + 1)
        updatedCurrentNews[i] = updatedItem
        _currentNews.value = updatedCurrentNews
        val indexInFeed = _newsFeed.value.indexOfFirst { it.title == updatedItem.title }

        if (indexInFeed != -1) {
            val updatedNewsFeed = _newsFeed.value.toMutableList()
            updatedNewsFeed[indexInFeed] = updatedItem
            _newsFeed.value = updatedNewsFeed
        }
    }

    private fun updateRandomNews() {
        val currentNewsList = _currentNews.value.toMutableList()
        val availableNews = _newsFeed.value.filter { it !in currentNewsList }
        if (availableNews.isNotEmpty()) {
            val randomIndex = Random.nextInt(0, 4)
            val randomNews = availableNews.random()
            currentNewsList[randomIndex] = randomNews
            _currentNews.value = currentNewsList
        }
    }
}