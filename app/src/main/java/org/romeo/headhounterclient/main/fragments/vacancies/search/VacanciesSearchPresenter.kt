package org.romeo.headhounterclient.main.fragments.vacancies.search

import com.github.terrakok.cicerone.Router
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable
import moxy.MvpPresenter
import org.romeo.headhounterclient.dagger.module.MAIN_SCHEDULER_KEY
import org.romeo.headhounterclient.main.fragments.vacancies.list.IVacanciesListPresenter
import org.romeo.headhounterclient.main.fragments.vacancies.list.VacanciesListPresenter
import org.romeo.headhounterclient.model.entity.vacancy.vacancy_full.VacancyFull
import org.romeo.headhounterclient.model.entity.vacancy.vacancy_short.VacancyShort
import org.romeo.headhounterclient.model.repo.IFavoritesRepo
import org.romeo.headhounterclient.model.repo.IFullVacanciesRepo
import org.romeo.headhounterclient.model.repo.IShortVacanciesRepo
import org.romeo.headhounterclient.navigation.screens.IScreens
import javax.inject.Inject
import javax.inject.Named

class VacanciesSearchPresenter : MvpPresenter<VacanciesSearchView>(), IVacanciesSearchPresenter {

    @Inject
    lateinit var router: Router

    @Inject
    lateinit var shortVacanciesRepo: IShortVacanciesRepo

    @Inject
    @field:Named(MAIN_SCHEDULER_KEY)
    lateinit var mainScheduler: Scheduler

    @Inject
    lateinit var screens: IScreens

    @Inject
    lateinit var fullRepo: IFullVacanciesRepo

    @Inject
    lateinit var favoritesRepo: IFavoritesRepo

    override val listPresenter: IVacanciesListPresenter =
        VacanciesListPresenter().apply {
            onClick = { item ->
                val url = items[item.pos].url
                startVacancyFragment(url)
            }

            onStarClicked = { item ->
                val cur = items[item.pos]

                if (cur.isFavorite)
                    favoritesRepo.deleteFromFavorites(cur)
                        .observeOn(mainScheduler)
                        .subscribe {
                            item.setStarBorder()
                        }
                else {
                    favoritesRepo.addToFavorites(cur)
                        .observeOn(mainScheduler)
                        .subscribe {
                            item.setStarFilled()
                        }
                }

                cur.isFavorite = !cur.isFavorite
            }
        }

    private fun startVacancyFragment(url: String) {
        fullRepo.getVacancyFullByUrl(url)
            .observeOn(mainScheduler)
            .subscribe(object : SingleObserver<VacancyFull> {
                override fun onSubscribe(d: Disposable?) {
                    viewState.showLoading()
                }

                override fun onSuccess(t: VacancyFull?) {
                    t?.let { vacancy ->
                        router.navigateTo(
                            screens.getVacancyScreen(vacancy)
                        )
                        viewState.hideLoading()
                    }
                }

                override fun onError(e: Throwable?) {
                    viewState.showMessage(e?.message)
                    viewState.hideLoading()
                }
            })
    }

    override fun onFirstViewAttach() {
        viewState.initList()
    }

    override fun onSearchPressed(searchText: String): Boolean {
        viewState.showLoading()
        shortVacanciesRepo.getVacanciesSingleBySearch(searchText)
            .observeOn(mainScheduler)
            .subscribe({ list ->
                resetListItems(list)
                viewState.hideLoading()
            }, { e ->
                e.printStackTrace()
                viewState.showMessage(e.message)
                viewState.hideLoading()
            })
        return true
    }

    override fun onFavoritesPressed(): Boolean {
        router.navigateTo(
            screens.getVacanciesFavoritesScreen()
        )

        return true
    }

    override fun onBackPressed() {
        router.exit()
    }

    private fun resetListItems(items: List<VacancyShort>) {
        listPresenter.items.clear()
        listPresenter.items.addAll(items)
        viewState.updateList()
    }
}