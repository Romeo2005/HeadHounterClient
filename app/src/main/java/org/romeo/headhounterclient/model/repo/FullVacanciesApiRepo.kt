package org.romeo.headhounterclient.model.repo

import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import org.romeo.headhounterclient.model.api.IRetrofitWorker
import org.romeo.headhounterclient.model.entity.vacancy.vacancy_full.VacancyFull

class FullVacanciesApiRepo(
    private val worker: IRetrofitWorker
) : IFullVacanciesRepo {

    override fun getVacancyFullByUrl(url: String): Single<VacancyFull> =
        worker.getVacancySingleByUrl(url).subscribeOn(Schedulers.io())
}