package org.romeo.headhounterclient.model.api

import io.reactivex.rxjava3.core.Single
import org.romeo.headhounterclient.model.entity.vacancy.vacancy_full.VacancyFull
import org.romeo.headhounterclient.model.entity.vacancy.vacancy_short.VacancyShort

class RetrofitWorker(private val service: RetrofitService) : IRetrofitWorker {
    override fun getVacanciesSingleBySearch(search: String): Single<List<VacancyShort>> {
        val args = mapOf(SEARCH_QUERY_KEY to search)
        return service.getVacancies(args).map {
            it.items
        }
    }

    override fun getVacancySingleByUrl(url: String): Single<VacancyFull> =
        service.getVacancy(url)

}