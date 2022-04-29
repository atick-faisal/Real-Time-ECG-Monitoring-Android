package dev.atick.movesense.repository

import android.content.Context
import com.movesense.mds.Mds

class MovesenseImpl : Movesense {

    private var mds: Mds? = null

    override fun initialize(context: Context) {
        mds = Mds.builder().build(context)
    }
}