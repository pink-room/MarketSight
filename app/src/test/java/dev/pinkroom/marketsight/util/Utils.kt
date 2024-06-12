package dev.pinkroom.marketsight.util

import dev.pinkroom.marketsight.data.local.DbTransaction
import io.mockk.coEvery
import io.mockk.slot

suspend fun DbTransaction.mockAndExecuteTransaction() {
    val data = this
    val transactionLambda = slot<suspend () -> Any>()
    coEvery { data(capture(transactionLambda)) } coAnswers {
        transactionLambda.captured.invoke()
    }
}