package lorry.folder.items.memogamma.persistence

import lorry.folder.items.memogamma.bubble.StylusState


data class GammaFile(val stylusState: StylusState) {
    companion object {
        val DEFAULT = GammaFile(StylusState(mutableListOf()))
    }

    fun isDefault() = this == DEFAULT
    
    
    
    
}