package co.touchlab.kampstarter

import co.touchlab.kampstarter.db.Breed
import co.touchlab.kampstarter.models.BreedModel
import co.touchlab.kampstarter.models.ItemDataSummary
import co.touchlab.kermit.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import co.touchlab.stately.ensureNeverFrozen


class NativeCoroutineAdapter(
    private val viewUpdate: (ItemDataSummary) -> Unit,
    errorUpdate: (String) -> Unit
) {

    val log = Kermit(CommonLogger())
    private val scope = MainScope(Dispatchers.Main, log)
    private val breedModel:BreedModel

    init {
        ensureNeverFrozen()
        breedModel = BreedModel(errorUpdate)
        observeBreeds()
    }

    private fun observeBreeds() {
        scope.launch {
            log.v { "Observe Breeds" }
            breedModel.selectAllBreeds()
                .flowOn(Dispatchers.Default)
                .collect { summary ->
                    log.v { "Collecting Things" }
                    viewUpdate(summary)
                }
        }
    }

    fun getBreedsFromNetwork() {
        scope.launch {
            breedModel.getBreedsFromNetwork()
        }
    }
    fun updateBreedFavorite(breed: Breed){
        scope.launch {
            breedModel.updateBreedFavorite(breed)
        }
    }

    fun onDestroy() {
        scope.onDestroy()
        breedModel.onDestroy()
    }
}